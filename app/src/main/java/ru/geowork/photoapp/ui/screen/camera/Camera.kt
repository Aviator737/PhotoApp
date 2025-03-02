package ru.geowork.photoapp.ui.screen.camera

import android.Manifest
import android.util.Rational
import android.view.Surface
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.WithPermissions
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.ui.theme.BackgroundSecondaryDark
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun Camera(
    state: CameraUiState,
    onUiAction: (CameraUiAction) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraScope = rememberCoroutineScope()

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }

    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    state.zoomLevels.firstOrNull { it.second }?.let { cameraControl?.setZoomRatio(it.first) }

    LaunchedEffect(Unit) {
        cameraScope.launch {
            val cameraProvider = ProcessCameraProvider.awaitInstance(context)

            val resolution = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                .build()

            val preview = Preview.Builder()
                .setResolutionSelector(resolution)
                .build()
                .apply {
                    setSurfaceProvider { newSurfaceRequest -> surfaceRequest = newSurfaceRequest }
                }

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setResolutionSelector(resolution)
                .build()

            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .setViewPort(ViewPort.Builder(Rational(3, 4), Surface.ROTATION_0).build())
                .build()

            val camera = cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup)

            cameraControl = camera.cameraControl

            onUiAction(
                CameraUiAction.OnZoomLevelsResolved(
                    minZoom = camera.cameraInfo.zoomState.value?.minZoomRatio ?: 1f,
                    maxZoom = camera.cameraInfo.zoomState.value?.maxZoomRatio ?: 1f
                )
            )

            try {
                awaitCancellation()
            } finally {
                cameraControl = null
                cameraProvider.unbindAll()
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(BackgroundSecondaryDark)) {
        WithPermissions(
            requestedPermissions = arrayOf(Manifest.permission.CAMERA),
            rationaleText = stringResource(R.string.camera_permissions_not_granted_text),
            onDismiss = { onUiAction(CameraUiAction.NavigateBack) }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        modifier = Modifier
                            .noRippleClickable { onUiAction(CameraUiAction.SwitchHDR) }
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(if (state.isHdrOn) R.drawable.ic_hdr_on else R.drawable.ic_hdr_off),
                        contentDescription = null,
                        tint = if (state.isHdrOn) AppTheme.colors.orange else AppTheme.colors.contentConstant
                    )
                    Icon(
                        modifier = Modifier
                            .noRippleClickable { onUiAction(CameraUiAction.SwitchGrid) }
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(if (state.showGrid) R.drawable.ic_grid_3x3 else R.drawable.ic_grid_off),
                        contentDescription = null,
                        tint = if (state.showGrid) AppTheme.colors.orange else AppTheme.colors.contentConstant
                    )
                    Icon(
                        modifier = Modifier
                            .noRippleClickable { onUiAction(CameraUiAction.SwitchExposureMenu) }
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(R.drawable.ic_exposure),
                        contentDescription = null,
                        tint = if (state.exposureCompensationIndex != null) AppTheme.colors.orange else AppTheme.colors.contentConstant
                    )
                }
                surfaceRequest?.let { surfaceRequest ->
                    val coordinateTransformer = remember { MutableCoordinateTransformer() }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)) {
                        CameraXViewfinder(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        with(coordinateTransformer) {
                                            val surfaceCoords = it.transform()
                                            surfaceRequest.resolution
                                            surfaceCoords.x
                                            surfaceCoords.y
                                        }
                                    }
                                },
                            surfaceRequest = surfaceRequest,
                            implementationMode = ImplementationMode.EXTERNAL,
                            coordinateTransformer = coordinateTransformer
                        )
                        if (state.showGrid) {
                            CameraGridOverlay(Modifier.fillMaxSize())
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.zoomLevels.forEach {
                                ZoomButton(
                                    value = it.first,
                                    isActive = it.second,
                                    onClick = { onUiAction(CameraUiAction.OnZoomSelected(it.first)) }
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CameraShotButton {  }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewCamera() {
    AppTheme {
        Camera(CameraUiState()) {}
    }
}
