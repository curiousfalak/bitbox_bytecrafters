package com.example.mltest.navigation



import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Botsheet(context: Context) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var isNotificationEnabled by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(360.dp)
            ) {
                SideMenu(navController = navController) {
                    coroutineScope.launch { drawerState.close() }
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route
                if (currentRoute in listOf("home", "profile", "sen")) {
                    TopAppBar(
                        title = { Text("SoulSpace", color = Color.Black) },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) drawerState.open()
                                        else drawerState.close()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
                            }
                        },
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

                if (currentRoute in listOf("home", "profile", "aicompanion")) {
                    BottomAppBar(containerColor = Color.Black) {
                        val bottomBarItems = listOf(
                            Triple(Icons.Default.Home, "Home", "home"),
                            Triple(Icons.Default.Face, "AICompanion", "aicompanion"),
                            Triple(Icons.Default.Person, "Profile", "profile")
                        )

                        bottomBarItems.forEach { (icon, description, route) ->
                            IconButton(
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo("home") { inclusive = false }
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
                startDestination = "onboarding",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("onboarding") {
                    OnBoardingScreen(onGetStarted = { navController.navigate("home") })
                }
                composable("home") { HomeScreen() }
                composable("ar_vr") { ARScreen() }
                composable("aicompanion") { ChatPage(context, modifier = Modifier, ChatViewModel()) }
                composable("exercise") { AppwriteDatabaseScreen(context) }
                composable("games") { DrawIt() }
                composable("mood") {
                    SentimentActivity(
                        context,
                        context as LifecycleOwner
                    )
                }
                composable("profile") { ProfileScreen() }
            }
        }
    }
}


