package ru.geowork.photoapp.data

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpProgressMonitor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.BuildConfig
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.model.SyncState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadRepository @Inject constructor(
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    private val filesRepository: FilesRepository
) {

    suspend fun upload(
        archive: FolderItem.ZipFile,
        onProgress: (SyncState) -> Unit
    ) = withContext(dispatcherIo) {
        onProgress(SyncState.Connecting)
        if (archive.uri == null) return@withContext
        val session = openSession()
        val channel = openChannel(session)

        val progressMonitor = object : SftpProgressMonitor {
            private var totalBytesTransferred = 0f
            private val fileSize = archive.size.takeIf { it > 0 } ?: 1

            override fun init(op: Int, src: String?, dest: String?, max: Long) {
                onProgress(SyncState.Uploading(0f))
            }

            override fun count(count: Long): Boolean {
                totalBytesTransferred += count
                onProgress(SyncState.Uploading(totalBytesTransferred / fileSize * 100))
                return true
            }

            override fun end() {
                onProgress(SyncState.Uploading(100f))
            }
        }

        filesRepository.openInputStream(archive.uri)?.buffered()?.use { inputStream ->
            channel.put(inputStream, archive.name, progressMonitor, ChannelSftp.OVERWRITE)
        }
        session.disconnect()
        channel.disconnect()
    }

    private fun openSession(): Session = JSch().getSession(
        BuildConfig.SFTP_LOGIN,
        BuildConfig.SFTP_HOST,
        BuildConfig.SFTP_PORT
    ).apply {
        setPassword(BuildConfig.SFTP_PASSWORD)
        setConfig("StrictHostKeyChecking", "no")
        connect()
    }

    private fun openChannel(session: Session): ChannelSftp = (session.openChannel("sftp") as ChannelSftp).apply {
        connect()
        cd(BuildConfig.SFTP_REMOTE_PATH)
    }
}
