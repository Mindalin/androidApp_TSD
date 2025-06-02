package com.example.tsdmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Product

private const val VDS_SERVER = "http://46.8.224.199:8000"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: AppViewModel,
    onCreateOrder: () -> Unit
) {
    LaunchedEffect(Unit) {
        println("CatalogScreen: Loading products...")
        viewModel.loadProducts(0, 100) // Загружаем сразу 100 товаров
        println("CatalogScreen: Products loaded: ${viewModel.products.value.size}")
    }

    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Каталог") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {

                        // Иконка поиска или поле ввода
                        if (isSearchExpanded) {
                            OutlinedTextField(
                                value = viewModel.searchQuery.value,
                                onValueChange = { newValue ->
                                    // Запрещаем перенос строки
                                    val singleLineValue = newValue.replace("\n", "")
                                    viewModel.searchQuery.value = singleLineValue
                                    viewModel.filterProducts()
                                    // Сбрасываем результаты и скрываем поле, если запрос пустой
                                    if (singleLineValue.isEmpty()) {
                                        viewModel.loadProducts(0, 100)
                                        isSearchExpanded = false
                                    }
                                },
                                label = { Text("Поиск по названию") },
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(56.dp),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isSearchExpanded = false
                                        viewModel.searchQuery.value = ""
                                        viewModel.loadProducts(0, 100)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Свернуть поиск"
                                        )
                                    }
                                }
                            )
                        } else {
                            IconButton(onClick = { isSearchExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Раскрыть поиск"
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    println("CatalogScreen: Attempting to create order...")
                    val selectedClient = viewModel.selectedClientForOrder.value
                    println("CatalogScreen: Selected client for order: $selectedClient")
                    selectedClient?.let { client ->
                        println("CatalogScreen: Creating order for client ID: ${client.id}")
                        viewModel.createOrder(client.id)
                        onCreateOrder()
                        println("CatalogScreen: Order creation request sent")
                    } ?: run {
                        println("CatalogScreen: Error - Client not selected")
                        viewModel.errorMessage.value = "Клиент не выбран"
                    }
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
                    modifier = Modifier.padding(8.dp)
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val productsList: List<Product> = viewModel.products.value
                itemsIndexed(productsList) { _, product ->
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
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    quantity: Int = 0,
    onIncrease: (() -> Unit)? = null,
    onDecrease: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
            .height(280.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Изображение (занимает большую часть карточки)
            if (product.image != null) {
                AsyncImage(
                    model = "${VDS_SERVER}/${product.image}",
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(4f)
                        .clickable(enabled = onEditClick != null, onClick = { onEditClick?.invoke() }),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(4f)
                        .clickable(enabled = onEditClick != null, onClick = { onEditClick?.invoke() }),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет изображения")
                }
            }

            // Название, цена и запас (под изображением)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.price} руб.",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
//                Text(
//                    text = "Запас: ${product.stock}",
//                    style = MaterialTheme.typography.bodySmall,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
            }

            // Кнопки действий (внизу карточки)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Если переданы onIncrease и onDecrease, показываем кнопки количества
                if (onIncrease != null && onDecrease != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDecrease) {
                            Icon(Icons.Default.Remove, contentDescription = "Уменьшить")
                        }
                        Text(text = quantity.toString())
                        if (quantity < product.stock) {
                            IconButton(onClick = onIncrease) {
                                Icon(Icons.Default.Add, contentDescription = "Увеличить")
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Кнопка удаления (если передан onDeleteClick)
                if (onDeleteClick != null) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            }
        }
    }
}