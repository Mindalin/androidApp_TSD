package com.example.tsdmanager.data

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: Int,
    @SerializedName("identifier") val identifier: String,
    @SerializedName("status") val status: String,
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("client") val client: Client,
    @SerializedName("items") val items: List<OrderItem>
)

data class OrderItem(
    @SerializedName("id") val id: Int,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: Product
)

data class OrderCreate(
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("items") val items: List<OrderItemCreate>
)

data class OrderItemCreate(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int
)