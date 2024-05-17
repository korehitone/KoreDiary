package com.syntxr.korediary.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syntxr.korediary.domain.model.Post

@Entity("post")
data class PostEntity (
    @PrimaryKey
    val uuid : String,
    val title : String,
    val value : String,
    val mood : String,
    val userId : String,
    val createdAt : String,
)
{
    fun toPost() = Post(
        uuid = uuid,
        title = title,
        value = value,
        mood = mood,
        userId = userId,
        createdAt = createdAt
    )
}