package com.example.tsdmanager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tsdmanager.ui.*
import kotlinx.coroutines.launch

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(context))
            ClientApp(viewModel = viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Токен уже сохранён в SharedPreferences, можно добавить очистку, если нужно
    }
}

@Composable
fun ClientApp(viewModel: AppViewModel) {
    MaterialTheme {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        var startDestination by remember { mutableStateOf("login") }
        var isLoading by remember { mutableStateOf(true) }

        // Локальная проверка токена при запуске приложения
        LaunchedEffect(Unit) {
            if (viewModel.isJwtTokenValidLocally()) {
                viewModel.getJwtToken()?.let { token ->
                    viewModel.initRepository(token)
                }
                startDestination = "profile" // Токен не истёк, пропускаем экран логина
            }
            isLoading = false
        }

        // Фоновая проверка токена на сервере, если локально токен валиден
        LaunchedEffect(startDestination) {
            if (startDestination == "profile") {
                viewModel.viewModelScope.launch {
                    if (!viewModel.isJwtTokenValidOnServer()) {
                        viewModel.clearJwtToken()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val showBottomBar = currentRoute != "login"

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        AppBottomNavigation(navController = navController, currentRoute = currentRoute)
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(padding)
                ) {
                    composable("login") {
                        LoginScreen(viewModel, onLoginSuccess = {
                            navController.navigate("profile") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        })
                    }
                    composable("profile") {
                        ProfileScreen(viewModel, onOrderDetails = { identifier ->
                            navController.navigate("order_detail/$identifier")
                        })
                    }
                    composable("select_client") {
                        SelectClientScreen(viewModel, onClientSelected = { client ->
                            viewModel.selectedClient.value = client
                            navController.navigate("catalog")
                        })
                    }
                    composable("catalog") {
                        CatalogScreen(viewModel, onCreateOrder = { navController.navigate("profile") })
                    }
                    composable("clients") {
                        ClientScreen(
                            viewModel,
                            onCreateClient = { navController.navigate("create_client") },
                            onEditClient = { navController.navigate("edit_client") }
                        )
                    }
                    composable("create_client") {
                        CreateClientScreen(viewModel, onClientCreated = { navController.popBackStack() })
                    }
                    composable("edit_client") {
                        EditClientScreen(viewModel, onClientUpdated = { navController.popBackStack() })
                    }
                    composable("products") {
                        ProductsScreen(
                            viewModel,
                            onCreateProduct = { navController.navigate("create_product") },
                            onEditProduct = { navController.navigate("edit_product") }
                        )
                    }
                    composable("create_product") {
                        CreateProductScreen(viewModel, onProductCreated = { navController.popBackStack() })
                    }
                    composable("edit_product") {
                        EditProductScreen(viewModel, onProductUpdated = { navController.popBackStack() })
                    }
                    composable("order_detail/{identifier}") { backStackEntry ->
                        val identifier = backStackEntry.arguments?.getString("identifier") ?: ""
                        OrderDetailScreen(viewModel, identifier, onOrderDeleted = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}