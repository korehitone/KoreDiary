package com.syntxr.korediary.data.source.remote.serializable

import com.syntxr.korediary.data.source.local.PostEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("id") val id : Int? = null,
    @SerialName("uuid") val uuid: String,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("value") val value: String,
    @SerialName("mood") val mood: String,
    @SerialName("published") val published : Boolean = false,
    @SerialName("created_at") val createdAt: String
)
{
    fun toPostEntity() = PostEntity(
        uuid = uuid,
        title = title,
        value = value,
        mood = mood,
        userId = userId,
        createdAt = createdAt
    )
}