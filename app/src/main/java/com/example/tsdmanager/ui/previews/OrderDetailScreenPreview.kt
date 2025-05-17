package com.example.tsdmanager.ui.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.data.Order
import com.example.tsdmanager.data.OrderItem
import com.example.tsdmanager.data.Product
import com.example.tsdmanager.ui.OrderItemCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreenPreviewContent(
    order: Order,
    errorMessage: String = "",
    newStatus: String = order.status,
    newProductName: String = "",
    newQuantity: String = "",
    onStatusChange: (String) -> Unit = {},
    onProductNameChange: (String) -> Unit = {},
    onQuantityChange: (String) -> Unit = {},
    onUpdateStatus: (String) -> Unit = {},
    onDeleteOrder: () -> Unit = {},
    onDownloadReceipt: () -> Unit = {},
    onUpdateQuantity: (OrderItem, Int) -> Unit = { _, _ -> },
    onRemoveItem: (OrderItem) -> Unit = { _ -> },
    onAddItem: (String, Int) -> Unit = { _, _ -> },
    onOrderDeleted: () -> Unit = {}
) {
    var localNewStatus by remember { mutableStateOf(newStatus) }
    var localNewProductName by remember { mutableStateOf(newProductName) }
    var localNewQuantity by remember { mutableStateOf(newQuantity) }
    var showOrderDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("${order.identifier}") },
                navigationIcon = {
                    IconButton(onClick = { onOrderDeleted() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = order.status,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (order.status) {
                                        "pending" -> Color(0xFFFFA500)
                                        "ready" -> Color.Green
                                        "shipped" -> Color.Gray
                                        else -> Color.Black
                                    }
                                )
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = { showOrderDeleteDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Red)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Удалить заказ",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 10.dp)
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        value = localNewStatus,
                        onValueChange = {},
                        label = { Text("Изменить статус") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("pending", "ready", "shipped").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    localNewStatus = status
                                    onStatusChange(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { onUpdateStatus(localNewStatus) },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Обновить")
                }
            }
            Text(
                text = "Клиент: ${order.client.lastName} ${order.client.firstName} ${order.client.middleName}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(order.items) { item ->
                    OrderItemCard(
                        item = item,
                        onUpdateQuantity = { qty -> onUpdateQuantity(item, qty) },
                        onRemove = { onRemoveItem(item) },
                        onShowSnackbar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
            }
            Column(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                OutlinedTextField(
                    value = localNewProductName,
                    onValueChange = {
                        localNewProductName = it
                        onProductNameChange(it)
                    },
                    label = { Text("Название товара") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = localNewQuantity,
                    onValueChange = {
                        localNewQuantity = it
                        onQuantityChange(it)
                    },
                    label = { Text("Количество") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onAddItem(localNewProductName, localNewQuantity.toIntOrNull() ?: 0)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Добавить")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onDownloadReceipt() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Скачать чек")
                }
            }
        }
    }

    if (showOrderDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showOrderDeleteDialog = false },
            title = { Text("Подтверждение удаления заказа") },
            text = { Text("Вы уверены, что хотите удалить этот заказ?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteOrder()
                        showOrderDeleteDialog = false
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOrderDeleteDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
}

@Composable
fun OrderItemCard(
    item: OrderItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    var quantity by remember { mutableStateOf(item.quantity) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val stock = item.product.stock // Доступный запас товара

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                IconButton(
                    onClick = {
                        if (quantity > 1) { // Минимальное количество 1
                            quantity -= 1
                            onUpdateQuantity(quantity)
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Уменьшить")
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                    modifier = Modifier
                        .width(40.dp)
                        .padding(horizontal = 4.dp),
                    textAlign = TextAlign.Center
                )
                if (quantity < stock) {
                    IconButton(
                        onClick = {
                            quantity += 1
                            onUpdateQuantity(quantity)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Увеличить")
                    }
                } else {
                    IconButton(
                        onClick = {
                            onShowSnackbar("Товара недостаточно на складе")
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Увеличить", tint = Color.Gray)
                    }
                }
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить этот товар из заказа?") },
            confirmButton = {
                Button(
                    onClick = {
                        onRemove()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 600) // Планшетная ориентация
@Composable
fun OrderDetailScreenPreviewTablet() {
    val mockOrder = Order(
        id = 1,
        identifier = "ШAA00001",
        status = "pending",
        clientId = 1,
        client = Client(1, "Иван", "Петров", "Ович", "1990-01-01", "+79991234567", "Москва"),
        items = listOf(
            OrderItem(1, 1, 1, 2, Product(1, "Товар 1", "image1.jpg", 100f, 10))
        )
    )

    OrderDetailScreenPreviewContent(order = mockOrder)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 400) // Телефонная ориентация
@Composable
fun OrderDetailScreenPreviewPhone() {
    val mockOrder = Order(
        id = 1,
        identifier = "ШAA00001",
        status = "pending",
        clientId = 1,
        client = Client(1, "Иван", "Петров", "Ович", "1990-01-01", "+79991234567", "Москва"),
        items = listOf(
            OrderItem(1, 1, 1, 2, Product(1, "Товар 1", "image1.jpg", 100f, 10)),
            OrderItem(1, 1, 1, 2, Product(1, "Товар 1", "image1.jpg", 100f, 10)),
            OrderItem(1, 1, 1, 2, Product(1, "Товар 1", "image1.jpg", 100f, 10)),
            OrderItem(1, 1, 1, 2, Product(1, "Товар 1", "image1.jpg", 100f, 10)),
            OrderItem(1, 1, 1, 2, Product(1, "Товар 4", "image1.jpg", 100f, 10))
        )
    )

    OrderDetailScreenPreviewContent(order = mockOrder)
}