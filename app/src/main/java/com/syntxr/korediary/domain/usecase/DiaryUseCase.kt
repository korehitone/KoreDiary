package com.syntxr.korediary.domain.usecase

import android.content.Context
import android.util.Log
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

    private suspend fun fetch(
        diaryOrder: DiaryOrder = DiaryOrder.Date, orderBy: OrderBy = OrderBy.Descending,
        // untuk fitur mengurutkan data

    ): Result<Flow<List<Post>>> =
        repository.fetch().map { flow ->
            flow.map { list ->
                when (orderBy) { // kalau ordernya
                    OrderBy.Ascending -> { // naik
                        when (diaryOrder) { // urut sesuai
                            DiaryOrder.Date -> list.sortedBy { it.createdAt } // tanggal
                            DiaryOrder.Mood -> list.sortedBy { it.mood.lowercase() } // mood
                            DiaryOrder.Title -> list.sortedBy { it.title.lowercase() }// judul
                        }
                    }

                    OrderBy.Descending -> { // turun
                        when (diaryOrder) {
                            DiaryOrder.Date -> list.sortedByDescending { it.createdAt }
                            DiaryOrder.Mood -> list.sortedByDescending { it.mood.lowercase() }
                            DiaryOrder.Title -> list.sortedByDescending { it.title.lowercase() }
                        }
                    }
                }
            }
        }


    private fun getLocal(
        diaryOrder: DiaryOrder = DiaryOrder.Date, orderBy: OrderBy = OrderBy.Descending,
    ): Flow<List<Post>> = repository.getLocal().map { posts ->
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

    suspend fun get(
        diaryOrder: DiaryOrder = DiaryOrder.Date, orderBy: OrderBy = OrderBy.Descending,
    ): Flow<ResponseState<List<Post>>> = flow { // return flow
        emit(ResponseState.Loading) // karena pakai flow maka harus pakai emit, ingat. // loading
        if (Network.checkConnectivity(context)) { // check internet
            try {
                fetch(diaryOrder, orderBy) // dapat data dari remote dan di urutkan
                    .onSuccess { flow -> // kalo success
                        emitAll(
                            flow.map { list ->
                                clear() // hapus semua data di local
                                insertLocal(list.map { it.toPostDto() }) // input data remote ke local
                                ResponseState.Success(list) // kembalikan sebagai success dengan data
                            }
                        )

                    }.onFailure { e -> // kalo gagal / failure
                        emitAll(
                            getLocal(diaryOrder, orderBy).map {
                                ResponseState.Error(e.message.toString(), it) // kembalikan sebagai error dengan data dan message
                            }
                        )
                    }
            } catch (e: Exception) { // kalo proses fetch() bermasalah
                emitAll(
                    getLocal(diaryOrder, orderBy).map {
                        ResponseState.Error(e.message.toString(), it)
                    }
                )
            }
        } else { // kalo tidak ada internet
            emitAll(
                getLocal(diaryOrder, orderBy).map {
//                    Log.d("FUWAFUWA", "get: $it") // abaikan
                    ResponseState.Error("Make sure you have connection", it)
                }
            )
        }
    }


    suspend fun unsubscribe() = repository.unsubscribe() // unsubsrcibe realtime

    private fun insertLocal(posts: List<PostDto>) = repository.upsert(posts) // insert ke local

    suspend fun updateDiary(uuid: String, title: String, value: String, mood: String) =
        repository.update(uuid, title, value, mood) // update ke supabase

    suspend fun insertDiary(postDto: PostDto) =  // insert ke supabase
        repository.insert(postDto)

    suspend fun deleteDiary(uuid: String) = repository.delete(uuid) // delete ke supabase

    suspend fun deleteAllDiary() = repository.deleteAll() // delete semua data yang dimiliki user

    fun search(query: String) = repository.search(query) // buat search data

    private fun clear() = repository.clear()
}