package ru.geowork.photoapp.ui.screen.graves.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.SyncState
import ru.geowork.photoapp.ui.components.ButtonLarge
import ru.geowork.photoapp.ui.theme.AppTheme
import java.util.Locale

@Composable
fun SyncStateBar(
    modifier: Modifier = Modifier,
    state: SyncState,
    onClick: () -> Unit
) {
    if (state is SyncState.NotReady) return

    Column(modifier = modifier
        .padding(horizontal = 24.dp, vertical = 8.dp)
        .background(
            color = AppTheme.colors.backgroundModal,
            shape = RoundedCornerShape(12.dp),
        )
        .border(
            border = BorderStroke(2.dp, AppTheme.colors.contentBorder),
            shape = RoundedCornerShape(12.dp),
        )
        .padding(24.dp)
    ) {
        when(state) {
            SyncState.NotReady -> {}
            SyncState.Ready -> {
                Text(
                    text = stringResource(R.string.state_finished_title),
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.medium20
                )
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(R.string.state_finished_hint),
                    color = AppTheme.colors.contentSecondary,
                    style = AppTheme.typography.regular12
                )
                ButtonLarge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.accentPrimary,
                        contentColor = AppTheme.colors.contentConstant
                    ),
                    onClick = onClick
                ) {
                    Text(
                        text = stringResource(id = R.string.upload),
                        style = AppTheme.typography.semibold16
                    )
                }
            }
            is SyncState.Archiving -> {
                Text(
                    text = "${stringResource(R.string.state_archiving)}: ${String.format(Locale.US, "%.1f", state.value)}%",
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.semibold16
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    progress = state.value / 100f,
                    color = AppTheme.colors.accentPrimary,
                    strokeCap = StrokeCap.Round
                )
//                ButtonLarge(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 12.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = AppTheme.colors.systemErrorPrimary,
//                        contentColor = AppTheme.colors.contentConstant
//                    ),
//                    onClick = onClick
//                ) {
//                    Text(
//                        text = stringResource(id = R.string.stop),
//                        style = AppTheme.typography.semibold16
//                    )
//                }
            }
            is SyncState.Uploading -> {
                Text(
                    text = "${stringResource(R.string.state_uploading)}: ${String.format(Locale.US, "%.1f", state.value)}%",
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.semibold16
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    progress = state.value / 100f,
                    color = AppTheme.colors.accentPrimary,
                    strokeCap = StrokeCap.Round
                )
            }
            SyncState.Uploaded -> {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.state_success),
                    color = AppTheme.colors.systemSuccessPrimary,
                    style = AppTheme.typography.medium20,
                    textAlign = TextAlign.Center
                )
            }
            SyncState.Failed -> {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.state_failed),
                    color = AppTheme.colors.systemErrorPrimary,
                    style = AppTheme.typography.medium20,
                    textAlign = TextAlign.Center
                )
                ButtonLarge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.accentPrimary,
                        contentColor = AppTheme.colors.contentConstant
                    ),
                    onClick = onClick
                ) {
                    Text(
                        text = stringResource(id = R.string.retry),
                        style = AppTheme.typography.semibold16
                    )
                }
            }
            SyncState.Connecting -> {
                Text(
                    text = stringResource(R.string.state_connecting),
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.semibold16
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = AppTheme.colors.accentPrimary,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
