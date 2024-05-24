package com.syntxr.korediary.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.utils.KEY_DATE
import com.syntxr.korediary.utils.KEY_MOOD
import com.syntxr.korediary.utils.KEY_PUBLISH
import com.syntxr.korediary.utils.KEY_TITLE
import com.syntxr.korediary.utils.KEY_TITLE_NOTIFY
import com.syntxr.korediary.utils.KEY_TXT_NOTIFY
import com.syntxr.korediary.utils.KEY_USER
import com.syntxr.korediary.utils.KEY_UUID
import com.syntxr.korediary.utils.KEY_VALUE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class InsertDataWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val client: SupabaseClient, // karena membutuhkan supabase, kita inject saja
) : CoroutineWorker(appContext, params) {

    private val workManager = WorkManager.getInstance(appContext) //deklarasi work manager agar bisa menggunakan noticifation worker
    private val constraint = Constraints.Builder() // membuat constraint untuk notication worker
        .setRequiredNetworkType(NetworkType.CONNECTED) // mengatur agar worker hanya dijalankan ketika tersambung ke internet
    private val notifyBuilder = OneTimeWorkRequestBuilder<NotificationWorker>()
    // val ini berisi Notification Worker
    // OneTimeWorkRequest berarti kita hanya akan menjalankan worker ini sekali, bukan selama periode tertentu

    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO){ // menggunakan Dispatcher.IO agar berjalan di background Thread atau latar belakang

            val postDto = PostDto( // membuat PostDto dengan data yang didapat dari params melalui inputData
                uuid = inputData.getString(KEY_UUID).toString(),
                userId = inputData.getString(KEY_USER).toString(),
                value = inputData.getString(KEY_VALUE).toString(),
                mood = inputData.getString(KEY_MOOD).toString(),
                title = inputData.getString(KEY_TITLE).toString(),
                published = inputData.getBoolean(KEY_PUBLISH, true),
                createdAt = inputData.getString(KEY_DATE).toString()
            )

            return@withContext try { // jangan lupa try catch biar tidak crash
                delay(3000) // delayselama 3 detik

                client.from("posts").insert( // insert ke supabase
                    postDto
                )

                notifyBuilder.setInputData( // mengirim title dan text yang dibutuhkan di notifcation worker dengan  Data.Builder
                    Data.Builder()
                        .putString(KEY_TITLE_NOTIFY, "Insert Post") // title
                        .putString(KEY_TXT_NOTIFY, "Successfully insert your post") // text
                        .build() // jangan lupa ini
                )

                notifyBuilder.setConstraints(constraint.build()) // memasukkan constraint ke notifyBuilder

                Result.success().apply { // kalo succes, menjalankan work manager agar notification worker berjalan
                    workManager.enqueueUniqueWork(
                        postDto.uuid, // worker perlu id dengan tipe data string yang unik
                        ExistingWorkPolicy.APPEND_OR_REPLACE, // kalo ada worker yang sama akan direplace
                        notifyBuilder.build() // jangan lupa dibuild val notifyBuildernya
                    )
                }
            } catch (e : Exception){ //catch
                if (runAttemptCount >= 5) { // kalo worker ini dijalankan sampai atau lebih dari lima kali
                    notifyBuilder.setInputData(
                        Data.Builder()
                            .putString(KEY_TITLE_NOTIFY, "Insert Post")
                            .putString(KEY_TXT_NOTIFY, "Failed insert your post").build()
                    )

                    notifyBuilder.setConstraints(constraint.build())
                    Result.failure().apply { // kalo gagal menjalankan...
                        workManager.enqueueUniqueWork(
                            postDto.uuid,
                            ExistingWorkPolicy.APPEND_OR_REPLACE,
                            notifyBuilder.build()
                        )
                    }
                } // batas akhir run attempt count
                Result.retry() // coba ulang
            }
        }
    }

}