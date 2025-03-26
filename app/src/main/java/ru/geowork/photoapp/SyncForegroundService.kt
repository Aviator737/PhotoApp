package ru.geowork.photoapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.geowork.photoapp.SyncForegroundService.Companion.PATH_PARAM
import ru.geowork.photoapp.data.sync.SyncRepository
import ru.geowork.photoapp.di.DispatcherMain
import ru.geowork.photoapp.model.SyncState
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SyncForegroundService : Service() {

    @Inject
    @DispatcherMain
    lateinit var dispatcherMain: CoroutineDispatcher

    @Inject
    lateinit var syncRepository: SyncRepository

    private lateinit var syncScope: CoroutineScope
    private var syncProgressJob: Job? = null

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        syncScope = CoroutineScope(dispatcherMain + Job())
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val path = intent?.getStringExtra(PATH_PARAM) ?: throw Exception("Не передан параметр $PATH_PARAM")
        val notificationId = syncRepository.syncStateFlow.value.size + 1
        startForeground(notificationId, createNotification(path, mapOf()))
        syncScope.launch {
            syncRepository.sync(path)
            syncProgressJob?.cancel()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        syncProgressJob = syncRepository.syncStateFlow.onEach {
            notificationManager.notify(notificationId, createNotification(path, it))
        }.onCompletion {
            notificationManager.notify(notificationId, createNotification(path, syncRepository.syncStateFlow.value))
        }.launchIn(syncScope)

        return START_NOT_STICKY
    }

    private fun createNotification(
        path: String,
        map: Map<String, SyncState>
    ): Notification {
        val state = map[path]

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle(path)
            .setOngoing(true)

        when(state) {
            is SyncState.Archiving -> {
                builder.setContentText("${getString(R.string.state_archiving)}: ${String.format(Locale.US, "%.1f", state.value)}%")
                builder.setProgress(100, state.value.toInt(), false)
            }
            is SyncState.Uploading -> {
                builder.setSmallIcon(android.R.drawable.stat_sys_upload)
                builder.setContentText("${getString(R.string.state_uploading)}: ${String.format(Locale.US, "%.1f", state.value)}%")
                builder.setProgress(100, state.value.toInt(), false)
            }
            SyncState.Connecting -> {
                builder.setContentText(getString(R.string.state_connecting))
                builder.setProgress(100, 0, true)
            }
            is SyncState.Failed -> {
                builder.setSmallIcon(R.drawable.ic_error)
                builder.setContentText(getString(R.string.state_failed))
                builder.setOngoing(false)
                builder.setAutoCancel(true)
            }
            SyncState.Uploaded -> {
                builder.setSmallIcon(R.drawable.ic_cloud_done)
                builder.setContentText(getString(R.string.state_success))
                builder.setOngoing(false)
                builder.setAutoCancel(true)
            }
            SyncState.NotReady,
            SyncState.Ready,
            null -> {}
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        syncScope.cancel()
    }

    companion object {
        const val PATH_PARAM = "path"
        private const val CHANNEL_ID = BuildConfig.APPLICATION_ID

        fun startForegroundArchiveAndUpload(context: Context, path: String) {
            val serviceIntent = Intent(context, SyncForegroundService::class.java).apply {
                putExtra(PATH_PARAM, path)
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
