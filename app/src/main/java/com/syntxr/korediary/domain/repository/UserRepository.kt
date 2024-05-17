package com.syntxr.korediary.domain.repository

import com.rmaprojects.apirequeststate.ResponseState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    //    logika ketika register
    suspend fun register(
        email: String,
        password: String,
        username: String
    ): Flow<ResponseState<Boolean>>

    //    logika ketika login
    suspend fun login(email: String, password: String): Flow<ResponseState<Boolean>>

    suspend fun update (name: String, email: String, password: String)

}