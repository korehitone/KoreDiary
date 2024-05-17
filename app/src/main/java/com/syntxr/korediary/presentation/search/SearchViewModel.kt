package com.syntxr.korediary.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.domain.model.Post
import com.syntxr.korediary.domain.usecase.GlobalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    globalUseCase: GlobalUseCase
) : ViewModel() {

    private val diaryUC = globalUseCase.diaryUseCase

    private val _state = MutableStateFlow<ResponseState<List<Post>>> (ResponseState.Idle)
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResponseState.Idle)

    fun search(query: String){
        viewModelScope.launch {
            _state.emit(ResponseState.Loading) // loading
            try {
                _state.emit(ResponseState.Success(diaryUC.search(query).stateIn(viewModelScope).value))
                // statein digunakan pada flow agar kita bisa mendapatkan value yang ada di dalamnya
            } catch (e : Exception){
                _state.emit(ResponseState.Error(e.message.toString()))
            }
        }
    }

    fun clear(){ // clear query search
        viewModelScope.launch {
            _state.emit(ResponseState.Idle)
        }
    }
}