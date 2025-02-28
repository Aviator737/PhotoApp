package ru.geowork.photoapp.ui.screen.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.view.Surface
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.WithPermissions

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun Camera(
    state: CameraUiState,
    onUiAction: (CameraUiAction) -> Unit
) {
    val context = LocalContext.current
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    val cameraIds = remember { getAvailableCameras(cameraManager) }
    var currentCameraId by remember { mutableStateOf(cameraIds.firstOrNull() ?: "0") }
    var cameraDevice: CameraDevice? by remember { mutableStateOf(null) }
    var captureSession: CameraCaptureSession? by remember { mutableStateOf(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        WithPermissions(
            requestedPermissions = arrayOf(Manifest.permission.CAMERA),
            rationaleText = stringResource(R.string.camera_permissions_not_granted_text),
            onDismiss = { onUiAction(CameraUiAction.NavigateBack) }
        ) {
            AndroidView(
                factory = { ctx ->
                    TextureView(ctx).apply {
                        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                                openCamera(
                                    cameraManager,
                                    this@apply,
                                    currentCameraId
                                ) { device, session ->
                                    cameraDevice?.close() // Закрываем старую камеру
                                    captureSession?.close() // Закрываем сессию
                                    cameraDevice = device
                                    captureSession = session
                                }
                            }

                            override fun onSurfaceTextureSizeChanged(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                            }

                            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = false
                            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                cameraDevice?.close() // Закрываем текущую камеру перед переключением
                captureSession?.close() // Закрываем сессию
                val nextIndex = (cameraIds.indexOf(currentCameraId) + 1) % cameraIds.size
                currentCameraId = cameraIds[nextIndex]
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(text = "Переключить камеру")
        }
    }

//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    val cameraScope = rememberCoroutineScope()
//
//    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
//
//    var cameraControl: CameraControl? = null
//
//    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//    for (cameraId in cameraManager.cameraIdList) {
//        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
//        val focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
//
//        Log.d("CameraInfo", "Камера ID: $cameraId, Фокусные расстояния: ${focalLengths?.joinToString()}")
//    }
//
//    LaunchedEffect(Unit) {
//        cameraScope.launch {
//            val cameraProvider = ProcessCameraProvider.awaitInstance(context)
//            val preview = Preview.Builder().build().apply {
//                setSurfaceProvider { newSurfaceRequest -> surfaceRequest = newSurfaceRequest }
//            }
//            val imageCapture = ImageCapture.Builder().build()
//            cameraProvider.unbindAll()
//            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture).also {
//                cameraControl = it.cameraControl
//
//                val minZoomLevel = it.cameraInfo.zoomState.value?.minZoomRatio
//                val maxZoomLevel = it.cameraInfo.zoomState.value?.maxZoomRatio
//
//                println("minZoomLevel $minZoomLevel")
//                println("maxZoomLevel $maxZoomLevel")
//
//                val cameraId = Camera2CameraInfo.from(it.cameraInfo).cameraId
//                println("cameraId: $cameraId")
//            }
//
//            try {
//                awaitCancellation()
//            } finally {
//                cameraControl = null
//                cameraProvider.unbindAll()
//            }
//        }
//    }
//
//    Column(modifier = Modifier.fillMaxWidth().background(BackgroundSecondaryDark)) {
//        WithPermissions(
//            requestedPermissions = arrayOf(Manifest.permission.CAMERA),
//            rationaleText = stringResource(R.string.camera_permissions_not_granted_text),
//            onDismiss = { onUiAction(CameraUiAction.NavigateBack) }
//        ) {
//            surfaceRequest?.let { surfaceRequest ->
//                val coordinateTransformer = remember { MutableCoordinateTransformer() }
//
//                CameraXViewfinder(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(0.75f)
//                        .pointerInput(Unit) {
//                            detectTapGestures {
//                                with(coordinateTransformer) {
//                                    val surfaceCoords = it.transform()
//                                    surfaceRequest.resolution
//                                    surfaceCoords.x
//                                    surfaceCoords.y
//                                }
//                            }
//                        },
//                    surfaceRequest = surfaceRequest,
//                    implementationMode = ImplementationMode.EXTERNAL,
//                    coordinateTransformer = coordinateTransformer
//                )
//            }
//        }
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            val inputScope = rememberCoroutineScope()
//            var inputJob: Job? = null
//            var zoomRatioValue by remember { mutableStateOf("") }
//            Input(
//                hint = "zoom ratio",
//                text = zoomRatioValue,
//                modifier = Modifier.width(150.dp)
//            ) {
//                zoomRatioValue = it
//                inputJob?.cancel()
//                inputJob = inputScope.launch {
//                    delay(3000)
//                    val value = it.toFloatOrNull() ?: run {
//                        zoomRatioValue = "1"
//                        return@run 1f
//                    }
//                    cameraControl?.setZoomRatio(value)
//                }
//            }
//            ButtonLarge(
//                onClick = { cameraControl?.setLinearZoom(0.8f) }
//            ) {
//                Text(
//                    text = "0.6x",
//                    style = AppTheme.typography.semibold16
//                )
//            }
//            ButtonLarge(
//                onClick = { cameraControl?.setZoomRatio(1f) }
//            ) {
//                Text(
//                    text = "1x",
//                    style = AppTheme.typography.semibold16
//                )
//            }
//            ButtonLarge(
//                onClick = { cameraControl?.setZoomRatio(2f) }
//            ) {
//                Text(
//                    text = "2x",
//                    style = AppTheme.typography.semibold16
//                )
//            }
//        }
//        LazyRow(modifier = Modifier
//            .background(color = BackgroundSecondaryDark)
//            .weight(1f)) {
//
//        }
//    }
}

fun getAvailableCameras(cameraManager: CameraManager): List<String> {
    return cameraManager.cameraIdList.filter { cameraId ->
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
        capabilities?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) == true
    }
}

@SuppressLint("MissingPermission")
fun openCamera(
    cameraManager: CameraManager,
    textureView: TextureView,
    cameraId: String,
    onCameraOpened: (CameraDevice, CameraCaptureSession) -> Unit
) {
    cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            val surfaceTexture = textureView.surfaceTexture ?: return
            val surface = Surface(surfaceTexture)

            val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                addTarget(surface)
            }

            camera.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    onCameraOpened(camera, session)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {}
            }, null)
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }, null)
}
