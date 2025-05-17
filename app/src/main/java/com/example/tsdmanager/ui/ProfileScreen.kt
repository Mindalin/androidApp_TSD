package com.example.tsdmanager.ui

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
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Order
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: AppViewModel, onOrderDetails: (String) -> Unit) {
    LaunchedEffect(Unit) { viewModel.loadOrders() }

    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf(viewModel.searchQuery.value) }
    val coroutineScope = rememberCoroutineScope()
    var inactivityJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(searchQuery) {
        viewModel.searchQuery.value = searchQuery
        viewModel.filterOrders()

        inactivityJob?.cancel()
        if (searchQuery.isNotEmpty()) {
            inactivityJob = coroutineScope.launch {
                delay(3000) // 3 секунды неактивности
                if (searchQuery.isEmpty()) {
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
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Поиск по идентификатору") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage.value,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        LazyColumn {
            items(viewModel.orders.value) { order ->
                OrderCard(order, onClick = { onOrderDetails(order.identifier) })
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.identifier,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}