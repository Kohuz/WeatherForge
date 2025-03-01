package cz.cvut.weatherforge.core.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.cvut.weatherforge.R
import cz.cvut.weatherforge.features.stations.presentation.list.ListScreen
import cz.cvut.weatherforge.features.stations.presentation.map.MapScreen
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cz.cvut.weatherforge.features.home.presentation.HomeScreen
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreen

sealed class Screens(val route: String) {

    sealed class TopLevel(route: String) : Screens(route) {
                abstract val icon: ImageVector

        data object Map : TopLevel("map") {
            override val icon = Icons.Filled.Place
        }

        data object List : TopLevel("list") {
            override val icon = Icons.AutoMirrored.Filled.List
        }
        data object Home : TopLevel("home") {
            override val icon = Icons.Filled.Home
        }

//        data object Settings : TopLevel("settings") {
//            override val icon = Icons.Filled.Info
//        }

        companion object {
            val all get() = listOf(Home, Map, List)
        }
    }

    data object Detail : Screens("detail/{id}") {
        const val ID = "id"
        fun createRoute(id: String) = "detail/$id"
    }
}
@Composable
fun Navigation() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Screens.TopLevel.all.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.TopLevel.Map.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(route = Screens.TopLevel.List.route) {
                ListScreen(navigateToDetail = { stationId ->
                    navController.navigate(Screens.Detail.createRoute(stationId))
                })
            }

            composable(route = Screens.TopLevel.Map.route) {
                MapScreen(navigateToDetail = { stationId ->
                    navController.navigate(Screens.Detail.createRoute(stationId))
                })
            }
            composable(route = Screens.TopLevel.Home.route) {
                HomeScreen()
            }
//            composable(route = Screens.TopLevel.Settings.route) {
//                SettingsScreen()
//            }


            composable(
                route = Screens.Detail.route,
                arguments = listOf(navArgument(Screens.Detail.ID) { type = NavType.StringType })
            ) { entry ->
                val stationId = entry.arguments?.getString(Screens.Detail.ID)
                if (stationId != null) {
                    DetailScreen(
                        stationId = stationId,
                        navigateUp = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
