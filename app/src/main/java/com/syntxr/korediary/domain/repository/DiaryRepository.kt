package com.syntxr.korediary.domain.repository

import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {

    suspend fun unsubscribe()

    fun getLocal(): Flow<List<Post>>

    suspend fun fetch(): Result<Flow<List<Post>>>

    fun upsert(posts : List<PostDto>)

    suspend fun update(uuid: String, title: String, value: String, mood: String)

    suspend fun insert(postDto: PostDto)

    suspend fun delete(uuid: String)

    fun search(query: String) : Flow<List<Post>>

    suspend fun deleteAll()

   fun clear()
}