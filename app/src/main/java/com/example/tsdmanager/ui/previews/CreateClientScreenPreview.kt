package com.example.tsdmanager.ui.previews

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tsdmanager.data.Client

@Composable
fun CreateClientScreenPreviewContent(
    firstName: String = "",
    lastName: String = "",
    middleName: String = "",
    birthDate: String = "",
    phone: String = "",
    address: String = "",
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onMiddleNameChange: (String) -> Unit = {},
    onBirthDateChange: (String) -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onAddressChange: (String) -> Unit = {},
    onCreateClient: (Client) -> Unit = {},
    onClientCreated: () -> Unit = {}
) {
    var localFirstName by remember { mutableStateOf(firstName) }
    var localLastName by remember { mutableStateOf(lastName) }
    var localMiddleName by remember { mutableStateOf(middleName) }
    var localBirthDate by remember { mutableStateOf(birthDate) }
    var localPhone by remember { mutableStateOf(phone) }
    var localAddress by remember { mutableStateOf(address) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Создать клиента", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = localFirstName,
            onValueChange = {
                localFirstName = it
                onFirstNameChange(it)
            },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = localLastName,
            onValueChange = {
                localLastName = it
                onLastNameChange(it)
            },
            label = { Text("Фамилия") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = localMiddleName,
            onValueChange = {
                localMiddleName = it
                onMiddleNameChange(it)
            },
            label = { Text("Отчество") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = localBirthDate,
            onValueChange = {
                localBirthDate = it
                onBirthDateChange(it)
            },
            label = { Text("Дата рождения (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = localPhone,
            onValueChange = {
                localPhone = it
                onPhoneChange(it)
            },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = localAddress,
            onValueChange = {
                localAddress = it
                onAddressChange(it)
            },
            label = { Text("Адрес") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val client = Client(0, localFirstName, localLastName, localMiddleName, localBirthDate, localPhone, localAddress)
                onCreateClient(client)
                onClientCreated()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateClientScreenPreview() {
    CreateClientScreenPreviewContent(
        firstName = "Иван",
        lastName = "Петров",
        middleName = "Ович",
        birthDate = "1990-01-01",
        phone = "+79991234567",
        address = "Москва"
    )
}