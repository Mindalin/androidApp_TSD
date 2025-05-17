package com.example.tsdmanager.data

import com.example.tsdmanager.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ClientApi {
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Token

    @GET("clients")
    suspend fun getClients(): List<Client>

    @POST("clients")
    @FormUrlEncoded
    suspend fun createClient(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("middle_name") middleName: String,
        @Field("birth_date") birthDate: String,
        @Field("phone") phone: String,
        @Field("address") address: String
    ): Response<Client>

    @PUT("clients/by-name")
    @FormUrlEncoded
    suspend fun updateClient(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("new_first_name") newFirstName: String?,
        @Field("new_last_name") newLastName: String?,
        @Field("new_middle_name") newMiddleName: String?,
        @Field("new_birth_date") newBirthDate: String?,
        @Field("new_phone") newPhone: String?,
        @Field("new_address") newAddress: String?
    ): Response<Client>

    @DELETE("clients/by-name")
    @FormUrlEncoded
    suspend fun deleteClient(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String
    ): Response<Client>

    @GET("products")
    suspend fun getProducts(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): List<Product>

    @POST("products")
    @Multipart
    suspend fun createProduct(
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Product>

    @PUT("products/by-name")
    @FormUrlEncoded
    suspend fun updateProduct(
        @Field("request") request: String
    ): Response<Product>

    @PUT("products/by-name/image")
    @Multipart
    suspend fun updateProductImage(
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Product>

    @DELETE("products/by-name")
    @FormUrlEncoded
    suspend fun deleteProduct(
        @Field("name") name: String
    ): Response<Product>

    @GET("orders")
    suspend fun getOrders(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): List<Order>

    @POST("orders")
    suspend fun createOrder(@Body order: OrderCreate): Response<Order>

    @GET("search/orders/{identifier}")
    suspend fun getOrderByIdentifier(@Path("identifier") identifier: String): Order

    @POST("orders/by-identifier/{identifier}/items")
    @FormUrlEncoded
    suspend fun addItemToOrder(
        @Path("identifier") identifier: String,
        @Field("product_name") productName: String,
        @Field("quantity") quantity: Int
    ): Response<Order>

    @PATCH("orders/by-identifier/{identifier}/items/by-name")
    @FormUrlEncoded
    suspend fun updateItemQuantity(
        @Path("identifier") identifier: String,
        @Field("product_name") productName: String,
        @Field("quantity") quantity: Int
    ): Response<Order>

    @PATCH("orders/by-identifier/{identifier}/status")
    @FormUrlEncoded
    suspend fun updateOrderStatus(
        @Path("identifier") identifier: String,
        @Field("status") status: String
    ): Response<Order>

    @DELETE("orders/{identifier}")
    suspend fun deleteOrder(@Path("identifier") identifier: String): Response<Map<String, String>>

    @GET("orders/{identifier}/receipt")
    suspend fun downloadReceipt(@Path("identifier") identifier: String): Response<ResponseBody>

    @GET("users/me")
    suspend fun getCurrentUser(): UserResponse
}