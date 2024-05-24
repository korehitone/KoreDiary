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
class InsertDraftWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val client: SupabaseClient,
) : CoroutineWorker(appContext, params) {

    private val workManager = WorkManager.getInstance(appContext)
    private val constraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
    private val notifyBuilder = OneTimeWorkRequestBuilder<NotificationWorker>()


    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val postDto = PostDto(
                uuid = inputData.getString(KEY_UUID).toString(),
                userId = inputData.getString(KEY_USER).toString(),
                value = inputData.getString(KEY_VALUE).toString(),
                mood = inputData.getString(KEY_MOOD).toString(),
                title = inputData.getString(KEY_TITLE).toString(),
                published = inputData.getBoolean(KEY_PUBLISH, false),
                createdAt = inputData.getString(KEY_DATE).toString()
            )

            return@withContext try {
                delay(3000)

                client.from("posts").insert(
                    postDto
                )

                notifyBuilder.setInputData(
                    Data.Builder()
                        .putString(KEY_TITLE_NOTIFY, "Draft Post")
                        .putString(KEY_TXT_NOTIFY, "Successfully draft your post")
                        .build()
                )

                notifyBuilder.setConstraints(constraint.build())
                Result.success().apply {
                    workManager.enqueueUniqueWork(
                        postDto.uuid,
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        notifyBuilder.build()
                    )
                }
            } catch (e: Exception) {
                if (runAttemptCount >= 5) {
                    notifyBuilder.setInputData(
                        Data.Builder()
                            .putString(KEY_TITLE_NOTIFY, "Draft Post")
                            .putString(KEY_TXT_NOTIFY, "Failed draft your post").build()
                    )

                    notifyBuilder.setConstraints(constraint.build())
                    Result.failure().apply {
                        workManager.enqueueUniqueWork(
                            postDto.uuid,
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