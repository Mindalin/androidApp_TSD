package com.example.tsdmanager.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.R
import com.example.tsdmanager.data.Product

private const val VDS_SERVER = "http://46.8.224.199:8000"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(viewModel: AppViewModel, onCreateOrder: () -> Unit) {
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
                title = { Text("Каталог") },
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
            FloatingActionButton(
                onClick = {
                    viewModel.selectedClient.value?.let { client ->
                        viewModel.createOrder(client.id)
                        onCreateOrder()
                    } ?: run { viewModel.errorMessage.value = "Клиент не выбран" }
                },
                modifier = Modifier.padding(16.dp),
                content = { Icon(Icons.Default.Check, contentDescription = "Создать заказ") }
            )
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
                        quantity = viewModel.cartState.value[product.id] ?: 0,
                        onIncrease = {
                            viewModel.cart[product.id] = (viewModel.cart[product.id] ?: 0) + 1
                            viewModel.cartState.value = viewModel.cart.toMap()
                        },
                        onDecrease = {
                            val current = viewModel.cart[product.id] ?: 0
                            if (current > 0) {
                                viewModel.cart[product.id] = current - 1
                                if (viewModel.cart[product.id] == 0) viewModel.cart.remove(product.id)
                                viewModel.cartState.value = viewModel.cart.toMap()
                            }
                        }
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
@Composable
fun ProductCard(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "${VDS_SERVER}/${product.image}",
                contentDescription = product.name,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = "${product.price} руб.")
                Text(text = "Запас: ${product.stock}")
            }
            if (onEditClick != null && onDeleteClick != null) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) {
                        Icon(Icons.Filled.Clear, contentDescription = "Уменьшить")
                    }
                    Text(text = quantity.toString())
                    if (quantity < product.stock) {
                        IconButton(onClick = onIncrease) {
                            Icon(Icons.Default.Add, contentDescription = "Увеличить")
                        }
                    }
                }
            }
        }
    }
}