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
import com.syntxr.korediary.data.source.remote.serializable.User
import com.syntxr.korediary.utils.KEY_EMAIL
import com.syntxr.korediary.utils.KEY_NAME
import com.syntxr.korediary.utils.KEY_PASSWORD
import com.syntxr.korediary.utils.KEY_TITLE_NOTIFY
import com.syntxr.korediary.utils.KEY_TXT_NOTIFY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.put

@HiltWorker
class UpdateUserWorker @AssistedInject constructor(
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
            return@withContext try {
                delay(2000)

                val name = inputData.getString(KEY_NAME).toString()
                val email = inputData.getString(KEY_EMAIL).toString()
                val password = inputData.getString(KEY_PASSWORD).toString()

                client.auth.signInWith(Email){ // karena update ke auth butuh signin
                    this.email = LocalUser.email
                    this.password = LocalUser.password
                }

                client.auth.updateUser { // updat data ke auth
                    this.email = email
                    this.password = password
                    this.data {
                        put("username", name)
                    }
                }

                client.from("users").update( // update username ke user
                    update = {
                        User::username setTo name
                    },
                    request = { // filter apakah ada user yang memiliki uuid yang sesuai ?
                        filter {
                            User::uuid eq LocalUser.uuid
                        }
                    }
                )
                LocalUser.apply { // simpan perubahan ke local
                    username = name
                    this.email = email
                    this.password = password
                }

                notifyBuilder.setInputData(
                    Data.Builder()
                        .putString(KEY_TITLE_NOTIFY, "Update Profile")
                        .putString(KEY_TXT_NOTIFY, "Successfully update your profile").build()
                )
                notifyBuilder.setConstraints(constraint.build())


                Result.success().apply {
                    workManager.enqueueUniqueWork(
                        LocalUser.uuid,
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        notifyBuilder.build()
                    )
                }
            } catch (e: Exception) {
                if (runAttemptCount >= 5){
                    notifyBuilder.setInputData(
                        Data.Builder()
                            .putString(KEY_TITLE_NOTIFY, "Update Profile")
                            .putString(KEY_TXT_NOTIFY, "Failed update your profile").build()
                    )
                    notifyBuilder.setConstraints(constraint.build())
                    Result.failure().apply {
                        workManager.enqueueUniqueWork(
                            LocalUser.uuid,
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