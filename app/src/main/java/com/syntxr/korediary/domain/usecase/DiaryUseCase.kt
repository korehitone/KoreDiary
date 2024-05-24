package com.syntxr.korediary.domain.usecase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.domain.model.Post
import com.syntxr.korediary.domain.repository.DiaryRepository
import com.syntxr.korediary.utils.DiaryOrder
import com.syntxr.korediary.utils.Network
import com.syntxr.korediary.utils.OrderBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiaryUseCase @Inject constructor(
    private val repository: DiaryRepository,
    private val context: Context,
) {

    suspend fun draft(): Flow<ResponseState<List<Post>>> = flow {
        if (Network.checkConnectivity(context)) {
            emit(ResponseState.Loading)
            try {
                repository.draft().onSuccess { flow ->
                    emitAll(
                        flow.map {
                            ResponseState.Success(it)
                        }
                    )
                }.onFailure {
                    emit(ResponseState.Error(it.message.toString()))
                    Log.d("AAAAAA4", "fetch: ${it.message}")
                }
            } catch (e: Exception) {
                emit(ResponseState.Error(e.message.toString()))
                Log.d("AAAAAA3", "fetch: ${e.message}")
            }
        } else {
            emit(ResponseState.Error("Make sure you have connection", emptyList()))
        }
    }

    suspend fun fetch(diaryOrder: DiaryOrder = DiaryOrder.Date, orderBy: OrderBy = OrderBy.Descending): Flow<ResponseState<List<Post>>> = flow {
        if (Network.checkConnectivity(context)) {
            emit(ResponseState.Loading)
            try {
                repository.fetch().onSuccess { flow ->
                    val data = flow.map { posts ->
                        when (orderBy) {
                            OrderBy.Ascending -> {
                                when (diaryOrder) {
                                    DiaryOrder.Date -> posts.sortedBy { it.createdAt }
                                    DiaryOrder.Mood -> posts.sortedBy { it.mood.lowercase() }
                                    DiaryOrder.Title -> posts.sortedBy { it.title.lowercase() }
                                }
                            }

                            OrderBy.Descending -> {
                                when (diaryOrder) {
                                    DiaryOrder.Date -> posts.sortedByDescending { it.createdAt }
                                    DiaryOrder.Mood -> posts.sortedByDescending { it.mood.lowercase() }
                                    DiaryOrder.Title -> posts.sortedByDescending { it.title.lowercase() }
                                }
                            }
                        }
                    }
                    emitAll(
                        data.map {
                            ResponseState.Success(it)
                        }
                    )
                }.onFailure {
                    emit(ResponseState.Error(it.message.toString()))
                    Log.d("AAAAAA2", "fetch: ${it.message}")
                }
            } catch (e: Exception) {
                emit(ResponseState.Error(e.message.toString()))
                Log.d("AAAAAA1", "fetch: ${e.message}")

            }
        } else {
            emit(ResponseState.Error("Make sure you have connection"))
        }
    }


    fun getFavourite(
        diaryOrder: DiaryOrder = DiaryOrder.Date, orderBy: OrderBy = OrderBy.Descending,
    ): Flow<List<Post>> = repository.favorite().map { posts ->
        when (orderBy) {
            OrderBy.Ascending -> {
                when (diaryOrder) {
                    DiaryOrder.Date -> posts.sortedBy { it.createdAt }
                    DiaryOrder.Mood -> posts.sortedBy { it.mood.lowercase() }
                    DiaryOrder.Title -> posts.sortedBy { it.title.lowercase() }
                }
            }

            OrderBy.Descending -> {
                when (diaryOrder) {
                    DiaryOrder.Date -> posts.sortedByDescending { it.createdAt }
                    DiaryOrder.Mood -> posts.sortedByDescending { it.mood.lowercase() }
                    DiaryOrder.Title -> posts.sortedByDescending { it.title.lowercase() }
                }
            }
        }
    }

    suspend fun unsubscribe() = repository.unsubscribe() // unsubsrcibe realtime


    suspend fun updateDiary(uuid: String, title: String, value: String, mood: String) =
        repository.update(uuid, title, value, mood) // update ke supabase

    suspend fun insertDiary(postDto: PostDto) =  // insert ke supabase
        repository.insertPost(postDto)

    suspend fun insertDraft(postDto: PostDto) = repository.insertDraft(postDto)
    suspend fun updateDraft(uuid: String, publish : Boolean) = repository.publish(uuid, publish)
    suspend fun deleteDiary(uuid: String) = repository.deletePost(uuid) // delete ke supabase

    suspend fun deleteAllDiary() = repository.deleteAllPost() // delete semua data yang dimiliki user

    suspend fun deleteAllDraft() = repository.deleteAllDraft()
    suspend fun search(query: String) = repository.search(query) // buat search data

    suspend fun insertFavorite(postDto: PostDto) = repository.insertFavorite(postDto)
    suspend fun deleteFavorite(uuid: String) = repository.deleteFavorite(uuid)
    fun deleteFavoriteAll() = repository.deleteFavoriteAll()
}