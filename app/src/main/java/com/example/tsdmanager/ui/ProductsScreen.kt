package com.example.tsdmanager.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: AppViewModel,
    onCreateProduct: () -> Unit,
    onEditProduct: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadProducts(0, 100) // Загружаем сразу 100 товаров
    }

    var isSearchExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) } // Для хранения productId при удалении

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Товары") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        // Кнопка добавления товара
                        IconButton(onClick = onCreateProduct) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить товар"
                            )
                        }
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(viewModel.products.value) { _, product ->
                    ProductCard(
                        product = product,
                        onEditClick = {
                            viewModel.selectedProduct.value = product
                            onEditProduct()
                        },
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    showDeleteDialog = product.id // Показываем диалог при долгом нажатии
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    // Диалог подтверждения удаления
    showDeleteDialog?.let { productId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить этот товар?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteProduct(productId)
                    showDeleteDialog = null
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}