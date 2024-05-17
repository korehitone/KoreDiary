package com.syntxr.korediary.presentation.search

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.korediary.presentation.create.EditorScreenNavArgs
import com.syntxr.korediary.presentation.destinations.EditorScreenDestination
import com.syntxr.korediary.presentation.home.component.PostItem
import com.syntxr.korediary.utils.formatToString
import com.syntxr.korediary.utils.parseToDate

@Destination
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle() // state dari viewmodel
    val lazyState = rememberLazyListState() // state untuk lazy column
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyState)
    // animasi untuk lazy column, membutuhkan state

    var queryState by remember {
        mutableStateOf("")
        // initial value "" atau kosong, digunakan untuk set query untuk pencarian
    }

    var activeState by remember {
        mutableStateOf(false)
        // untuk kondisi, apakah search bar masih aktif ?
    }

    Scaffold { values ->
        Column(
            modifier = Modifier
                .padding(values)
                .fillMaxSize()
        ) {
            IconButton(
                // tombol kembali ke halaman sebelumnya
                onClick = { navigator.navigateUp() },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
            }
            SearchBar(
                query = queryState, // value untuk search bar
                onQueryChange = { // ketika ada perubahan value dari search bar
                    queryState = it
                },
                onSearch = { // apakah lagi search ?
                    activeState = true // mengubah kondisi menjadi true (aktif)
                    viewModel.search(it) // memanggil fungsi mencari berdasarkan query yang ada
                },
                active = activeState, // kondisi
                onActiveChange = { // perubahan kondisi
                    activeState = it
                },
                trailingIcon = {
                    if (activeState) { // ketika kondisi true atau aktif
                        IconButton(onClick = {
                            if (queryState.isNotEmpty()) { // kalau query tidak kosong
                                queryState = ""
                            } else {
                                activeState = false
                                viewModel.clear() // menghapus query yang ada
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "btn_close"
                            )
                        }
                    }
                }
            ) {
                state.DisplayResult(
                    onIdle = { // kalau tidak sedang melakukan apapun
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Search Something here  ♪(´▽｀)",
                            )
                        }
                    },
                    onLoading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LinearProgressIndicator()
                        }
                    },
                    onSuccess = { data ->
                        if (data.isNotEmpty()){ // menampilkan data ketika tidak kosong
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = lazyState,
                                flingBehavior = flingBehavior,
                                content = {
                                    items(data) { post ->
                                        val zoneTime = post.createdAt.parseToDate()
                                        val formatted = zoneTime.formatToString()
                                        PostItem(
                                            title = post.title,
                                            date = formatted,
                                            mood = post.mood,
                                            modifier = Modifier.combinedClickable(
                                                onClick = {
                                                    navigator.navigate( // pindah ke editor screen
                                                        EditorScreenDestination(
                                                            EditorScreenNavArgs(
                                                                true,
                                                                post.uuid,
                                                                post.title,
                                                                post.value,
                                                                post.mood
                                                            )
                                                        )
                                                    )
                                                },
                                            )
                                        )
                                    }
                                }
                            )
                        }else{ // kalau data tidak ada
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Can't found it ＞︿＜",
                                )
                            }
                        }
                    },
                    onError = { message ->
                        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}