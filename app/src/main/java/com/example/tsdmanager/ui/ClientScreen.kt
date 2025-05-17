package com.example.tsdmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import coil.compose.AsyncImage
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.data.Order
import com.example.tsdmanager.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(viewModel: AppViewModel, onCreateClient: () -> Unit, onEditClient: () -> Unit) {
    LaunchedEffect(Unit) { viewModel.loadClients() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Клиенты") },
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClient) {
                Icon(Icons.Default.Add, contentDescription = "Создать клиента")
            }
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
                        onEditClick = {
                            viewModel.selectedClient.value = client
                            onEditClient()
                        },
                        onDeleteClick = { viewModel.deleteClient(client.firstName, client.lastName) }
                    )
                }
            }
        }
    }
}