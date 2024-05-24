package com.syntxr.korediary.domain.repository

import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    suspend fun unsubscribe()

    fun favorite(): Flow<List<Post>>

    suspend fun draft(): Result<Flow<List<Post>>>

    suspend fun fetch(): Result<Flow<List<Post>>>
    suspend fun update(uuid: String, title: String, value: String, mood: String)
    suspend fun publish(uuid: String, publish : Boolean)

    suspend fun insertPost(postDto: PostDto)
    suspend fun insertDraft(postDto: PostDto)
    suspend fun insertFavorite(postDto: PostDto)

    suspend fun deletePost(uuid: String)
    suspend fun deleteFavorite(uuid: String)

    suspend fun search(query: String): Flow<List<Post>>

    suspend fun deleteAllPost()

    suspend fun deleteAllDraft()

    fun deleteFavoriteAll()
}