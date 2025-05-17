package com.example.tsdmanager.ui.previews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.data.Order
import com.example.tsdmanager.ui.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenPreviewContent(
    orders: List<Order> = emptyList(),
    errorMessage: String = "",
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSelectClient: () -> Unit = {},
    onOrderDetails: (String) -> Unit = {}
) {
    var localSearchQuery by remember { mutableStateOf(searchQuery) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заказы") },
                actions = {
                    OutlinedTextField(
                        value = localSearchQuery,
                        onValueChange = {
                            localSearchQuery = it
                            onSearchQueryChange(it)
                        },
                        label = { Text("Поиск по идентификатору") },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSelectClient) {
                Icon(Icons.Default.Add, contentDescription = "Создать заказ")
            }
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
                items(orders) { order ->
                    OrderCard(order, onClick = { onOrderDetails(order.identifier) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val mockOrders = listOf(
        Order(1, "Ш00001", "pending", 1, Client(1, "Иван", "Петров", "Ович", "1990-01-01", "+79991234567", "Москва"), emptyList()),
        Order(2, "ААА00001", "ready", 2, Client(2, "Мария", "Иванова", "Петровна", "1995-05-05", "+79997654321", "Санкт-Петербург"), emptyList()),
        Order(3, "ААА00002", "shipped", 1, Client(1, "Иван", "Петров", "Ович", "1990-01-01", "+79991234567", "Москва"), emptyList())
    )

    ProfileScreenPreviewContent(
        orders = mockOrders,
        errorMessage = ""
    )
}