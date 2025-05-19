package com.example.tsdmanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Client

@Composable
fun SelectClientScreen(
    viewModel: AppViewModel,
    onClientSelected: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    val clients by viewModel.clients
    val searchQuery by viewModel.searchQuery
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
                text = "Выбор клиента",
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
                        value = searchQuery,
                        onValueChange = { newValue ->
                            // Запрещаем перенос строки
                            val singleLineValue = newValue.replace("\n", "")
                            viewModel.searchQuery.value = singleLineValue
                            viewModel.filterClientsForSelection()
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
                // Кнопка "Перейти к товарам" (перенесена справа от поиска)
                if (viewModel.selectedClientForOrder.value != null) {
                    IconButton(
                        onClick = {
                            onClientSelected()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Перейти к товарам"
                        )
                    }
                }
            }
        }

        // Сообщение об ошибке
        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage.value,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Список клиентов
        LazyColumn {
            items(
                items = clients,
                key = { client -> client.id }
            ) {
                ClientCard(
                    client = it,
                    isSelected = it == viewModel.selectedClientForOrder.value,
                    onClientClick = {
                        viewModel.selectedClientForOrder.value = it
                    }
                )
            }
        }
    }
}

@Composable
fun ClientCard(
    client: Client,
    isSelected: Boolean,
    onClientClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClientClick() }
            .background(
                if (isSelected) Color(0xFFB3E5FC) else Color.Transparent, // Светло-голубой цвет для выделения
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = Color(0xFF0288D1), // Тёмно-голубая обводка
                    shape = RoundedCornerShape(8.dp)
                ) else Modifier
            ),
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

                // Подробная информация (свёрнутая по умолчанию)
                if (isExpanded) {
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
            }

            // Кнопка "Подробнее"
            IconButton(
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = if (isExpanded) "Свернуть" else "Подробнее"
                )
            }
        }
    }
}