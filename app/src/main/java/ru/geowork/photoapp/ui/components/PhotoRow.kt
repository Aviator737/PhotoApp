package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun PhotoRow(
    modifier: Modifier = Modifier,
    name: String,
    onTakePhotoClick: () -> Unit
) {
    Column(
        modifier = modifier
            .border(
                border = BorderStroke(2.dp, AppTheme.colors.contentBorder),
                shape = RoundedCornerShape(12.dp),
            )
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = name,
                style = AppTheme.typography.medium16,
                color = AppTheme.colors.contentPrimary
            )
        }
        LazyRow(
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.size(64.dp)
                        .background(AppTheme.colors.contentBorder, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTakePhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_photo_add),
                        contentDescription = null,
                        tint = AppTheme.colors.contentPrimary
                    )
                }
            }
        }
    }
}
