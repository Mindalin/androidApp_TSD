package com.example.tsdmanager.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppBottomNavigation(navController: NavController, currentRoute: String?) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600 // Планшетная ориентация

    if (isTablet) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight()
        ) {
            NavigationRailItem(
                icon = { Icon(Icons.Default.List, contentDescription = "Заказы") },
                label = { Text("Заказы") },
                selected = currentRoute == "profile",
                onClick = { navController.navigate("profile") { popUpTo(navController.graph.startDestinationId) { inclusive = true } } },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationRailItem(
                icon = { Icon(Icons.Default.Build, contentDescription = "Собрать заказ") },
                label = { Text("Собрать заказ") },
                selected = currentRoute == "select_client",
                onClick = { navController.navigate("select_client") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationRailItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Клиенты") },
                label = { Text("Клиенты") },
                selected = currentRoute == "clients",
                onClick = { navController.navigate("clients") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationRailItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Товары") },
                label = { Text("Товары") },
                selected = currentRoute == "products",
                onClick = { navController.navigate("products") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    } else {
        NavigationBar(
            tonalElevation = 8.dp
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.List, contentDescription = "Заказы") },
                label = { Text("Заказы") },
                selected = currentRoute == "profile",
                onClick = { navController.navigate("profile") { popUpTo(navController.graph.startDestinationId) { inclusive = true } } },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Build, contentDescription = "Собрать заказ") },
                label = { Text("Собрать заказ") },
                selected = currentRoute == "select_client",
                onClick = { navController.navigate("select_client") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Клиенты") },
                label = { Text("Клиенты") },
                selected = currentRoute == "clients",
                onClick = { navController.navigate("clients") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Товары") },
                label = { Text("Товары") },
                selected = currentRoute == "products",
                onClick = { navController.navigate("products") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}