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
fun EditProductScreenPreviewContent(
    product: Product,
    onNameChange: (String) -> Unit = {},
    onPriceChange: (String) -> Unit = {},
    onStockChange: (String) -> Unit = {},
    onProductUpdated: (Product) -> Unit = {},
    onProductUpdatedCallback: () -> Unit = {}
) {
    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Редактировать товар", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onNameChange(it)
            },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
                onPriceChange(it)
            },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = stock,
            onValueChange = {
                stock = it
                onStockChange(it)
            },
            label = { Text("Запас") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = {
                val updatedProduct = Product(product.id, name, product.image, price.toFloatOrNull() ?: 0f, stock.toIntOrNull() ?: 0)
                onProductUpdated(updatedProduct)
                onProductUpdatedCallback()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProductScreenPreview() {
    val mockProduct = Product(1, "Товар 1", "image1.jpg", 100f, 10)
    EditProductScreenPreviewContent(product = mockProduct)
}