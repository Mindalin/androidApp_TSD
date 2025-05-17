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
    val orders = mutableStateOf<List<Order>>(emptyList())
    val selectedClient = mutableStateOf<Client?>(null)
    val selectedProduct = mutableStateOf<Product?>(null)
    val cart = mutableMapOf<Int, Int>()
    val cartState = mutableStateOf<Map<Int, Int>>(emptyMap())
    val currentOrder = mutableStateOf<Order?>(null)

    // SharedPreferences для хранения JWT-токена и срока действия
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val JWT_TOKEN_KEY = "jwt_token"
    private val JWT_EXPIRY_KEY = "jwt_expiry"

    // Секретный ключ подписи, совпадающий с сервером
    private val SECRET_KEY = BuildConfig.SECRET_KEY

    // Инициализация репозитория
    fun initRepository(token: String) {
        repository = ClientRepository(token)
    }

    // Функция для авторизации
    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = ClientRepository("").login(username, password)
            val token = response.accessToken
            saveJwtToken(token)
            initRepository(token)
            isLoggedIn.value = true
            true
        } catch (e: Exception) {
            errorMessage.value = "Ошибка входа: ${e.message}"
            false
        }
    }

    // Функция для сохранения токена и срока действия
    private fun saveJwtToken(token: String) {
        try {
            // Декодируем токен и извлекаем срок действия
            val key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray(StandardCharsets.UTF_8))
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            val expiry = claims.expiration.time / 1000 // Время истечения в секундах

            sharedPreferences.edit().apply {
                putString(JWT_TOKEN_KEY, token)
                putLong(JWT_EXPIRY_KEY, expiry)
                apply()
            }
        } catch (e: Exception) {
            errorMessage.value = "Ошибка обработки токена: ${e.message}"
        }
    }

    // Функция для получения токена
    fun getJwtToken(): String? {
        return sharedPreferences.getString(JWT_TOKEN_KEY, null)
    }

    // Функция для получения срока действия токена
    private fun getJwtExpiry(): Long {
        return sharedPreferences.getLong(JWT_EXPIRY_KEY, 0)
    }

    // Функция для очистки токена
    fun clearJwtToken() {
        sharedPreferences.edit().apply {
            remove(JWT_TOKEN_KEY)
            remove(JWT_EXPIRY_KEY)
            apply()
        }
        repository = null
        isLoggedIn.value = false
    }

    // Локальная проверка валидности токена
    fun isJwtTokenValidLocally(): Boolean {
        val token = getJwtToken() ?: return false
        val expiry = getJwtExpiry()
        val currentTime = System.currentTimeMillis() / 1000
        return token.isNotEmpty() && expiry > currentTime
    }

    // Проверка валидности токена на сервере
    suspend fun isJwtTokenValidOnServer(): Boolean {
        return try {
            repository?.validateToken() == true
        } catch (e: Exception) {
            errorMessage.value = "Ошибка проверки токена: ${e.message}"
            false
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            try {
                orders.value = repository?.getOrders(0, 1000) ?: emptyList()
            } catch (e: Exception) {
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

    fun createOrder(clientId: Int) {
        viewModelScope.launch {
            try {
                val items = cart.map { (productId, quantity) ->
                    OrderItemCreate(productId = productId, quantity = quantity)
                }
                val order = OrderCreate(clientId = clientId, status = "pending", items = items)
                repository?.createOrder(order)
                cart.clear()
                cartState.value = emptyMap()
                loadOrders()
            } catch (e: Exception) {
                errorMessage.value = "Ошибка создания заказа: ${e.message}"
            }
        }
    }

    fun loadOrderDetails(identifier: String) {
        viewModelScope.launch {
            try {
                currentOrder.value = repository?.getOrderByIdentifier(identifier)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка загрузки деталей заказа: ${e.message}"
            }
        }
    }

    fun addItemToOrder(identifier: String, productName: String, quantity: Int) {
        viewModelScope.launch {
            try {
                repository?.addItemToOrder(identifier, productName, quantity)
                loadOrderDetails(identifier)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка добавления товара: ${e.message}"
            }
        }
    }

    fun updateItemQuantity(identifier: String, productName: String, quantity: Int) {
        viewModelScope.launch {
            try {
                repository?.updateItemQuantity(identifier, productName, quantity)
                loadOrderDetails(identifier)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка обновления количества: ${e.message}"
            }
        }
    }

    fun updateOrderStatus(identifier: String, status: String) {
        viewModelScope.launch {
            try {
                repository?.updateOrderStatus(identifier, status)
                loadOrderDetails(identifier)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка обновления статуса: ${e.message}"
            }
        }
    }

    fun deleteOrder(identifier: String) {
        viewModelScope.launch {
            try {
                repository?.deleteOrder(identifier)
                loadOrders()
            } catch (e: Exception) {
                errorMessage.value = "Ошибка удаления заказа: ${e.message}"
            }
        }
    }

    fun downloadReceipt(identifier: String, onSuccess: (File) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository?.downloadReceipt(identifier)
                if (response?.isSuccessful == true) {
                    // Создаём файл в директории files
                    val file = File(context.filesDir, "receipt_$identifier.pdf")
                    file.writeBytes(response.body()?.bytes() ?: byteArrayOf())
                    onSuccess(file)
                } else {
                    errorMessage.value = "Ошибка скачивания чека: ${response?.code()} ${response?.message()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Ошибка скачивания чека: ${e.message}"
            }
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            try {
                clients.value = repository?.getClients() ?: emptyList()
            } catch (e: Exception) {
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
                repository?.createClient(client)
                loadClients()
            } catch (e: Exception) {
                errorMessage.value = "Ошибка создания клиента: ${e.message}"
            }
        }
    }

    fun updateClient(firstName: String, lastName: String, newClient: Client) {
        viewModelScope.launch {
            try {
                repository?.updateClient(firstName, lastName, newClient)
                loadClients()
            } catch (e: Exception) {
                errorMessage.value = "Ошибка обновления клиента: ${e.message}"
            }
        }
    }

    fun deleteClient(firstName: String, lastName: String) {
        viewModelScope.launch {
            try {
                repository?.deleteClient(firstName, lastName)
                loadClients()
            } catch (e: Exception) {
                errorMessage.value = "Ошибка удаления клиента: ${e.message}"
            }
        }
    }

    fun loadProducts(skip: Int, limit: Int) {
        viewModelScope.launch {
            try {
                products.value = repository?.getProducts(skip, limit) ?: emptyList()
            } catch (e: Exception) {
                errorMessage.value = "Ошибка загрузки товаров: ${e.message}"
            }
        }
    }

    fun filterProducts() {
        val query = searchQuery.value.lowercase()
        val filtered = products.value.filter { product ->
            product.name.lowercase().contains(query)
        }
        products.value = filtered
    }

    fun createProduct(product: Product, imageFile: File) {
        viewModelScope.launch {
            try {
                repository?.createProduct(product, imageFile)
                loadProducts(0, 100)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка создания товара: ${e.message}"
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository?.updateProduct(product)
                loadProducts(0, 100)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка обновления товара: ${e.message}"
            }
        }
    }

    fun updateProductImage(name: String, imageFile: File) {
        viewModelScope.launch {
            try {
                repository?.updateProductImage(name, imageFile)
                loadProducts(0, 100)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка обновления изображения: ${e.message}"
            }
        }
    }

    fun deleteProduct(name: String) {
        viewModelScope.launch {
            try {
                repository?.deleteProduct(name)
                loadProducts(0, 100)
            } catch (e: Exception) {
                errorMessage.value = "Ошибка удаления товара: ${e.message}"
            }
        }
    }
}