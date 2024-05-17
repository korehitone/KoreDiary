package com.syntxr.korediary.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.domain.usecase.GlobalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    globalUseCase: GlobalUseCase,
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email

    private val _password = MutableStateFlow("")
    val password: Flow<String> = _password

    private val _username = MutableStateFlow("")
    val username: Flow<String> = _username

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onUsernameChange(username: String) {
        _username.value = username
    }

    private val _registerState = MutableStateFlow<ResponseState<Boolean>>(ResponseState.Idle)
    val registerState = _registerState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ResponseState.Idle
        )

    private val authUC = globalUseCase.authUseCase

    fun register() {
        viewModelScope.launch {
            _registerState.emitAll(authUC.register(_email.value, _password.value, _username.value))
        }
    }
}