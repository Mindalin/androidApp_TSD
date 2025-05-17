package com.example.tsdmanager.ui.previews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.ui.ClientCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreenPreviewContent(
    clients: List<Client> = emptyList(),
    errorMessage: String = "",
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onEditClient: (Client) -> Unit = {},
    onDeleteClient: (Client) -> Unit = {},
    onCreateClient: () -> Unit = {}
) {
    var localSearchQuery by remember { mutableStateOf(searchQuery) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Клиенты") },
                actions = {
                    OutlinedTextField(
                        value = localSearchQuery,
                        onValueChange = {
                            localSearchQuery = it
                            onSearchQueryChange(it)
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
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn {
                items(clients) { client ->
                    ClientCard(
                        client = client,
                        onEditClick = { onEditClient(client) },
                        onDeleteClick = { onDeleteClient(client) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ClientScreenPreview() {
    val mockClients = listOf(
        Client(1, "Иван", "Петров", "Ович", "1990-01-01", "+79991234567", "Москва"),
        Client(2, "Мария", "Иванова", "Петровна", "1995-05-05", "+79997654321", "Санкт-Петербург")
    )

    ClientScreenPreviewContent(
        clients = mockClients,
        errorMessage = ""
    )
}