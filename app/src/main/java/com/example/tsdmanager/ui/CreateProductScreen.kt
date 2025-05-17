package com.example.tsdmanager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Client
import com.example.tsdmanager.data.Product
import java.io.File

@Composable
fun CreateProductScreen(viewModel: AppViewModel, onProductCreated: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageFile by remember { mutableStateOf<File?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Создать товар", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Запас") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = {
                // Здесь нужно добавить логику выбора файла изображения
                // Для примера предположим, что imageFile уже выбран
                imageFile?.let { file ->
                    val product = Product(0, name, "", price.toFloatOrNull() ?: 0f, stock.toIntOrNull() ?: 0)
                    viewModel.createProduct(product, file)
                    onProductCreated()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать")
        }
    }
}