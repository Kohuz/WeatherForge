package cz.cvut.weatherforge.core.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme
import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.features.home.presentation.HomeScreen
import cz.cvut.weatherforge.features.record.presentatioin.RecordsScreen
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreen
import cz.cvut.weatherforge.features.stations.presentation.list.ListScreen
import cz.cvut.weatherforge.features.stations.presentation.map.MapScreen

sealed class Screens(val route: String) {

    sealed class TopLevel(route: String) : Screens(route) {
        abstract val icon: ImageVector

        data object Map : TopLevel("map/{latitude}/{longitude}") {
            override val icon = Icons.Filled.Place
            fun createRoute(latitude: Double, longitude: Double) = "map/$latitude/$longitude"
        }

        data object DefaultMap : TopLevel("map") { // Add a default route for the map
            override val icon = Icons.Filled.Place
        }

        data object List : TopLevel("list") {
            override val icon = Icons.AutoMirrored.Filled.List
        }

        data object Home : TopLevel("home") {
            override val icon = Icons.Filled.Home
        }

        data object Records : TopLevel("records") {
            override val icon = Icons.Filled.Leaderboard
        }

        companion object {
            val all get() = listOf(Home, DefaultMap, List, Records) // Use DefaultMap in the bottom bar
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
            startDestination = Screens.TopLevel.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(route = Screens.TopLevel.List.route) {
                AppTheme {
                    ListScreen(navigateToDetail = { stationId ->
                        navController.navigate(Screens.Detail.createRoute(stationId))
                    })
                }
            }

            // Default map route (no parameters)
            composable(route = Screens.TopLevel.DefaultMap.route) {
                AppTheme {
                    MapScreen(
                        navigateToDetail = { stationId ->
                            navController.navigate(Screens.Detail.createRoute(stationId))
                        },
                        stationLocation = null // No specific location
                    )
                }
            }

            // Parameterized map route (with latitude and longitude)
            composable(
                route = Screens.TopLevel.Map.route,
                arguments = listOf(
                    navArgument("latitude") { type = NavType.FloatType },
                    navArgument("longitude") { type = NavType.FloatType }
                )
            ) { entry ->
                val latitude = entry.arguments?.getFloat("latitude")?.toDouble()
                val longitude = entry.arguments?.getFloat("longitude")?.toDouble()
                if (latitude != null && longitude != null) {
                    val stationLocation = LatLng(latitude, longitude)
                    AppTheme {
                        MapScreen(
                            navigateToDetail = { stationId ->
                                navController.navigate(Screens.Detail.createRoute(stationId))
                            },
                            stationLocation = stationLocation // Pass the station's location
                        )
                    }
                }
            }

            composable(route = Screens.TopLevel.Home.route) {
                AppTheme {
                    HomeScreen(navigateToDetail = { stationId ->
                        navController.navigate(Screens.Detail.createRoute(stationId))
                    })
                }
            }

            composable(route = Screens.TopLevel.Records.route) {
                AppTheme {
                    RecordsScreen()
                }
            }

            composable(
                route = Screens.Detail.route,
                arguments = listOf(navArgument(Screens.Detail.ID) { type = NavType.StringType })
            ) { entry ->
                val stationId = entry.arguments?.getString(Screens.Detail.ID)
                if (stationId != null) {
                    AppTheme {
                        DetailScreen(
                            stationId = stationId,
                            navigateUp = { navController.navigateUp() },
                            navigateToDetail = { statId ->
                                navController.navigate(Screens.Detail.createRoute(statId))
                            },
                            navigateToMap = { latLng ->
                                navController.navigate(
                                    Screens.TopLevel.Map.createRoute(
                                        latLng.latitude,
                                        latLng.longitude
                                    )
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}