package com.syntxr.korediary.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntxr.korediary.domain.usecase.GlobalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    globalUseCase: GlobalUseCase
) : ViewModel() {

    private val userUC =  globalUseCase.authUseCase

    fun update(name: String, email: String, password: String){
        viewModelScope.launch {
            userUC.update(name, email, password)
        }
    }

}