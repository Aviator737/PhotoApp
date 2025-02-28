package ru.geowork.photoapp.ui.screen.camera

import android.Manifest
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.WithPermissions
import ru.geowork.photoapp.ui.theme.BackgroundSecondaryDark

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun Camera(
    state: CameraUiState,
    onUiAction: (CameraUiAction) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraScope = rememberCoroutineScope()

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }

    LaunchedEffect(Unit) {
        cameraScope.launch {
            val cameraProvider = ProcessCameraProvider.awaitInstance(context)
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider { newSurfaceRequest -> surfaceRequest = newSurfaceRequest }
            }
            val imageCapture = ImageCapture.Builder().build()
            val camera = cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)

            //val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

            val exposureState = camera.cameraInfo.exposureState

            if (exposureState.isExposureCompensationSupported) {
                val range = exposureState.exposureCompensationRange

                //to set
                //if (range.contains(index))
                //     camera.getCameraControl().setExposureCompensationIndex(index);
            }

            try {
                awaitCancellation()
            } finally {
                cameraProvider.unbindAll()
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().background(BackgroundSecondaryDark)) {
        WithPermissions(
            requestedPermissions = arrayOf(Manifest.permission.CAMERA),
            rationaleText = stringResource(R.string.camera_permissions_not_granted_text),
            onDismiss = { onUiAction(CameraUiAction.NavigateBack) }
        ) {
            surfaceRequest?.let { surfaceRequest ->
                val coordinateTransformer = remember { MutableCoordinateTransformer() }

                CameraXViewfinder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.33f)
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
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        }
        LazyRow(modifier = Modifier
            .background(color = BackgroundSecondaryDark)
            .weight(1f)) {

        }
    }
}
