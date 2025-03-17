package ru.geowork.photoapp.ui.screen.camera.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Rational
import android.util.Size
import android.view.HapticFeedbackConstants
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.WithPermissions
import ru.geowork.photoapp.ui.screen.camera.CameraUiAction
import ru.geowork.photoapp.ui.screen.camera.CameraUiState
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.ui.theme.BackgroundSecondaryDark
import ru.geowork.photoapp.util.HideSystemBars
import ru.geowork.photoapp.util.noRippleClickable
import java.io.OutputStream
import java.util.Locale

@Composable
fun Camera(
    state: CameraUiState,
    onUiAction: (CameraUiAction) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraScope = rememberCoroutineScope()

    var surfaceMeteringPointFactory by remember { mutableStateOf<SurfaceOrientedMeteringPointFactory?>(null) }
    var focusCoordinates by remember { mutableStateOf<Offset?>(null) }
    var showFocusPoint by remember { mutableStateOf(false) }

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var shutterDim by remember { mutableStateOf(false) }

    state.zoomLevels.firstOrNull { it.second }?.let { cameraControl?.setZoomRatio(it.first) }
    state.exposureState.selectedStep?.let { cameraControl?.setExposureCompensationIndex(it.index) }

    val itemsScrollState = rememberLazyListState()

    BoxWithConstraints {
        if (constraints.maxHeight < 2000) HideSystemBars()
    }

    DisposableEffect(Unit) {
        val orientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return
                imageCapture?.targetRotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
            }
        }
        orientationEventListener.enable()
        onDispose {
            orientationEventListener.disable()
        }
    }

    LifecycleResumeEffect(Unit) {
        onUiAction(CameraUiAction.OnUpdateFolderItems)
        onPauseOrDispose {}
    }

    LaunchedEffect(state.takePhoto) {
        if (state.takePhoto != null) {
            shutterDim = true
            context.takePhoto(
                uri = state.takePhoto.uri,
                outputStream = state.takePhoto.outputStream,
                imageCapture = imageCapture,
                onError = { println(it) },
                onImageSaved = { onUiAction(CameraUiAction.OnPhotoTaken(it)) }
            )
            delay(400)
            shutterDim = false
        }
    }

    LaunchedEffect(state.items.size) {
        if (state.items.isNotEmpty()) {
            itemsScrollState.animateScrollToItem(state.items.lastIndex)
        }
    }

    LaunchedEffect(Unit) {
        cameraScope.launch {
            val cameraProvider = ProcessCameraProvider.awaitInstance(context)

            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                .setResolutionStrategy(
                    ResolutionStrategy(
                        Size(4000, 3000),
                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                    )
                )
                .build()

            val previewUseCase = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .apply {
                    setSurfaceProvider { request ->
                        surfaceRequest = request
                        surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            request.resolution.width.toFloat(),
                            request.resolution.height.toFloat()
                        )
                    }
                }

            val imageCaptureUseCase = ImageCapture.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()

            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(previewUseCase)
                .addUseCase(imageCaptureUseCase)
                .setViewPort(ViewPort.Builder(Rational(3, 4), Surface.ROTATION_0).build())
                .build()

            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                useCaseGroup
            )

            imageCapture = imageCaptureUseCase
            cameraControl = camera.cameraControl

            onUiAction(
                CameraUiAction.OnZoomLevelsResolved(
                    minZoom = camera.cameraInfo.zoomState.value?.minZoomRatio ?: 1f,
                    maxZoom = camera.cameraInfo.zoomState.value?.maxZoomRatio ?: 1f
                )
            )

            with(camera.cameraInfo.exposureState) {
                onUiAction(
                    CameraUiAction.OnExposureResolved(
                        isSupported = isExposureCompensationSupported,
                        default = exposureCompensationIndex,
                        step = exposureCompensationStep.toFloat(),
                        min = exposureCompensationRange.lower,
                        max = exposureCompensationRange.upper,
                    )
                )
            }

            try {
                awaitCancellation()
            } finally {
                cameraProvider.unbindAll()
                cameraControl = null
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundSecondaryDark)
    ) {
        if (state.isInitialized) {
            WithPermissions(
                requestedPermissions = arrayOf(Manifest.permission.CAMERA),
                rationaleText = stringResource(R.string.camera_permissions_not_granted_text),
                onDismiss = { onUiAction(CameraUiAction.NavigateBack) }
            ) {
                surfaceRequest?.let { surfaceRequest ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)
                    ) {
                        CameraXViewfinder(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures { tapCoordinates ->
                                        surfaceMeteringPointFactory
                                            ?.createPoint(tapCoordinates.x, tapCoordinates.y)
                                            ?.let {
                                                cameraControl?.startFocusAndMetering(
                                                    FocusMeteringAction
                                                        .Builder(it)
                                                        .build()
                                                )
                                                focusCoordinates = tapCoordinates
                                                showFocusPoint = true
                                            }
                                    }
                                    detectTransformGestures { _, _, zoomChange, _ ->
                                        println(zoomChange)
                                    }
                                },
                            surfaceRequest = surfaceRequest,
                            implementationMode = ImplementationMode.EXTERNAL
                        )
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shutterDim,
                            enter = fadeIn(tween(200)),
                            exit = fadeOut(tween(200))
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)))
                        }
                        if (state.showGrid) {
                            CameraGridOverlay(Modifier.fillMaxSize())
                        }
                        focusCoordinates?.let { CameraFocusPoint(it, showFocusPoint) }
                        LaunchedEffect(showFocusPoint) {
                            delay(1000)
                            showFocusPoint = false
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
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType(HapticFeedbackConstants.VIRTUAL_KEY))
                                        onUiAction(CameraUiAction.OnZoomSelected(it.first))
                                    }
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        modifier = Modifier
                            .noRippleClickable { onUiAction(CameraUiAction.SwitchGrid) }
                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(if (state.showGrid) R.drawable.ic_grid_3x3 else R.drawable.ic_grid_off),
                        contentDescription = null,
                        tint = if (state.showGrid) AppTheme.colors.orange else AppTheme.colors.contentConstant
                    )
                    Box(
                        modifier = Modifier
                            .noRippleClickable { onUiAction(CameraUiAction.SwitchExposureMenu) }
                            .padding(16.dp)
                            .size(height = 24.dp, width = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.exposureState.isVisible || state.exposureState.selectedStep?.index != 0) {
                            val value = state.exposureState.selectedStep?.value ?: 0f
                            val prefix = when {
                                value > 0f -> "+"
                                value == 0f -> " "
                                else -> ""
                            }
                            Text(
                                text = prefix + String.format(Locale.US, "%.1f", value),
                                color = AppTheme.colors.orange
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_exposure),
                                contentDescription = null,
                                tint = AppTheme.colors.contentConstant
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier.height(40.dp).fillMaxWidth().padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    this@Column.AnimatedVisibility(
                        visible = state.exposureState.isVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        ExposureCompensationSettings(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.exposureState,
                            onSelected = { onUiAction(CameraUiAction.OnExposureStepSelected(it)) }
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CameraShotButton { onUiAction(CameraUiAction.OnTakePhotoClick) }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            state = itemsScrollState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(state.items) { index, item ->
                AsyncImage(
                    model = item.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onUiAction(CameraUiAction.OnPhotoClick(index)) }
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewCamera() {
    AppTheme {
        Camera(CameraUiState(), onUiAction = {})
    }
}

private fun Context.takePhoto(
    uri: Uri,
    outputStream: OutputStream,
    imageCapture: ImageCapture?,
    onError: (ImageCaptureException) -> Unit,
    onImageSaved: (Uri) -> Unit
) {
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()
    imageCapture?.takePicture(outputFileOptions, mainExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(error: ImageCaptureException) {
                outputStream.close()
                onError(error)
            }
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputStream.close()
                onImageSaved(uri)
            }
        }
    )
}
