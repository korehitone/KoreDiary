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
import com.syntxr.korediary.data.kotpref.LocalUser
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.utils.KEY_TITLE_NOTIFY
import com.syntxr.korediary.utils.KEY_TXT_NOTIFY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class DeleteAllDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val client: SupabaseClient,
) : CoroutineWorker(appContext, params) {

    private val workManager = WorkManager.getInstance(appContext)
    private val constraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
    private val notifyBuilder = OneTimeWorkRequestBuilder<NotificationWorker>()


    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val uuid = LocalUser.uuid
            return@withContext try {
                delay(2000)
                client.from("posts").delete {
                    filter {
                        PostDto::userId eq uuid
                        PostDto::published eq true
                    }
                }

                notifyBuilder.setInputData(
                    Data.Builder()
                        .putString(KEY_TITLE_NOTIFY, "Delete All Post")
                        .putString(KEY_TXT_NOTIFY, "Successfully delete all your post").build()
                )
                notifyBuilder.setConstraints(constraint.build())

                Result.success().apply {
                    workManager.enqueueUniqueWork(
                        uuid,
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        notifyBuilder.build()
                    )
                }
            } catch (e: Exception) {
                if (runAttemptCount >= 5){
                    notifyBuilder.setInputData(
                        Data.Builder()
                            .putString(KEY_TITLE_NOTIFY, "Delete All Post")
                            .putString(KEY_TXT_NOTIFY, "Failed delete your post").build()
                    )
                    notifyBuilder.setConstraints(constraint.build())
                    Result.failure().apply {
                        workManager.enqueueUniqueWork(
                            uuid,
                            ExistingWorkPolicy.APPEND_OR_REPLACE,
                            notifyBuilder.build()
                        )
                    }
                }
                Result.retry()
            }
        }
    }

}