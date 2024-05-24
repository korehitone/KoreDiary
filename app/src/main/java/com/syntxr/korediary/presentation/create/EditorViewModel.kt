package com.syntxr.korediary.presentation.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.domain.usecase.GlobalUseCase
import com.syntxr.korediary.presentation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    globalUseCase: GlobalUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val diaryUC = globalUseCase.diaryUseCase

    private val _state = MutableStateFlow<ResponseState<Boolean>>(ResponseState.Idle)

    val isEditorHandle = savedStateHandle.navArgs<EditorScreenNavArgs>().isEdit // abaikan terlebih dahulu
    val titleHandle = savedStateHandle.navArgs<EditorScreenNavArgs>().title // abaikan terlebih dahulu
    val valueHandle = savedStateHandle.navArgs<EditorScreenNavArgs>().ysiyg // abaikan terlebih dahulu
    val moodHandle = savedStateHandle.navArgs<EditorScreenNavArgs>().mood // abaikan terlebih dahulu
    private val uuidHandle = savedStateHandle.navArgs<EditorScreenNavArgs>().uuid // abaikan terlebih dahulu

    val state =  _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ResponseState.Idle
    )

    fun saveCloud(postDto: PostDto){
        viewModelScope.launch {
            diaryUC.insertDiary(postDto)
        }
    }

    fun saveDraft(postDto: PostDto){
        viewModelScope.launch {
            diaryUC.insertDraft(postDto)
        }
    }

    fun updateCloud(title: String, value: String, mood: String) {
        viewModelScope.launch {
            diaryUC.updateDiary(uuidHandle, title, value, mood)
        }
    }

}