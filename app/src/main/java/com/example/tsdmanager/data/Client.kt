package com.example.tsdmanager.data

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("middle_name") val middleName: String,
    @SerializedName("birth_date") val birthDate: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String
)