package com.example.tsdmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectClientScreen(viewModel: AppViewModel, onClientSelected: (Client) -> Unit) {
    LaunchedEffect(Unit) { viewModel.loadClients() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор клиента") },
                actions = {
                    OutlinedTextField(
                        value = viewModel.searchQuery.value,
                        onValueChange = {
                            viewModel.searchQuery.value = it
                            viewModel.filterClients()
                        },
                        label = { Text("Поиск по имени") },
                        modifier = Modifier.padding(8.dp)
                    )
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
            LazyColumn {
                items(viewModel.clients.value) { client ->
                    ClientCard(
                        client = client,
                        onEditClick = {},
                        onDeleteClick = {},
                        onSelectClick = { onClientSelected(client) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClientCard(client: Client, onEditClick: () -> Unit, onDeleteClick: () -> Unit, onSelectClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelectClick?.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${client.lastName} ${client.firstName} ${client.middleName}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "Дата рождения: ${client.birthDate}")
                Text(text = "Телефон: ${client.phone}")
                Text(text = "Адрес: ${client.address}")
            }
            if (onEditClick != {}) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            } else {
                IconButton(onClick = onSelectClick ?: {}) {
                    Icon(Icons.Default.Add, contentDescription = "Выбрать")
                }
            }
        }
    }
}