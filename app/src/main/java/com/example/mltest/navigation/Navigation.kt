package com.example.mltest.navigation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.mltest.screens.HomeScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Botsheet(context: Context) {
    val navController = rememberNavController()
    var isNotificationEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            if (currentRoute in listOf("home", "sightinghistory")) {
                TopAppBar(
                    title = { Text("SoulSpace", color = Color.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F0FF)),
                    actions = {
                        IconButton(onClick = { isNotificationEnabled = !isNotificationEnabled }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (isNotificationEnabled) Color.Yellow else Color.Black
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            if (currentRoute in listOf("home", "sightinghistory")) {
                BottomAppBar(containerColor = Color.Black) {
                    val bottomBarItems = listOf(
                        Triple(Icons.Default.Home, "Home", "home"),
                        Triple(Icons.Default., "Sightings", "sightinghistory")
                    )

                    bottomBarItems.forEach { (icon, description, route) ->
                        IconButton(
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo("home") { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = description,
                                tint = if (currentRoute == route) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
          //  composable("sightinghistory") { SightingHistoryScreen() }
        }
    }
}
