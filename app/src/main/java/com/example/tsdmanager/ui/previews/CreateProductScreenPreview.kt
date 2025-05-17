package com.example.tsdmanager.ui.previews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.data.Product

@Composable
fun CreateProductScreenPreviewContent(
    name: String = "",
    price: String = "",
    stock: String = "",
    onNameChange: (String) -> Unit = {},
    onPriceChange: (String) -> Unit = {},
    onStockChange: (String) -> Unit = {},
    onProductCreated: (Product) -> Unit = {},
    onProductCreatedCallback: () -> Unit = {}
) {
    var localName by remember { mutableStateOf(name) }
    var localPrice by remember { mutableStateOf(price) }
    var localStock by remember { mutableStateOf(stock) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Создать товар", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = localName,
            onValueChange = {
                localName = it
                onNameChange(it)
            },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = localPrice,
            onValueChange = {
                localPrice = it
                onPriceChange(it)
            },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = localStock,
            onValueChange = {
                localStock = it
                onStockChange(it)
            },
            label = { Text("Запас") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = {
                val product = Product(0, localName, "", localPrice.toFloatOrNull() ?: 0f, localStock.toIntOrNull() ?: 0)
                onProductCreated(product)
                onProductCreatedCallback()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProductScreenPreview() {
    CreateProductScreenPreviewContent(
        name = "Товар 1",
        price = "100",
        stock = "10"
    )
}