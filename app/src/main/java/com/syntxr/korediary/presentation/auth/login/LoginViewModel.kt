package com.syntxr.korediary.presentation.auth.login

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
class LoginViewModel @Inject constructor(
    globalUseCase: GlobalUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("") // membuat val yang berisi initial  value untuk email
    val email: Flow<String> = _email // val email yang menggunakan flow agar perubahan data nya terjadi langsung, bisa dipanggil ke screen

    private val _password = MutableStateFlow("") // sama kayak email tapi untuk password
    val password : Flow<String> = _password

    fun onEmailChange(email: String) { // function untuk menerima perubahan data yang di input dari pengguna
        _email.value = email // memasukkan data yang diterima ke  val _email
    }

    fun onPasswordChange(password: String) { // sama kayak email
        _password.value = password
    }

    private val _loginState = MutableStateFlow<ResponseState<Boolean>>(ResponseState.Idle)
    // membuat val yang berisi ResponseState<Boolean> dengan flow, initial valuenya .ResponseStateIdle
    val loginState = _loginState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ResponseState.Idle
        )
//    val _loginState dibuat state flow agar bisa dipanggil valuenya di screen

    private val authUC = globalUseCase.authUseCase // memanggil AuthUseCase


    // fun yang memanggil logika login dari repository melalui usecase yang telah dibuat
    fun login(){
        viewModelScope.launch {
            _loginState.emitAll(authUC.login(_email.value, _password.value))
        }
    }
}