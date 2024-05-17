package com.syntxr.korediary

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.scope.resultRecipient
import com.syntxr.korediary.data.kotpref.LocalUser
import com.syntxr.korediary.presentation.NavGraphs
import com.syntxr.korediary.presentation.auth.login.LoginScreen
import com.syntxr.korediary.presentation.auth.register.RegisterScreen
import com.syntxr.korediary.presentation.create.EditorScreen
import com.syntxr.korediary.presentation.destinations.EditorScreenDestination
import com.syntxr.korediary.presentation.destinations.HomeScreenDestination
import com.syntxr.korediary.presentation.destinations.LoginScreenDestination
import com.syntxr.korediary.presentation.destinations.RegisterScreenDestination
import com.syntxr.korediary.presentation.destinations.SearchScreenDestination
import com.syntxr.korediary.presentation.destinations.SettingsScreenDestination
import com.syntxr.korediary.presentation.home.HomeScreen
import com.syntxr.korediary.presentation.search.SearchScreen
import com.syntxr.korediary.presentation.settings.SettingsScreen

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun KoreApp(
    navController: NavHostController = rememberNavController(),
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator // agar bisa memunculkan bottom sheet

    ModalBottomSheetLayout( // bottom sheet
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {
        DestinationsNavHost( // nav host
            navGraph = NavGraphs.root,
            navController = navController,
            startRoute = if (LocalUser.uuid.isEmpty()) LoginScreenDestination else HomeScreenDestination,
            engine = rememberAnimatedNavHostEngine()
        ){
            composable(HomeScreenDestination){
                HomeScreen( // karena di screen membutuhkan navigator, kita isi seperti ini
                navigator = destinationsNavigator
                )
            }

            composable(RegisterScreenDestination){
                RegisterScreen(navigator = destinationsNavigator)
            }

            composable(LoginScreenDestination){
                LoginScreen(navigator = destinationsNavigator)
            }

            composable(EditorScreenDestination){
                EditorScreen(
                    navigator = destinationsNavigator,
                    resultRecipient = resultRecipient()
                )
            }

            composable(SettingsScreenDestination){
                SettingsScreen(navigator = destinationsNavigator)
            }

            composable(SearchScreenDestination){
                SearchScreen(navigator = destinationsNavigator)
            }

        }
    }
}