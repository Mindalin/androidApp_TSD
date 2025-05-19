package com.example.tsdmanager

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsdmanager.data.*
import com.example.tsdmanager.data.ClientRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.launch
import java.io.File
import java.nio.charset.StandardCharsets

class AppViewModel(private val context: Context) : ViewModel() {
    private var repository: ClientRepository? = null

    val isLoggedIn = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    val clients = mutableStateOf<List<Client>>(emptyList())
    val searchQuery = mutableStateOf("")
    val products = mutableStateOf<List<Product>>(emptyList())
    private val originalProducts = mutableStateOf<List<Product>>(emptyList())
    val orders = mutableStateOf<List<Order>>(emptyList())
    val selectedClient = mutableStateOf<Client?>(null)
    val selectedClientForOrder = mutableStateOf<Client?>(null)
    val selectedProduct = mutableStateOf<Product?>(null)
    val cart = mutableMapOf<Int, Int>()
    val cartState = mutableStateOf<Map<Int, Int>>(emptyMap())
    val currentOrder = mutableStateOf<Order?>(null)

    // Состояние загрузки
    val isLoading = mutableStateOf(false)

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val JWT_TOKEN_KEY = "jwt_token"
    private val JWT_EXPIRY_KEY = "jwt_expiry"
    private val SECRET_KEY = BuildConfig.SECRET_KEY

    fun initRepository(token: String) {
        repository = ClientRepository(token)
    }

    suspend fun login(username: String, password: String): Boolean {
        return try {
            isLoading.value = true
            val response = ClientRepository("").login(username, password)
            val token = response.accessToken
            saveJwtToken(token)
            initRepository(token)
            isLoggedIn.value = true
            isLoading.value = false
            true
        } catch (e: Exception) {
            isLoading.value = false
            errorMessage.value = "Ошибка входа: ${e.message}"
            false
        }
    }

