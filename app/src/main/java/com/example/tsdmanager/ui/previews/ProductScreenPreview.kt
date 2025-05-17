package com.example.tsdmanager.ui.previews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.data.Order
import com.example.tsdmanager.ui.OrderCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenPreviewContent(
    orders: List<Order> = emptyList(),
    errorMessage: String = "",
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onOrderDetails: (String) -> Unit = {}
) {
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var localSearchQuery by rememberSaveable { mutableStateOf(searchQuery) }
    val coroutineScope = rememberCoroutineScope()
    var inactivityJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(localSearchQuery) {
        onSearchQueryChange(localSearchQuery)
        inactivityJob?.cancel()
        if (localSearchQuery.isNotEmpty()) {
            inactivityJob = coroutineScope.launch {
                delay(3000)
                if (localSearchQuery.isEmpty()) {
                    isSearchVisible = false
                }
            }
        } else {
            isSearchVisible = false
        }
    }

    Column {
        TopAppBar(
            title = { Text("Мои заказы") },
            actions = {
                IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                    Icon(Icons.Default.Search, contentDescription = "Поиск")
                }
            }
        )
        AnimatedVisibility(
            visible = isSearchVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            OutlinedTextField(
                value = localSearchQuery,
                onValueChange = { localSearchQuery = it },
                label = { Text("Поиск по идентификатору") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 600) // Планшетная ориентация
@Composable
fun ProfileScreenPreviewTablet() {
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 400) // Телефонная ориентация
@Composable
fun ProfileScreenPreviewPhone() {
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