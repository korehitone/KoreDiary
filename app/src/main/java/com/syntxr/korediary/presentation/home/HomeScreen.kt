package com.syntxr.korediary.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.fengdai.compose.pulltorefresh.PullToRefresh
import com.github.fengdai.compose.pulltorefresh.PullToRefreshState
import com.github.fengdai.compose.pulltorefresh.rememberPullToRefreshState
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
import com.syntxr.korediary.presentation.destinations.DetailScreenDestination
import com.syntxr.korediary.presentation.destinations.EditorScreenDestination
import com.syntxr.korediary.presentation.detail.DetailScreenNavArgs
import com.syntxr.korediary.presentation.home.component.OrderSection
import com.syntxr.korediary.presentation.home.post.PostContent
import com.syntxr.korediary.presentation.home.draft.DraftContent
import com.syntxr.korediary.presentation.home.favorite.FavoriteContent
import com.syntxr.korediary.utils.DiaryOrder
import com.syntxr.korediary.utils.GlobalState
import com.syntxr.korediary.utils.OrderBy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(true) // layar yang pertama kali tampil
@Destination  // karena menggunakan library dari raamcosta, kita perlu ini agar bisa ditampilkan atau dituju
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator, // untuk berpindah antara screen
    viewModel: HomeViewModel = hiltViewModel(), // karena pakai hilt-dagger, kita pakai ini. HomeViewModel juga kan pakai hilt view model
) {
    val coroutine = rememberCoroutineScope()
    val multiFabItem =
        listOf( // karena menggunakan library multi fab, kita perlu membuat list yang berisi sub menu
            MultiFabItem(
                icon = R.drawable.round_add_24, // menggunakan vector image yang disediakan android studio dan google
                label = "create",
                labelColor = MaterialTheme.colorScheme.onBackground
            ),
            MultiFabItem(
                icon = R.drawable.round_sort_24,
                label = "sort",
                labelColor = MaterialTheme.colorScheme.onBackground
            ),
            MultiFabItem(
                icon = R.drawable.round_sync_24,
                label = "refresh",
                labelColor = MaterialTheme.colorScheme.onBackground
            )
        )
    var refreshing by remember { mutableStateOf(false) }
    val lazyState = rememberLazyListState() // state untuk lazy colum
    val flingBehavior =
        rememberSnapFlingBehavior(lazyListState = lazyState) // animasi, memerkukan state
    val state= viewModel.state.value// state yang kita buat di view model
    val post by viewModel.postState.collectAsStateWithLifecycle()
    val draft by viewModel.draftState.collectAsStateWithLifecycle()


    var isVisible by remember { // kondisi untuk order section, apakah ditampilkan ?
        mutableStateOf(false)
    }

    var sortBy: DiaryOrder by remember { // urutkan menurut, nilai defaultnya DiaryOrder.date
        mutableStateOf(DiaryOrder.Date)
    }

    var orderBy: OrderBy by remember { // urutkan secara, nilai defaultnya OrderBy.Descending
        mutableStateOf(OrderBy.Descending)
    }

    val tabs = listOf(
        TabItem(
            title = "Diaries",
            content = {
                PostContent(
                    data = post,
                    lazyState = lazyState,
                    flingBehavior = flingBehavior,
                    delete = {
                        viewModel.deletePost(it)
                    },
                    favorite = {
                        viewModel.insertFavorite(it.toPostDto())
                    },
                    deleteAll = { viewModel.deleteAllPost() },
                    toEdit = { title, value, mood, uuid ->
                        navigator.navigate(
                            EditorScreenDestination(
                                EditorScreenNavArgs(
                                    isEdit = true,
                                    uuid = uuid,
                                    title = title,
                                    ysiyg = value,
                                    mood = mood
                                )
                            )
                        )
                    }
                ) { title, value, mood, date ->
                    navigator.navigate(
                        DetailScreenDestination(
                            DetailScreenNavArgs(
                                title, value, mood, date
                            )
                        )
                    )
                }
            }
        ),
        TabItem(
            "draft",
            content = {
                DraftContent(
                    data = draft,
                    lazyState = lazyState,
                    flingBehavior = flingBehavior,
                    delete = {
                        viewModel.deletePost(it)
                    },
                    deleteAll = { viewModel.deleteAllDraft() },
                    publish = { uuid, published ->
                        viewModel.publish(uuid, published)
                    }
                )
            }
        ),
        TabItem(
            "favorite",
            content = {
                FavoriteContent(
                    data = state.favourite ?: emptyList(),
                    lazyState = lazyState,
                    flingBehavior = flingBehavior,
                    delete = {viewModel.deleteFavorite(it)},
                    deleteAll = { viewModel.deleteAllFavorite() },
                    toDetail = { title, value, mood, date ->
                        navigator.navigate(
                            DetailScreenDestination(
                                DetailScreenNavArgs(
                                    title, value, mood, date
                                )
                            )
                        )
                    }
                )
            }
        )
    )

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()


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


                        "sort" -> {
                            isVisible = !isVisible
                        }

                        "refresh" -> {
                            viewModel.refresh()
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

                            TabRow(
                                selectedTabIndex = pagerState.currentPage,
                            ) {
                                tabs.forEachIndexed { index, tabItem ->
                                    Tab(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        },
                                        text = {
                                            Text(
                                                text = tabItem.title,
                                            )
                                        }
                                    )
                                }
                            }


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
                                    }
                                )
                            }

                            HorizontalPager(
                                state = pagerState,
                                userScrollEnabled = false
                            ) {
                                tabs[pagerState.currentPage].content()
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

data class TabItem(
    val title: String,
    val content: @Composable () -> Unit,
)