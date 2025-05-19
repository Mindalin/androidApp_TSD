package com.example.tsdmanager.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Client

@Composable
fun ClientScreen(
    viewModel: AppViewModel,
    onCreateClient: () -> Unit,
    onEditClient: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    val clients by viewModel.clients
    val searchQuery by viewModel.searchQuery
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) } // Для хранения clientId при удалении
    val context = androidx.compose.ui.platform.LocalContext.current

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
                // Кнопка добавления клиента
                IconButton(onClick = onCreateClient) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить клиента"
                    )
                }
                // Иконка поиска или поле ввода
                if (isSearchExpanded) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            // Запрещаем перенос строки
                            val singleLineValue = newValue.replace("\n", "")
                            viewModel.searchQuery.value = singleLineValue
                            viewModel.filterClients()
                            // Сбрасываем результаты и скрываем поле, если запрос пустой
                            if (singleLineValue.isEmpty()) {
                                viewModel.loadClients()
                                isSearchExpanded = false
                            }
                        },
                        label = { Text("Поиск по имени") },
                        modifier = Modifier
                            .width(200.dp)
                            .height(56.dp),
                        singleLine = true, // Запрещаем многострочный ввод
                        trailingIcon = {
                            IconButton(onClick = {
                                isSearchExpanded = false
                                viewModel.searchQuery.value = ""
                                viewModel.loadClients() // Сбрасываем результаты
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

        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage.value,
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
                ClientCardWithActions(
                    client = client,
                    onEditClick = {
                        viewModel.selectedClient.value = client
                        onEditClient()
                    },
                    onDeleteClick = { showDeleteDialog = client.id },
                    onCallClick = { phone ->
                        startDialer(context, phone)
                    }
                )
            }
        }
    }

    // Диалог подтверждения удаления
    showDeleteDialog?.let { clientId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить этого клиента?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteClient(clientId)
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

// Отдельная функция для запуска Intent
fun startDialer(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    context.startActivity(intent)
}

@Composable
fun ClientCardWithActions(
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
                    text = "${client.lastName} ${client.firstName} ${client.middleName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

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