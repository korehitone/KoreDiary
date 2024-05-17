package com.syntxr.korediary.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iamageo.multifablibrary.FabIcon
import com.iamageo.multifablibrary.FabOption
import com.iamageo.multifablibrary.MultiFabItem
import com.iamageo.multifablibrary.MultiFloatingActionButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.korediary.R
import com.syntxr.korediary.data.kotpref.LocalUser
import com.syntxr.korediary.presentation.create.EditorScreenNavArgs
import com.syntxr.korediary.presentation.create.SaveDialog
import com.syntxr.korediary.presentation.destinations.EditorScreenDestination
import com.syntxr.korediary.presentation.destinations.SearchScreenDestination
import com.syntxr.korediary.presentation.destinations.SettingsScreenDestination
import com.syntxr.korediary.presentation.home.component.OrderSection
import com.syntxr.korediary.presentation.home.component.PostItem
import com.syntxr.korediary.utils.DiaryOrder
import com.syntxr.korediary.utils.GlobalState
import com.syntxr.korediary.utils.OrderBy
import com.syntxr.korediary.utils.formatToString
import com.syntxr.korediary.utils.parseToDate

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(true) // layar yang pertama kali tampil
@Destination  // karena menggunakan library dari raamcosta, kita perlu ini agar bisa ditampilkan atau dituju
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator, // untuk berpindah antara screen
    viewModel: HomeViewModel = hiltViewModel(), // karena pakai hilt-dagger, kita pakai ini. HomeViewModel juga kan pakai hilt view model
) {
    val multiFabItem = listOf( // karena menggunakan library multi fab, kita perlu membuat list yang berisi sub menu
        MultiFabItem(
            icon = R.drawable.round_add_24, // menggunakan vector image yang disediakan android studio dan google
            label = "create",
            labelColor = MaterialTheme.colorScheme.onBackground
        ),
        MultiFabItem(
            icon = R.drawable.round_settings_24,
            label = "settings",
            labelColor = MaterialTheme.colorScheme.onBackground
        ),
        MultiFabItem(
            icon = R.drawable.round_sort_24,
            label = "sort",
            labelColor = MaterialTheme.colorScheme.onBackground
        ),
        MultiFabItem(
            icon = R.drawable.round_search_24,
            label = "search",
            labelColor = MaterialTheme.colorScheme.onBackground
        )
    )
    val lazyState = rememberLazyListState() // state untuk lazy colum
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyState) // animasi, memerkukan state
    val state by viewModel.state.collectAsStateWithLifecycle() // state yang kita buat di view model

    var isVisible by remember { // kondisi untuk order section, apakah ditampilkan ?
        mutableStateOf(false)
    }

    var deleteDialogVisibe by remember { // kondisi untuk delete dialog, apakah ditampilkan
        mutableStateOf(false)
    }

    var deleteAllDialogVisibe by remember {
        mutableStateOf(false)
    }


    var sortBy: DiaryOrder by remember { // urutkan menurut, nilai defaultnya DiaryOrder.date
        mutableStateOf(DiaryOrder.Date)
    }

    var orderBy: OrderBy by remember { // urutkan secara, nilai defaultnya OrderBy.Descending
        mutableStateOf(OrderBy.Descending)
    }

    var uuid by remember { // uuid yang bisa diubah datanya, digunakan untuk dialog
        mutableStateOf("")
    }

    var deleteTxt by remember { // text untuk delete & delete all dialog
        mutableStateOf("")
    }

    LaunchedEffect(key1 = Unit, block = { // menjalankan di latar belakang
        viewModel.subscribe(sortBy, orderBy) // mengambil data, subscribe ke real time
    })

    Scaffold( // view parent, kalau misalnya ingin menggunakan topbar, pakai snackbar, atau FAB. Harus pakai ini
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { // FAB
            MultiFloatingActionButton( // dari library multifab
                fabIcon = FabIcon(
                    iconRes = R.drawable.round_expand_less_24,
                    iconResAfterRotate = R.drawable.round_expand_less_24,
                    iconRotate = 180f
                ),
                fabOption = FabOption(
                    showLabels = true,
                    backgroundTint = MaterialTheme.colorScheme.tertiary,
                    iconTint = MaterialTheme.colorScheme.surface
                ),
                fabTitle = "Multi FAB Menu",
                itemsMultiFab = multiFabItem,
                showFabTitle = false,
                onFabItemClicked = {
                    when (it.label) { // kondisi, ketika label adalah, maka ketika di klik akan...
                        "create" -> {
                            navigator.navigate( // jangan dulu diketik jika kalian belum buat Editor screen
                                EditorScreenDestination( // mengirim data ke EditorScreen
                                    EditorScreenNavArgs(
                                        false,
                                        "",
                                        "",
                                        "{\"spans\":[],\"text\":\"text sample\"}",
                                        "😊"
                                    )
                                )
                            )
                        }

                        "settings" -> {
                            navigator.navigate(SettingsScreenDestination)
                        }

                        "sort" -> {
                            isVisible = !isVisible
                        }

                        "search" -> {
                            navigator.navigate(SearchScreenDestination)
                        }
                    }
                }
            )
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            val maxHeight = this.maxHeight
            val headerHeight = maxHeight / 5
            val bodyHeight = maxHeight * 5 / 6
            Box(
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .paint(
                        painterResource(id = GlobalState.theme.background),
                        contentScale = ContentScale.Crop
                    )
            ) {

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = LocalUser.username.ifEmpty { "Unknown" }, // kalo username local kosong, maka akan menampilkan unknown
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.6.sp
                    )
                    Text(
                        text = "Hi, welcome back",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bodyHeight)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                ),
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AnimatedVisibility( // memunculkan komponen  yang ada di dalamnya jika kondisi terpenuhi atau true
                            visible = isVisible,
                            enter = fadeIn() + slideInVertically(), // animasi untuk child
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            OrderSection(
                                diaryOrder = sortBy,
                                order = orderBy,
                                onOrderChange = { select, order ->
                                    sortBy = select
                                    orderBy = order
                                    viewModel.onSelect(sortBy, orderBy)
                                    Log.d("NEEEEEE~~~~~~~~~~", "HomeScreen: $select - $order")
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        AnimatedVisibility(
                            visible = !state.getSuccessDataOrNull().isNullOrEmpty(), // kalau data tidak kosong
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            TextButton(
                                onClick = {
                                    deleteAllDialogVisibe = true; deleteTxt = // ; agar codingan bisa inline
                                    "Do you want to delete all post?"
                                }
                            ) {
                                Text(text = "Delete All")
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                        }

                        state.DisplayResult( // dari library api wrapper, menampilkan data dan pesan error menggunakan ini
                            onLoading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    content = {
                                        LinearProgressIndicator()
                                    },
                                    contentAlignment = Alignment.Center
                                )
                            },
                            onSuccess = { data ->
                                if (data.isNotEmpty()) { // kalo data tidak kosong akan ditampilkan
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
                                                    modifier = Modifier.combinedClickable( // menyediakan agar bisa di klik sekali dan di klik lama
                                                        onClick = { // klik sekali
                                                            navigator.navigate(
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
                                                        onLongClick = { // klik lama
                                                            deleteDialogVisibe = true; deleteTxt =
                                                            "do you want to delete this?"
                                                            uuid = post.uuid
                                                        }
                                                    )
                                                )
                                            }
                                        }
                                    )
                                } else { // kalo data kosong, akan menampilkan text
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = "You have nothing, ヾ(≧ ▽ ≦)ゝ yay!",
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            },
                            onErrorWithData = { msg, data ->
                                if (!data.isNullOrEmpty()) {
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
                                                            navigator.navigate(
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
                                                        onLongClick = {
                                                            deleteDialogVisibe = true; deleteTxt =
                                                            "do you want to delete this?"
                                                            uuid = post.uuid
                                                        }
                                                    )
                                                )
                                            }
                                        }
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = "You have nothing, ヾ(≧ ▽ ≦)ゝ yay!",
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                                Toast.makeText(LocalContext.current, msg, Toast.LENGTH_SHORT).show() // memunculkan pesan error dengan toast
                            }
                        )


                        AnimatedVisibility(visible = deleteDialogVisibe) { // memunculkan delete dialog
                            SaveDialog(
                                text = deleteTxt,
                                onDismissRequest = { deleteDialogVisibe = false },
                                onConfirmation = {
                                    viewModel.deletePost(uuid) // memanggil fungsi delete dari viewmodel
                                    deleteDialogVisibe = false
                                }
                            )
                        }

                        AnimatedVisibility(visible = deleteAllDialogVisibe) {
                            SaveDialog(
                                text = deleteTxt,
                                onDismissRequest = { deleteDialogVisibe = false },
                                onConfirmation = {
                                    viewModel.deleteAllPost()
                                    deleteAllDialogVisibe = false
                                }
                            )
                        }

                    }


                }
            )
        }
    }
    DisposableEffect(key1 = Unit, effect = { // kalau pakai launch effect jadi crash
        onDispose { viewModel.unsubscribe() }
    // kita harus unsubscribe. karene jika kita berpindah lalu kembali ke screen ini, akan ada error bahwa kita tidak bisa subscribe channel lebih dari sekali
    })
}

//@Preview
//@Composable
//fun HomePrev() {
//    HomeScreen()
//}