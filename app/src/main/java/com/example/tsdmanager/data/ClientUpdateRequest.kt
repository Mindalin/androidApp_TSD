package com.example.tsdmanager.data

data class ClientUpdateRequest(
    val first_name: String,
    val last_name: String,
    val new_first_name: String?,
    val new_last_name: String?,
    val new_middle_name: String?,
    val new_birth_date: String?,
    val new_phone: String?,
    val new_address: String?
)
