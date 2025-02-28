package ru.geowork.photoapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun EditSelectableEditableInput(
    hint: String = "",
    text: String = "",
    icon: Painter? = null,
    isSelected: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onInput: (String) -> Unit = {}
) {
    val colors = AppTheme.colors
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text, selection = TextRange(text.length))) }

    val textValue = textFieldValueState.copy(text = text)

    var isEditing by remember { mutableStateOf(false) }

    val hintFontSize by animateIntAsState(
        targetValue = if (isEditing || text.isNotEmpty()) 12 else 16,
        label = "hint fontSize"
    )

    val hintLineHeight by animateIntAsState(
        targetValue = if (isEditing || text.isNotEmpty()) 16 else 24,
        label = "hint lineHeight"
    )

    BasicTextField(
        textStyle = AppTheme.typography.regular16.copy(
            color = AppTheme.colors.contentPrimary
        ),
        cursorBrush = SolidColor(colors.contentPrimary),
        value = textValue,
        onValueChange = {
            textFieldValueState = it
            onInput(it.text)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = singleLine,
        enabled = enabled && isSelected || text.isEmpty() || text.isBlank(),
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isEditing = it.hasFocus },
        decorationBox = { innerTextField ->
            Row(modifier = Modifier
                .border(
                    border = BorderStroke(2.dp, if (isSelected) colors.accentPrimary else colors.contentBorder),
                    shape = RoundedCornerShape(12.dp),
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .then(if (!isEditing) Modifier.noRippleClickable {
                    onClick()
                    if (isSelected || text.isEmpty() || text.isBlank()) {
                        focusRequester.requestFocus()
                    }
                } else Modifier),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).padding(end = 8.dp)
                    )
                }
                Column(
                    modifier = Modifier.height(40.dp).weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = hint,
                        color = colors.contentSecondary,
                        style = AppTheme.typography.medium16,
                        fontSize = hintFontSize.sp,
                        lineHeight = hintLineHeight.sp,
                        minLines = if (singleLine) 1 else Int.MAX_VALUE
                    )
                    if (isEditing || text.isNotEmpty()) {
                        innerTextField()
                    }
                }
                AnimatedVisibility(
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally(),
                    visible = !isEditing && text.isNotEmpty()
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_pencil),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp).size(24.dp),
                        colorFilter = ColorFilter.tint(AppTheme.colors.contentSecondary)
                    )
                }
            }
        }
    )
}
