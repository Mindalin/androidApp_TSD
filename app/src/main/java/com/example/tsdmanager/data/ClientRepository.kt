package com.example.tsdmanager.data

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class ClientRepository(private val token: String) {
    private val clientApi: ClientApi

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://46.8.224.199:8000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        clientApi = retrofit.create(ClientApi::class.java)
    }

    suspend fun login(username: String, password: String): Token {
        return clientApi.login(username, password)
    }

    suspend fun getClients(): List<Client> {
        return clientApi.getClients()
    }

    suspend fun createClient(client: Client): Response<Client> {
        return clientApi.createClient(
            client.firstName,
            client.lastName,
            client.middleName ?: "",
            client.birthDate,
            client.phone,
            client.address
        )
    }

    suspend fun updateClient(
        firstName: String,
        lastName: String,
        newClient: Client
    ): Response<Client> {
        val updateRequest = ClientUpdateRequest(
            first_name = firstName,
            last_name = lastName,
            new_first_name = newClient.firstName,
            new_last_name = newClient.lastName,
            new_middle_name = newClient.middleName,
            new_birth_date = newClient.birthDate,
            new_phone = newClient.phone,
            new_address = newClient.address
        )
        return clientApi.updateClient(updateRequest)
    }

    suspend fun deleteClient(clientId: Int): Response<Client> {
        return clientApi.deleteClient(clientId)
    }

    suspend fun getProducts(skip: Int, limit: Int): List<Product> {
        return clientApi.getProducts(skip, limit)
    }

    suspend fun createProduct(product: Product, imageFile: File): Response<Product> {
        val name = product.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val price = product.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val stock = product.stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
        return clientApi.createProduct(name, price, stock, imagePart)
    }

    suspend fun updateProduct(product: Product): Response<Product> {
        val request = ProductUpdate(
            name = product.name,
            newName = product.name,
            newPrice = product.price,
            newStock = product.stock
        )
        return clientApi.updateProduct(Gson().toJson(request))
    }

    suspend fun updateProductImage(name: String, imageFile: File): Response<Product> {
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
        return clientApi.updateProductImage(nameBody, imagePart)
    }

    suspend fun deleteProduct(productId: Int): Response<Product> {
        return clientApi.deleteProduct(productId)
    }

    suspend fun getOrders(skip: Int, limit: Int): List<Order> {
        return clientApi.getOrders(skip, limit)
    }

    suspend fun createOrder(order: OrderCreate): Response<Order> {
        return clientApi.createOrder(order)
    }

    suspend fun getOrderByIdentifier(identifier: String): Order {
        return clientApi.getOrderByIdentifier(identifier)
    }

    suspend fun addItemToOrder(identifier: String, productName: String, quantity: Int): Response<Order> {
        return clientApi.addItemToOrder(identifier, productName, quantity)
    }

    suspend fun updateItemQuantity(identifier: String, productName: String, quantity: Int): Response<Order> {
        return clientApi.updateItemQuantity(identifier, productName, quantity)
    }

    suspend fun updateOrderStatus(identifier: String, status: String): Response<Order> {
        return clientApi.updateOrderStatus(identifier, status)
    }

    suspend fun deleteOrder(identifier: String): Response<Map<String, String>> {
        return clientApi.deleteOrder(identifier)
    }

    suspend fun downloadReceipt(identifier: String): Response<ResponseBody> {
        return clientApi.downloadReceipt(identifier)
    }

    suspend fun validateToken(): Boolean {
        return try {
            clientApi.getCurrentUser()
            true
        } catch (e: Exception) {
            false
        }
    }
}