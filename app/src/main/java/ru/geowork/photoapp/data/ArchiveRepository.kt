package ru.geowork.photoapp.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.model.SyncState
import ru.geowork.photoapp.util.SEPARATOR
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveRepository @Inject constructor(
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    private val filesRepository: FilesRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend fun archiveFolder(
        path: String,
        onProgress: (SyncState) -> Unit
    ): FolderItem.ZipFile? = withContext(dispatcherIo) {
        onProgress(SyncState.Archiving(0f))
        val photographName = dataStoreRepository.getPhotographName().orEmpty()
        val supervisorName = dataStoreRepository.getSupervisorName().orEmpty()
        val archiveName = path.replace("_", "-").replace(SEPARATOR, "_") +
                "_${photographName.replace(" ", "-")}" +
                "_${supervisorName.replace(" ", "-")}"

        val archiveFolderItem = FolderItem.ZipFile(name = archiveName)
        filesRepository.createFolderItem(archiveFolderItem, path)?.let { archiveFileUri ->
            filesRepository.openOutputStream(archiveFileUri)?.buffered()?.let { bufferedOutputStream ->
                ZipOutputStream(bufferedOutputStream)
            }
        }?.use { zipOutputStream ->
            val items = filesRepository.getFolderItemsWithChild(path)
            if (items.isEmpty()) return@use
            val total = items.count()
            var i = 0f
            zipOutputStream.addAll(items, archiveName) {
                i++
                onProgress(SyncState.Archiving(i / total * 100))
            }
        }
        delay(500)
        return@withContext filesRepository.getFolderItems(path)
            .filterIsInstance<FolderItem.ZipFile>()
            .firstOrNull()
    }

    private suspend fun ZipOutputStream.addAll(items: List<FolderItem>, pathPrefix: String, callback: () -> Unit) {
        items.asSequence().forEach { item ->
            when(item) {
                is FolderItem.Folder -> addAll(
                    item.childItems.orEmpty(),
                    "$pathPrefix$SEPARATOR${item.name}",
                    callback
                )
                is FolderItem.ImageFile,
                is FolderItem.DocumentFile -> {
                    add(item, pathPrefix)
                    callback()
                }
                is FolderItem.ZipFile -> {}
            }
        }
    }

    private suspend fun ZipOutputStream.add(item: FolderItem, pathPrefix: String) {
        item.uri?.let { uri ->
            filesRepository.openInputStream(uri)
        }?.let { inputStream ->
            val path = if (pathPrefix.isEmpty()) item.name else "$pathPrefix$SEPARATOR${item.name}"
            val zipEntry = ZipEntry(path)
            putNextEntry(zipEntry)
            inputStream.copyTo(this)
            closeEntry()
        }
    }

    private fun List<FolderItem>.count(): Int {
        var i = 0
        forEach {
            if (it is FolderItem.Folder) {
                i += it.childItems?.count() ?: 1
            } else {
                i++
            }
        }
        return i
    }
}
