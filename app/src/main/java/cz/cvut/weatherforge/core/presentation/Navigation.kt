package cz.cvut.weatherforge.core.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
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

sealed class Screens(val route: String) {

    sealed class TopLevel(route: String) : Screens(route) {

        @get:StringRes
        abstract val title: Int

        abstract val icon: ImageVector

        data object Map : TopLevel("stations/map") {

            override val title = R.string.bottom_nav_title_stations_map

            override val icon = Icons.Filled.Person
        }

        data object List : TopLevel("stations/list") {

            override val title = R.string.bottom_nav_title_stations_list

            override val icon = Icons.Filled.Info
        }

        companion object {

            val all get() = listOf(Map, List)
        }
    }

    class StationDetail(stationId: String) : Screens("stations/$stationId") {

        companion object {

            const val ID = "id"
        }
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
                        label = { Text(stringResource(screen.title)) },
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
                ListScreen()
            }

            composable(route = Screens.TopLevel.Map.route) {
                MapScreen()
            }
        }
    }
}
