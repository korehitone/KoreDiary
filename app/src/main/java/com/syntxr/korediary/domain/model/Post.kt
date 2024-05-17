package com.syntxr.korediary.domain.model

import com.syntxr.korediary.data.source.remote.serializable.PostDto

data class Post (
    val uuid : String,
    val title : String,
    val value : String,
    val mood : String,
    val userId : String,
    val createdAt : String,
) {
    fun toPostDto() = PostDto(
        uuid = uuid,
        title = title,
        value = value,
        mood = mood,
        userId = userId,
        createdAt = createdAt
    )
}