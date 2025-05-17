package com.example.tsdmanager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: AppViewModel, onCreateProduct: () -> Unit, onEditProduct: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(page) {
        isLoading = true
        viewModel.loadProducts(page, 20)  // Передаём limit
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Товары") },
                actions = {
                    OutlinedTextField(
                        value = viewModel.searchQuery.value,
                        onValueChange = {
                            viewModel.searchQuery.value = it
                            viewModel.filterProducts()
                        },
                        label = { Text("Поиск по названию") },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateProduct) {
                Icon(Icons.Default.Add, contentDescription = "Создать товар")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (viewModel.errorMessage.value.isNotEmpty()) {
                Text(
                    text = viewModel.errorMessage.value,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn {
                itemsIndexed(viewModel.products.value) { _, product ->
                    ProductCard(
                        product = product,
                        quantity = 0,
                        onIncrease = {},
                        onDecrease = {},
                        onEditClick = {
                            viewModel.selectedProduct.value = product
                            onEditProduct()
                        },
                        onDeleteClick = { viewModel.deleteProduct(product.name) }
                    )
                }
                item {
                    if (!isLoading) {
                        Button(onClick = { page++ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Загрузить ещё")
                        }
                    } else {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }
}

