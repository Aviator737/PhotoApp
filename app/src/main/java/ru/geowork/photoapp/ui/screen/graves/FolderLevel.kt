package ru.geowork.photoapp.ui.screen.graves

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import ru.geowork.photoapp.R

private fun getResourceIdByLevel(level: Int) = when(level) {
    0 -> R.string.folder_l0_name
    1 -> R.string.folder_l1_name
    2 -> R.string.folder_l2_name
    else -> R.string.folder_ln_name
}

@Composable
fun getFolderTypeNameByLevel(
    level: Int,
    capitalize: Boolean = false
): String {
    val resourceId = getResourceIdByLevel(level)
    return if (capitalize) {
        stringResource(resourceId).capitalize(Locale.current)
    } else {
        stringResource(resourceId)
    }
}

fun Context.getFolderTypeNameByLevel(
    level: Int,
    capitalize: Boolean = false
): String {
    val resourceId = getResourceIdByLevel(level)
    return if (capitalize) {
        getString(resourceId).capitalize(Locale.current)
    } else {
        getString(resourceId)
    }
}
