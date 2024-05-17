package com.syntxr.korediary.data.source.remote.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id : Int,
    @SerialName("uuid") val uuid : String,
    @SerialName("username") val username : String,
    @SerialName("created_at") val createdAt : String
)
