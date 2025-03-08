package ru.geowork.photoapp.ui.screen.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.ui.screen.camera.CameraUiState
import ru.geowork.photoapp.ui.screen.camera.CameraUiState.ExposureState.Step.Companion.findClosestElement
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun ExposureCompensationSettings(
    modifier: Modifier = Modifier,
    state: CameraUiState.ExposureState,
    onSelected: (CameraUiState.ExposureState.Step) -> Unit
) {
    if (state.steps.isEmpty()) return
    var sliderWidth by remember { mutableFloatStateOf(0f) }
    val stepWidth = sliderWidth / state.steps.size
    var valueOffset by remember { mutableFloatStateOf((state.selectedStep?.value ?: 0f) * stepWidth) }

    Row(
        modifier = modifier
            .onSizeChanged { sliderWidth = it.width.toFloat() }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    val newValue = (valueOffset + (dragAmount / stepWidth / 2))
                        .coerceIn(state.steps.first().value, state.steps.last().value)

                    valueOffset = newValue
                    println(valueOffset)
                    state.steps.findClosestElement(newValue)?.let { onSelected(it) }
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        state.steps.forEach { step ->
            val isSelected = step.index == (state.selectedStep?.index ?: 0)
            Box(
                modifier = Modifier.size(
                    width = if (isSelected) 3.dp else 1.5.dp,
                    height = if (isSelected) 20.dp else 10.dp,
                ).background(
                    color = if (isSelected) AppTheme.colors.orange else AppTheme.colors.contentConstant.copy(
                        alpha = if (step.value % 1.0 == 0.0) 0.9f else 0.4f
                    ),
                    shape = RoundedCornerShape(1.dp)
                )
            )
        }
    }
}
