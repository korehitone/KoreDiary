package com.syntxr.korediary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.domain.model.Post
import com.syntxr.korediary.domain.usecase.GlobalUseCase
import com.syntxr.korediary.utils.DiaryOrder
import com.syntxr.korediary.utils.OrderBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    globalUseCase: GlobalUseCase,
) : ViewModel() {
    private val diaryUC = globalUseCase.diaryUseCase // Diary Use Case

    private val _state =
        MutableStateFlow<ResponseState<List<Post>>>(ResponseState.Loading)

    val state = _state.asStateFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ResponseState.Loading
    )

    fun subscribe(
        diaryOrder: DiaryOrder = DiaryOrder.Date, orderBy: OrderBy = OrderBy.Descending,
    ) {
        CoroutineScope(Dispatchers.IO).launch {// berjalan di latar belakang
            val data = diaryUC.get(diaryOrder, orderBy) // memanggil fun get dari use case
            _state.emitAll(data) // memasukkannya ke _state
        }
    }

    fun unsubscribe() {
        viewModelScope.launch {
            diaryUC.unsubscribe()
        }
    }

    fun onSelect(sort: DiaryOrder, order: OrderBy) { // ketika ada perubahan dalam mengurutkan
        subscribe(sort, order)
    }

    fun deletePost(uuid: String){
        viewModelScope.launch {
            diaryUC.deleteDiary(uuid)
        }
    }

    fun deleteAllPost(){
        viewModelScope.launch {
            diaryUC.deleteAllDiary()
        }
    }

}