    private fun saveJwtToken(token: String) {
        try {
            val key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray(StandardCharsets.UTF_8))
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            val expiry = claims.expiration.time / 1000
            sharedPreferences.edit().apply {
                putString(JWT_TOKEN_KEY, token)
                putLong(JWT_EXPIRY_KEY, expiry)
                apply()
            }
        } catch (e: Exception) {
            errorMessage.value = "Ошибка обработки токена: ${e.message}"
        }
    }

    fun getJwtToken(): String? {
        return sharedPreferences.getString(JWT_TOKEN_KEY, null)
    }

    private fun getJwtExpiry(): Long {
        return sharedPreferences.getLong(JWT_EXPIRY_KEY, 0)
    }

    fun clearJwtToken() {
        sharedPreferences.edit().apply {
            remove(JWT_TOKEN_KEY)
            remove(JWT_EXPIRY_KEY)
            apply()
        }
        repository = null
        isLoggedIn.value = false
    }

    fun isJwtTokenValidLocally(): Boolean {
        val token = getJwtToken() ?: return false
        val expiry = getJwtExpiry()
        val currentTime = System.currentTimeMillis() / 1000
        return token.isNotEmpty() && expiry > currentTime
    }

    suspend fun isJwtTokenValidOnServer(): Boolean {
        return try {
            isLoading.value = true
            val result = repository?.validateToken() == true
            isLoading.value = false
            result
        } catch (e: Exception) {
            isLoading.value = false
            errorMessage.value = "Ошибка проверки токена: ${e.message}"
            false
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                orders.value = repository?.getOrders(0, 1000) ?: emptyList()
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка загрузки заказов: ${e.message}"
            }
        }
    }

    fun filterOrders() {
        val query = searchQuery.value.lowercase()
        val filtered = orders.value.filter { order ->
            order.identifier.lowercase().contains(query)
        }
        orders.value = filtered
    }

    fun filterClientsForSelection() {
        val query = searchQuery.value.lowercase()
        val filtered = clients.value.filter { client ->
            client.firstName.lowercase().contains(query) ||
                    client.lastName.lowercase().contains(query) ||
                    (client.middleName?.lowercase()?.contains(query) ?: false)
        }
        clients.value = filtered
    }

    fun createOrder(clientId: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val items = cart.map { (productId, quantity) ->
                    OrderItemCreate(productId = productId, quantity = quantity)
                }
                val order = OrderCreate(clientId = clientId, status = "pending", items = items)
                repository?.createOrder(order)
                cart.clear()
                cartState.value = emptyMap()
                loadOrders()
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка создания заказа: ${e.message}"
            }
        }
    }

    fun loadOrderDetails(identifier: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                currentOrder.value = repository?.getOrderByIdentifier(identifier)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка загрузки деталей заказа: ${e.message}"
            }
        }
    }

    fun addItemToOrder(identifier: String, productName: String, quantity: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.addItemToOrder(identifier, productName, quantity)
                loadOrderDetails(identifier)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка добавления товара: ${e.message}"
            }
        }
    }

    fun updateItemQuantity(identifier: String, productName: String, quantity: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.updateItemQuantity(identifier, productName, quantity)
                loadOrderDetails(identifier)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка обновления количества: ${e.message}"
            }
        }
    }

    fun updateOrderStatus(identifier: String, status: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.updateOrderStatus(identifier, status)
                loadOrderDetails(identifier)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка обновления статуса: ${e.message}"
            }
        }
    }

    fun deleteOrder(identifier: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.deleteOrder(identifier)
                loadOrders()
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка удаления заказа: ${e.message}"
            }
        }
    }

    fun downloadReceipt(identifier: String, onSuccess: (File) -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = repository?.downloadReceipt(identifier)
                if (response?.isSuccessful == true) {
                    val file = File(context.filesDir, "receipt_$identifier.pdf")
                    file.writeBytes(response.body()?.bytes() ?: byteArrayOf())
                    onSuccess(file)
                } else {
                    errorMessage.value = "Ошибка скачивания чека: ${response?.code()} ${response?.message()}"
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка скачивания чека: ${e.message}"
            }
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                clients.value = repository?.getClients() ?: emptyList()
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка загрузки клиентов: ${e.message}"
            }
        }
    }

    fun filterClients() {
        val query = searchQuery.value.lowercase()
        val filtered = clients.value.filter { client ->
            client.firstName.lowercase().contains(query) ||
                    client.lastName.lowercase().contains(query) ||
                    (client.middleName?.lowercase()?.contains(query) ?: false)
        }
        clients.value = filtered
    }

    fun createClient(client: Client) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.createClient(client)
                loadClients()
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка создания клиента: ${e.message}"
            }
        }
    }

    fun updateClient(firstName: String, lastName: String, newClient: Client) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.updateClient(firstName, lastName, newClient)
                loadClients()
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка обновления клиента: ${e.message}"
            }
        }
    }

    fun deleteClient(clientId: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.deleteClient(clientId)
                loadClients()
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка удаления клиента: ${e.message}"
            }
        }
    }

    fun loadProducts(skip: Int, limit: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val loadedProducts = repository?.getProducts(skip, limit) ?: emptyList()
                originalProducts.value = loadedProducts
                products.value = loadedProducts
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка загрузки товаров: ${e.message}"
            }
        }
    }

    fun filterProducts() {
        val query = searchQuery.value.lowercase()
        if (query.isEmpty()) {
            products.value = originalProducts.value
        } else {
            val filtered = originalProducts.value.filter { product ->
                product.name.lowercase().contains(query)
            }
            products.value = filtered
        }
    }

    fun createProduct(product: Product, imageFile: File) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.createProduct(product, imageFile)
                loadProducts(0, 100)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка создания товара: ${e.message}"
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.updateProduct(product)
                loadProducts(0, 100)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка обновления товара: ${e.message}"
            }
        }
    }

    fun updateProductImage(name: String, imageFile: File) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.updateProductImage(name, imageFile)
                loadProducts(0, 100)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка обновления изображения: ${e.message}"
            }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository?.deleteProduct(productId)
                loadProducts(0, 100)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка удаления товара: ${e.message}"
            }
        }
    }

    fun increaseQuantity(productId: Int) {
        cart[productId] = (cart[productId] ?: 0) + 1
        cartState.value = cart.toMap()
    }

    fun decreaseQuantity(productId: Int) {
        val current = cart[productId] ?: 0
        if (current > 0) {
            cart[productId] = current - 1
            if (cart[productId] == 0) cart.remove(productId)
            cartState.value = cart.toMap()
        }
    }
}