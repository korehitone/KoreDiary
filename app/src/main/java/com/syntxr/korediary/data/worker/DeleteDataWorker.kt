package com.syntxr.korediary.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.syntxr.korediary.data.source.local.DiaryDatabase
import com.syntxr.korediary.domain.model.Post
import com.syntxr.korediary.utils.KEY_UUID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class DeleteDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val client: SupabaseClient,
    private val db: DiaryDatabase
) : CoroutineWorker(appContext, params){


    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO){
            val uuid = inputData.getString(KEY_UUID).toString()
            return@withContext try {

                delay(3000)

                client.from("posts").delete {
                    filter {
                        Post::uuid eq uuid
                    // memeriksa apakah ada  post dengan uuid yang sesuai
                    }
                }

                Result.success()
            } catch (e : Exception){
                db.dao.delete(uuid)
                Result.retry()
            }
        }
    }

}