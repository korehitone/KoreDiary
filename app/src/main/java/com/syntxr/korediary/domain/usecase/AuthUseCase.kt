package com.syntxr.korediary.domain.usecase

import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend fun register(email: String, password: String, username : String): Flow<ResponseState<Boolean>> =
        repository.register(email, password, username)

    suspend fun login(email: String, password: String): Flow<ResponseState<Boolean>> =
        repository.login(email, password)

    suspend fun update(name: String, email: String, password: String) = repository.update(name, email, password)
}