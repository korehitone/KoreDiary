package com.syntxr.korediary.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.syntxr.korediary.data.kotpref.LocalUser
import com.syntxr.korediary.data.source.local.DiaryDao
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.data.worker.DeleteAllDataWorker
import com.syntxr.korediary.data.worker.DeleteDataWorker
import com.syntxr.korediary.data.worker.InsertDataWorker
import com.syntxr.korediary.data.worker.UpdateDataWorker
import com.syntxr.korediary.domain.model.Post
import com.syntxr.korediary.domain.repository.DiaryRepository
import com.syntxr.korediary.utils.KEY_MOOD
import com.syntxr.korediary.utils.KEY_TITLE
import com.syntxr.korediary.utils.KEY_UUID
import com.syntxr.korediary.utils.KEY_VALUE
import com.syntxr.korediary.utils.toDataWorker
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class DiaryRepoImpl @Inject constructor(
    context: Context,
    private val client: SupabaseClient,
    private val dao: DiaryDao,
) : DiaryRepository {

    private val diaryChannel = client.channel("diaries")

    private val workManager = WorkManager.getInstance(context)
    private val constraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)

    override suspend fun unsubscribe() {
        diaryChannel.unsubscribe()
        client.realtime.removeChannel(diaryChannel)
    }

    override fun getLocal(): Flow<List<Post>> =
        dao.getStoredData().map { list -> list.map { it.toPost() } }

    override suspend fun fetch(): Result<Flow<List<Post>>> { // mengembalikan ke result
            val data = diaryChannel.postgresListDataFlow(
                schema = "public",
                table = "posts",
                primaryKey = PostDto::id,
                filter = FilterOperation( // filter, karena hanya mengambil post yang dimiliki user saat ini
                    column = "user_id",
                    operator = FilterOperator.EQ,
                    value = LocalUser.uuid
                )
            ).flowOn(Dispatchers.IO)
            diaryChannel.subscribe() // subscribe ke channel yang dibuat

            return Result.success(data.map {  list ->
                // pakai ekstensi toPost() karena perlu List<Post>
                list.map { it.toPostEntity().toPost() }
            })
    }

    override fun upsert(posts: List<PostDto>) {
        CoroutineScope(Dispatchers.IO).launch{
            dao.upsert(posts.map { it.toPostEntity() })
        }
    }

    override suspend fun update(
        uuid: String,
        title: String,
        value: String,
        mood: String,
    ) {
        val updateBuilder = OneTimeWorkRequestBuilder<UpdateDataWorker>()

        updateBuilder.setInputData( // kirim data ke worker
            Data.Builder()
                .putString(KEY_UUID, uuid)
                .putString(KEY_TITLE, title)
                .putString(KEY_VALUE, value)
                .putString(KEY_MOOD, mood).build()
        )
        updateBuilder.setConstraints(constraint.build())

        val updateRequest = updateBuilder.build()

        workManager.enqueueUniqueWork(
            uuid,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            updateRequest
        )
    }

    override suspend fun insert(postDto: PostDto) {

        val insertBuilder = OneTimeWorkRequestBuilder<InsertDataWorker>()
        insertBuilder.setInputData(postDto.toDataWorker()) // postDto di convert ke Data dengan to DataWorker
        insertBuilder.setConstraints(constraint.build())

        val insertRequest = insertBuilder.build()

        workManager.enqueueUniqueWork(
            postDto.uuid,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            insertRequest
        )
    }

    override suspend fun delete(uuid: String) {

        val deleteBuilder = OneTimeWorkRequestBuilder<DeleteDataWorker>()
        deleteBuilder.setInputData(Data.Builder().putString(KEY_UUID, uuid).build())
        deleteBuilder.setConstraints(constraint.build())

        val deleteRequest = deleteBuilder.build()

        workManager.enqueueUniqueWork(
            uuid,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            deleteRequest
        )

    }

    override fun search(query: String) =
        dao.search(query).map { list -> list.map { it.toPost() } }

    override suspend fun deleteAll() {
        val deleteBuilder = OneTimeWorkRequestBuilder<DeleteAllDataWorker>()
        deleteBuilder.setConstraints(constraint.build())

        val deleteRequest = deleteBuilder.build()

        workManager.enqueueUniqueWork(
            "${LocalUser.username}_${LocalUser.uuid}",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            deleteRequest
        )
    }

    override fun clear() {CoroutineScope(Dispatchers.IO).launch{dao.clear() } }
}