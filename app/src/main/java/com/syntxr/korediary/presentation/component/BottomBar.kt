package com.syntxr.korediary.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.syntxr.korediary.presentation.NavGraphs
import com.syntxr.korediary.presentation.appCurrentDestinationAsState
import com.syntxr.korediary.presentation.destinations.HomeScreenDestination
import com.syntxr.korediary.presentation.destinations.SearchScreenDestination
import com.syntxr.korediary.presentation.destinations.SettingsScreenDestination
import com.syntxr.korediary.presentation.startAppDestination


@Composable
fun BottomBar(
    navController: NavController,
) {
    val currentDestination =
        navController.appCurrentDestinationAsState().value ?: NavGraphs.root.startAppDestination

    val barItems = listOf(
        BarItem(HomeScreenDestination, Icons.Rounded.Home, "Home"),
        BarItem(SearchScreenDestination, Icons.Rounded.Search, "Search"),
        BarItem(SettingsScreenDestination, Icons.Rounded.Settings, "Settings")
    )

    NavigationBar {
        barItems.forEach { bar ->
            NavigationBarItem(
                selected = currentDestination == bar.directions,
                onClick = {
                    navController.navigate(bar.directions) {
                        popUpTo(currentDestination.route){
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                label = { Text(text = bar.label) },
                icon = {
                    Icon(
                        imageVector = bar.icon,
                        contentDescription = bar.label
                    )
                }
            )
        }
    }

}

data class BarItem(
    val directions: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String,
)