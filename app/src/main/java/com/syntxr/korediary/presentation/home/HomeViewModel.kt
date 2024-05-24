package com.syntxr.korediary.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.domain.model.Post
import com.syntxr.korediary.domain.usecase.GlobalUseCase
import com.syntxr.korediary.utils.DiaryOrder
import com.syntxr.korediary.utils.OrderBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    globalUseCase: GlobalUseCase,
) : ViewModel() {
    private val diaryUC = globalUseCase.diaryUseCase // Diary Use Case



    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _postState = MutableStateFlow<ResponseState<List<Post>>>(ResponseState.Loading)
    val postState = _postState.asStateFlow()

    private val _draftState = MutableStateFlow<ResponseState<List<Post>>>(ResponseState.Loading)
    val draftState = _draftState.asStateFlow()

    private var getFavorite : Job? = null
    private var getPost: Job? = null
    private var getDraft: Job? = null

    fun subscribe(
        diaryOrder: DiaryOrder = DiaryOrder.Date, orderBy: OrderBy = OrderBy.Descending,
    ) {
        getPost?.cancel()
        getFavorite?.cancel()
        getDraft?.cancel()
        CoroutineScope(Dispatchers.IO).launch {// berjalan di latar belakang
                getPost = launch { _postState.emitAll(diaryUC.fetch(diaryOrder, orderBy)) }

            getFavorite = diaryUC.getFavourite(diaryOrder, orderBy).onEach {
                _state.value  = _state.value.copy(
                    favourite = it
                )
            }.launchIn(this)

                getDraft = launch {  _draftState.emitAll(diaryUC.draft()) }
            unsubscribe()
        }
    }

    fun unsubscribe() {
        viewModelScope.launch {
            diaryUC.unsubscribe()
        }
    }

    fun refresh(){
        viewModelScope.launch {
            subscribe()
        }
    }

    fun onSelect(sort: DiaryOrder, order: OrderBy) { // ketika ada perubahan dalam mengurutkan
        viewModelScope.launch {
            subscribe(sort, order)
        }
    }

    fun insertFavorite(postDto: PostDto) {
        CoroutineScope(Dispatchers.IO).launch {
            diaryUC.insertFavorite(postDto)
        }
    }

    fun publish(uuid: String, published: Boolean){
        viewModelScope.launch {
            diaryUC.updateDraft(uuid, published)
        }
    }
    fun deleteAllDraft() {
        viewModelScope.launch{
            diaryUC.deleteAllDraft()
        }
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

    fun deleteFavorite(uuid: String){
        CoroutineScope(Dispatchers.IO).launch {
            diaryUC.deleteFavorite(uuid)
        }
    }

    fun deleteAllFavorite(){
        CoroutineScope(Dispatchers.IO).launch {
            diaryUC.deleteFavoriteAll()
        }
    }

}

data class HomeState(
    val favourite: List<Post>? = emptyList(),
)
