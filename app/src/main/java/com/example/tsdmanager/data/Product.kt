package com.example.tsdmanager.data

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("price") val price: Float,
    @SerializedName("stock") val stock: Int
)

data class ProductUpdate(
    @SerializedName("name") val name: String,
    @SerializedName("new_name") val newName: String?,
    @SerializedName("new_price") val newPrice: Float?,
    @SerializedName("new_stock") val newStock: Int?
)