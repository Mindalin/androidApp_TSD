package com.example.tsdmanager.ui.previews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.data.Client

@Composable
fun ClientScreenPreviewContent(
    clients: List<Client> = emptyList(),
    errorMessage: String = "",
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onCreateClient: () -> Unit = {},
    onEditClient: (Client) -> Unit = {},
    onDeleteClient: (Client) -> Unit = {},
    onCallClick: (String) -> Unit = {}
) {
    var localSearchQuery by remember { mutableStateOf(searchQuery) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок и поиск
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Клиенты",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Иконка поиска или поле ввода
                if (isSearchExpanded) {
                    OutlinedTextField(
                        value = localSearchQuery,
                        onValueChange = {
                            localSearchQuery = it
                            onSearchQueryChange(it)
                        },
                        label = { Text("Поиск по имени") },
                        modifier = Modifier
                            .width(200.dp)
                            .height(56.dp),
                        trailingIcon = {
                            IconButton(onClick = { isSearchExpanded = false }) {
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
                // Кнопка добавления клиента
                IconButton(onClick = onCreateClient) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить клиента"
                    )
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Используем Column с прокруткой
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            clients.forEach { client ->
                ClientCardWithActionsPreview(
                    client = client,
                    onEditClick = { onEditClient(client) },
                    onDeleteClick = { onDeleteClient(client) },
                    onCallClick = { phone -> onCallClick(phone) }
                )
            }
        }
    }
}

@Composable
fun ClientCardWithActionsPreview(
    client: Client,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCallClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Основная информация
                Text(
                    text = "${client.lastName} ${client.firstName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                client.middleName?.let {
                    Text(
                        text = "Отчество: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Подробная информация (всегда раскрыта)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Дата рождения: ${client.birthDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Телефон: ${client.phone}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Адрес: ${client.address}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                // Кнопка "Редактировать"
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать"
                    )
                }
                // Кнопка "Удалить"
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить"
                    )
                }
                // Кнопка "Позвонить"
                IconButton(onClick = {
                    onCallClick(client.phone)
                }) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Позвонить"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFFFF, device = "id:pixel_5")
@Composable
fun ClientScreenPreview() {
    val mockClients = listOf(
        Client(1, "Иван", "Петров", "Ович", "1990-01-01", "+79991234567", "Москва"),
        Client(2, "Мария", "Иванова", "Петровна", "1995-05-05", "+79997654321", "Санкт-Петербург")
    )

    ClientScreenPreviewContent(clients = mockClients)
}