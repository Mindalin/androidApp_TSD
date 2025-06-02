package com.example.tsdmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Order

@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onOrderDetails: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    val orders by viewModel.orders
    val searchQuery by viewModel.searchQuery
    var isSearchExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Заказы",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            if (isSearchExpanded) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newValue ->
                        // Запрещаем перенос строки
                        val singleLineValue = newValue.replace("\n", "")
                        viewModel.searchQuery.value = singleLineValue
                        viewModel.filterOrders()
                        // Сбрасываем результаты, если запрос пустой
                        if (singleLineValue.isEmpty()) {
                            viewModel.loadOrders()
                            isSearchExpanded = false
                        }
                    },
                    label = { Text("Поиск") },
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp),
                    singleLine = true, // Запрещаем многострочный ввод
                    trailingIcon = {
                        IconButton(onClick = {
                            isSearchExpanded = false
                            viewModel.searchQuery.value = ""
                            viewModel.loadOrders() // Сбрасываем результаты
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

        LazyColumn {
            items(orders) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOrderDetails(order.identifier) }
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${order.identifier}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}