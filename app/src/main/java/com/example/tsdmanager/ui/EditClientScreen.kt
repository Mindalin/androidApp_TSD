package com.example.tsdmanager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.AppViewModel
import com.example.tsdmanager.data.Client
import java.io.File

@Composable
fun EditClientScreen(viewModel: AppViewModel, onClientUpdated: () -> Unit) {
    val client = viewModel.selectedClient.value ?: return

    var firstName by remember { mutableStateOf(client.firstName) }
    var lastName by remember { mutableStateOf(client.lastName) }
    var middleName by remember { mutableStateOf(client.middleName) }
    var birthDate by remember { mutableStateOf(client.birthDate) }
    var phone by remember { mutableStateOf(client.phone) }
    var address by remember { mutableStateOf(client.address) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Редактировать клиента", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Фамилия") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Отчество") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Дата рождения (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Адрес") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val updatedClient = Client(0, firstName, lastName, middleName, birthDate, phone, address)
                viewModel.updateClient(client.firstName, client.lastName, updatedClient)
                onClientUpdated()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}