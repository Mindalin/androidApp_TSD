package com.example.tsdmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tsdmanager.ui.CatalogScreen
import com.example.tsdmanager.ui.ClientScreen
import com.example.tsdmanager.ui.CreateClientScreen
import com.example.tsdmanager.ui.CreateProductScreen
import com.example.tsdmanager.ui.EditClientScreen
import com.example.tsdmanager.ui.EditProductScreen
import com.example.tsdmanager.ui.LoginScreen
import com.example.tsdmanager.ui.OrderDetailScreen
import com.example.tsdmanager.ui.ProductsScreen
import com.example.tsdmanager.ui.ProfileScreen
import com.example.tsdmanager.ui.ScannerScreen
import com.example.tsdmanager.ui.SelectClientScreen
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
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            // Можно добавить уведомление для пользователя
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(context))
            // Запрос разрешения камеры
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
            ClientApp(viewModel = viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientApp(viewModel: AppViewModel) {
    MaterialTheme {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        var startDestination by rememberSaveable { mutableStateOf("login") }
        var isLoading by rememberSaveable { mutableStateOf(true) }
        var isInitialCheckDone by rememberSaveable { mutableStateOf(false) }
        val configuration = LocalConfiguration.current
        val isTabletLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        // Очистка errorMessage после отображения
        LaunchedEffect(viewModel.errorMessage.value) {
            if (viewModel.errorMessage.value.isNotEmpty()) {
                // Задержка для показа ошибки, затем очистка
                kotlinx.coroutines.delay(3000) // 3 секунды
                viewModel.errorMessage.value = ""
            }
        }

        LaunchedEffect(Unit) {
            if (!isInitialCheckDone) {
                if (viewModel.isJwtTokenValidLocally()) {
                    viewModel.getJwtToken()?.let { token ->
                        viewModel.initRepository(token)
                    }
                    startDestination = "profile"
                }
                isLoading = false
                isInitialCheckDone = true
            }
        }

        LaunchedEffect(startDestination, isInitialCheckDone) {
            if (startDestination == "profile" && isInitialCheckDone) {
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
            Box(modifier = Modifier.fillMaxSize()) {
                if (isTabletLandscape && currentRoute != "login") {
                    Row {
                        AppSideNavigation(navController, currentRoute)
                        Scaffold(
                            modifier = Modifier.fillMaxSize()
                        ) { padding ->
                            Box(
                                modifier = Modifier
                                    .padding(padding)
                                    .fillMaxSize()
                            ) {
                                NavHost(
                                    navController = navController,
                                    startDestination = startDestination
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
                                        SelectClientScreen(viewModel, onClientSelected = {
                                            navController.navigate("catalog")
                                        })
                                    }
                                    composable("catalog") {
                                        CatalogScreen(viewModel, onCreateOrder = {
                                            navController.navigate("profile")
                                        })
                                    }
                                    composable("clients") {
                                        ClientScreen(
                                            viewModel,
                                            onCreateClient = { navController.navigate("create_client") },
                                            onEditClient = { navController.navigate("edit_client") }
                                        )
                                    }
                                    composable("create_client") {
                                        CreateClientScreen(viewModel, onClientCreated = {
                                            navController.popBackStack()
                                        })
                                    }
                                    composable("edit_client") {
                                        EditClientScreen(viewModel, onClientUpdated = {
                                            navController.popBackStack()
                                        })
                                    }
                                    composable("products") {
                                        ProductsScreen(
                                            viewModel,
                                            onCreateProduct = { navController.navigate("create_product") },
                                            onEditProduct = { navController.navigate("edit_product") }
                                        )
                                    }
                                    composable("create_product") {
                                        CreateProductScreen(viewModel, onProductCreated = {
                                            navController.popBackStack()
                                        })
                                    }
                                    composable("edit_product") {
                                        EditProductScreen(viewModel, onProductUpdated = {
                                            navController.popBackStack()
                                        })
                                    }
                                    composable("order_detail/{identifier}") { backStackEntry ->
                                        val identifier = backStackEntry.arguments?.getString("identifier") ?: ""
                                        OrderDetailScreen(viewModel, identifier, onOrderDeleted = {
                                            navController.popBackStack()
                                        })
                                    }
                                    composable("scanner") {
                                        ScannerScreen(viewModel = viewModel, navController = navController)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute != "login") {
                                AppBottomNavigation(navController = navController, currentRoute = currentRoute)
                            }
                        }
                    ) { padding ->
                        Box(
                            modifier = Modifier
                                .padding(padding)
                                .fillMaxSize()
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = startDestination
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
                                    SelectClientScreen(viewModel, onClientSelected = {
                                        navController.navigate("catalog")
                                    })
                                }
                                composable("catalog") {
                                    CatalogScreen(viewModel, onCreateOrder = {
                                        navController.navigate("profile")
                                    })
                                }
                                composable("clients") {
                                    ClientScreen(
                                        viewModel,
                                        onCreateClient = { navController.navigate("create_client") },
                                        onEditClient = { navController.navigate("edit_client") }
                                    )
                                }
                                composable("create_client") {
                                    CreateClientScreen(viewModel, onClientCreated = {
                                        navController.popBackStack()
                                    })
                                }
                                composable("edit_client") {
                                    EditClientScreen(viewModel, onClientUpdated = {
                                        navController.popBackStack()
                                    })
                                }
                                composable("products") {
                                    ProductsScreen(
                                        viewModel,
                                        onCreateProduct = { navController.navigate("create_product") },
                                        onEditProduct = { navController.navigate("edit_product") }
                                    )
                                }
                                composable("create_product") {
                                    CreateProductScreen(viewModel, onProductCreated = {
                                        navController.popBackStack()
                                    })
                                }
                                composable("edit_product") {
                                    EditProductScreen(viewModel, onProductUpdated = {
                                        navController.popBackStack()
                                    })
                                }
                                composable("order_detail/{identifier}") { backStackEntry ->
                                    val identifier = backStackEntry.arguments?.getString("identifier") ?: ""
                                    OrderDetailScreen(viewModel, identifier, onOrderDeleted = {
                                        navController.popBackStack()
                                    })
                                }
                                composable("scanner") {
                                    ScannerScreen(viewModel = viewModel, navController = navController)
                                }
                            }
                        }
                    }
                }

                if (viewModel.isLoading.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun AppSideNavigation(navController: NavHostController, currentRoute: String?) {
    NavigationRail(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        NavigationRailItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Клиенты") },
            selected = currentRoute == "clients",
            onClick = {
                navController.navigate("clients") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Товары") },
            selected = currentRoute == "products",
            onClick = {
                navController.navigate("products") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Заказы") },
            selected = currentRoute == "profile" || currentRoute?.startsWith("order_detail") == true,
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.PlaylistAdd, contentDescription = "Сборка заказа") },
            selected = currentRoute == "select_client" || currentRoute == "catalog",
            onClick = {
                navController.navigate("select_client") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Сканер") },
            selected = currentRoute == "scanner",
            onClick = {
                navController.navigate("scanner") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Клиенты") },
            selected = currentRoute == "clients",
            onClick = {
                navController.navigate("clients") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Товары") },
            selected = currentRoute == "products",
            onClick = {
                navController.navigate("products") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Заказы") },
            selected = currentRoute == "profile" || currentRoute?.startsWith("order_detail") == true,
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PlaylistAdd, contentDescription = "Сборка заказа") },
            selected = currentRoute == "select_client" || currentRoute == "catalog",
            onClick = {
                navController.navigate("select_client") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Сканер") },
            selected = currentRoute == "scanner",
            onClick = {
                navController.navigate("scanner") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    }
}

