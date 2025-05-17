package com.example.tsdmanager.ui.previews

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsdmanager.data.Client

@Composable
fun EditClientScreenPreviewContent(
    client: Client,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onMiddleNameChange: (String) -> Unit = {},
    onBirthDateChange: (String) -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onAddressChange: (String) -> Unit = {},
    onClientUpdated: (Client) -> Unit = {},
    onClientUpdatedCallback: () -> Unit = {}
) {
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
            onValueChange = {
                firstName = it
                onFirstNameChange(it)
            },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                onLastNameChange(it)
            },
            label = { Text("Фамилия") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = middleName,
            onValueChange = {
                middleName = it
                onMiddleNameChange(it)
            },
            label = { Text("Отчество") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = birthDate,
            onValueChange = {
                birthDate = it
                onBirthDateChange(it)
            },
            label = { Text("Дата рождения (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                onPhoneChange(it)
            },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
                onAddressChange(it)
            },
            label = { Text("Адрес") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val updatedClient = Client(0, firstName, lastName, middleName, birthDate, phone, address)
                onClientUpdated(updatedClient)
                onClientUpdatedCallback()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditClientScreenPreview() {
    val mockClient = Client(1, "Иван", "Петров", "Ович", "1990-01-01", "+79991234567", "Москва")
    EditClientScreenPreviewContent(client = mockClient)
}