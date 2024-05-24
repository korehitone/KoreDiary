package com.syntxr.korediary.presentation.home.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.domain.model.Post
import com.syntxr.korediary.presentation.create.SaveDialog
import com.syntxr.korediary.presentation.home.component.PostItem
import com.syntxr.korediary.utils.formatToString
import com.syntxr.korediary.utils.parseToDate

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostContent(
    data: ResponseState<List<Post>>,
    lazyState: LazyListState,
    flingBehavior: FlingBehavior,
    delete: (uuid: String) -> Unit,
    favorite: (Post) -> Unit,
    deleteAll: () -> Unit,
    toEdit: (title: String, value: String, mood: String, uuid: String) -> Unit,
    toDetail: (title: String, value: String, mood: String, date: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var isDelete by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf("")
    }

    var isDeleteAll by remember {
        mutableStateOf(false)
    }

    var uuid by remember { // uuid yang bisa diubah datanya, digunakan untuk dialog
        mutableStateOf("")
    }

    data.DisplayResult(
        onLoading = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ){
            LinearProgressIndicator()
        } },
        onSuccess = { data ->
            if (data.isNotEmpty()){
                Column(
                    Modifier.fillMaxSize()
                ) {
                    TextButton(onClick = { isDeleteAll = true }) {
                        Text(text = "delete All")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        state = lazyState,
                        flingBehavior = flingBehavior,
                        content = {
                            items(data) { post ->
                                val menus = listOf(
                                    Menu("edit") {
                                        toEdit(
                                            post.title,
                                            post.value,
                                            post.mood,
                                            post.uuid
                                        )
                                    },
                                    Menu("favorite") {
                                        favorite(post)
                                    },
                                    Menu("delete") {
                                        uuid = post.uuid
                                        isDelete = true
                                    },
                                    Menu("detail") {
                                        toDetail(
                                            post.title,
                                            post.value,
                                            post.mood,
                                            post.createdAt.parseToDate().formatToString()
                                        )
                                    }
                                )
                                ExposedDropdownMenuBox(
                                    // dropdown
                                    expanded = expanded, // memanggil boolean, apakah dropdown di tampilkan
                                    onExpandedChange = { // ketika kondisi berubah, mengembalikan boolean untuk expanded
                                        expanded = !expanded
                                    },
                                ) {
                                    PostItem(
                                        title = post.title,
                                        date = post.createdAt.parseToDate().formatToString(),
                                        mood = post.mood,
                                        modifier = Modifier
                                            .menuAnchor()
                                            .combinedClickable(
                                                onClick = {
                                                    toDetail(
                                                        post.title,
                                                        post.value,
                                                        post.mood,
                                                        post.createdAt
                                                            .parseToDate()
                                                            .formatToString()
                                                    )
                                                },
                                                onLongClick = {
                                                    selected = post.uuid
                                                    expanded = true
                                                }
                                            )
                                    )

                                    AnimatedVisibility(visible = selected === post.uuid) {
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }) {
                                            menus.forEach { menu -> // setiap value dari list tema  yang dibuat
                                                DropdownMenuItem( // dropdown item
                                                    text = { Text(text = menu.text) }, // nama tema
                                                    onClick = { // ketika di klik
                                                        menu.onClick()
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = "You have nothing.. ::>_<::")
                }
            }
        },
        onError = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(text = "You have nothing.. ::>_<::")
            }
        }
    )

        AnimatedVisibility(visible = isDelete) {
            SaveDialog(
                text = "Do you want delete this ?",
                onDismissRequest = { isDelete = false },
                onConfirmation = {
                    delete(uuid)
                    isDelete = false
                })
        }

        AnimatedVisibility(visible = isDeleteAll) {
            SaveDialog(
                text = "Do you want delete all data ?",
                onDismissRequest = { isDeleteAll = false },
                onConfirmation = {
                    deleteAll()
                    isDeleteAll = false
                })
        }
    }


data class Menu(
    val text: String,
    val onClick: () -> Unit,
)