package com.example.tsdmanager.ui.previews

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.data.Product
import com.example.tsdmanager.ui.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreenPreviewContent(
    products: List<Product> = emptyList(),
    errorMessage: String = "",
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onCreateProduct: () -> Unit = {},
    onEditProduct: (Product) -> Unit = {},
    onDeleteProduct: (Int) -> Unit = {}
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    var localSearchQuery by remember { mutableStateOf(searchQuery) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) } // Для хранения productId при удалении

    LaunchedEffect(localSearchQuery) {
        onSearchQueryChange(localSearchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Товары") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(6.dp)
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
                                value = localSearchQuery,
                                onValueChange = { newValue ->
                                    // Запрещаем перенос строки
                                    val singleLineValue = newValue.replace("\n", "")
                                    localSearchQuery = singleLineValue
                                    onSearchQueryChange(singleLineValue)
                                    // Сбрасываем результаты и скрываем поле, если запрос пустой
                                    if (singleLineValue.isEmpty()) {
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
                                        localSearchQuery = ""
                                        onSearchQueryChange("")
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
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(products) { _, product ->
                    ProductCard(
                        product = product,
                        onEditClick = { onEditProduct(product) },
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
                    onDeleteProduct(productId)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 600) // Планшетная ориентация
@Composable
fun ProductsScreenPreviewTablet() {
    val mockProducts = listOf(
        Product(1, "Кондиционер", "https://example.com/conditioner.jpg", 15000f, 10),
        Product(2, "Телевизор", "https://example.com/tv.jpg", 30000f, 5),
        Product(3, "Холодильник", "https://example.com/fridge.jpg", 25000f, 8),
        Product(4, "Микроволновка", "https://example.com/microwave.jpg", 8000f, 15)
    )

    ProductsScreenPreviewContent(
        products = mockProducts,
        errorMessage = "",
        onEditProduct = { /* Моковая реализация для превью */ },
        onDeleteProduct = { /* Моковая реализация для превью */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 400) // Телефонная ориентация
@Composable
fun ProductsScreenPreviewPhone() {
    val mockProducts = listOf(
        Product(1, "Кондиционер", "https://example.com/conditioner.jpg", 15000f, 10),
        Product(2, "Телевизор", "https://example.com/tv.jpg", 30000f, 5),
        Product(3, "Холодильник", "https://example.com/fridge.jpg", 25000f, 8),
        Product(4, "Микроволновка", "https://example.com/microwave.jpg", 8000f, 15)
    )

    ProductsScreenPreviewContent(
        products = mockProducts,
        errorMessage = "",
        onEditProduct = { /* Моковая реализация для превью */ },
        onDeleteProduct = { /* Моковая реализация для превью */ }
    )
}