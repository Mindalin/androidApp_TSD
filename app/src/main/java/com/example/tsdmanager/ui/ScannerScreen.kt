package com.example.tsdmanager.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.tsdmanager.AppViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.text.matches

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(viewModel: AppViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Настройка сканера для Code 128 и QR-кодов
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_CODE_128, Barcode.FORMAT_QR_CODE)
        .build()
    val scanner = BarcodeScanning.getClient(options)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(executor) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        Log.d("ScannerScreen", "Processing image for barcode")
                                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                        scanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                if (barcodes.isEmpty()) {
                                                    Log.d("ScannerScreen", "No barcodes detected")
                                                }
                                                barcodes.forEach { barcode ->
                                                    val value = barcode.rawValue
                                                    Log.d("ScannerScreen", "Barcode detected: $value")
                                                    if (value != null && value.matches(Regex(".{9}"))) {
                                                        // Проверка существования заказа
                                                        viewModel.viewModelScope.launch {
                                                            viewModel.loadOrderDetails(value)
                                                            if (viewModel.currentOrder.value != null) {
                                                                Log.d("ScannerScreen", "Valid identifier: $value, order found, navigating")
                                                                coroutineScope.launch {
                                                                    snackbarHostState.showSnackbar(
                                                                        message = "Успешно отсканировано: $value",
                                                                        duration = SnackbarDuration.Long
                                                                    )
                                                                }
                                                                navController.navigate("order_detail/$value") {
                                                                    popUpTo(navController.graph.startDestinationId)
                                                                    launchSingleTop = true
                                                                }
                                                            } else {
                                                                Log.d("ScannerScreen", "Order not found for identifier: $value")
                                                                errorMessage = "Заказ с идентификатором $value не найден"
                                                                showErrorDialog = true
                                                            }
                                                        }
                                                    } else {
                                                        Log.d("ScannerScreen", "Invalid identifier length: $value")
                                                        errorMessage = "Идентификатор должен быть длиной 9 символов: $value"
                                                        showErrorDialog = true
                                                    }
                                                }
                                            }
                                            .addOnFailureListener {
                                                Log.e("ScannerScreen", "Barcode scanning failed: ${it.message}")
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = "Ошибка сканирования: ${it.message}",
                                                        duration = SnackbarDuration.Long
                                                    )
                                                }
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        Log.d("ScannerScreen", "No media image available")
                                        imageProxy.close()
                                    }
                                }
                            }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                            Log.d("ScannerScreen", "Camera bound successfully")
                        } catch (e: Exception) {
                            Log.e("ScannerScreen", "Camera binding failed: ${e.message}")
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Ошибка камеры: ${e.message}",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(200.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
            ) {
            }

            if (viewModel.isLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Диалоговое окно для ошибок
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Ошибка сканирования") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(
                            onClick = { showErrorDialog = false }
                        ) {
                            Text("ОК")
                        }
                    }
                )
            }
        }
    }

    // Проверка разрешения камеры
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            snackbarHostState.showSnackbar(
                message = "Требуется разрешение камеры",
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            Log.d("ScannerScreen", "Executor shutdown")
        }
    }
}