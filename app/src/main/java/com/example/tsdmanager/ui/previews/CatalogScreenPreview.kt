package com.example.tsdmanager.ui.previews

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tsdmanager.data.Product
import com.example.tsdmanager.ui.ProductCard

private const val VDS_SERVER = "http://46.8.224.199:8000"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreenPreviewContent(
    products: List<Product> = emptyList(),
    cartState: Map<Int, Int> = emptyMap(),
    errorMessage: String = "",
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onCartIncrease: (Int) -> Unit = {},
    onCartDecrease: (Int) -> Unit = {},
    onCreateOrder: () -> Unit = {}
) {
    var page by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var localSearchQuery by remember { mutableStateOf(searchQuery) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Каталог") },
                actions = {
                    OutlinedTextField(
                        value = localSearchQuery,
                        onValueChange = {
                            localSearchQuery = it
                            onSearchQueryChange(it)
                        },
                        label = { Text("Поиск по названию") },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateOrder,
                modifier = Modifier.padding(16.dp),
                content = { Icon(Icons.Default.Check, contentDescription = "Создать заказ") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn {
                itemsIndexed(products) { _, product ->
                    ProductCard(
                        product = product,
                        quantity = cartState[product.id] ?: 0,
                        onIncrease = { onCartIncrease(product.id) },
                        onDecrease = { onCartDecrease(product.id) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CatalogScreenPreview() {
    val mockProducts = listOf(
        Product(1, "Товар 1", "image1.jpg", 100f, 10),
        Product(2, "Товар 2", "image2.jpg", 200f, 5)
    )
    val mockCartState = mapOf(1 to 2, 2 to 1)

    CatalogScreenPreviewContent(
        products = mockProducts,
        cartState = mockCartState,
        errorMessage = ""
    )
}