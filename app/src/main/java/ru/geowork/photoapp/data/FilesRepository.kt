package ru.geowork.photoapp.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.ImageProxy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.di.ApplicationIoScope
import ru.geowork.photoapp.di.DispatcherDefault
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.util.SEPARATOR
import ru.geowork.photoapp.util.compressToSize
import ru.geowork.photoapp.util.createFileLikeExt
import ru.geowork.photoapp.util.createFileLikeUri
import ru.geowork.photoapp.util.deleteFile
import ru.geowork.photoapp.util.deleteFolderRecursively
import ru.geowork.photoapp.util.getExtensionFromMimeType
import ru.geowork.photoapp.util.getFiles
import ru.geowork.photoapp.util.openInputStream
import ru.geowork.photoapp.util.openOutputStream
import ru.geowork.photoapp.util.readMap
import ru.geowork.photoapp.util.rotate
import ru.geowork.photoapp.util.writeMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationIoScope private val applicationIoScope: CoroutineScope,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    @DispatcherDefault private val dispatcherDefault: CoroutineDispatcher,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend fun createFolderItem(folderItem: FolderItem, relativePath: String): Uri? {
        val fileExt = getFileExt(folderItem)
        return context.createFileLikeExt(
            fileName = folderItem.name,
            path = getPath(relativePath),
            ext = fileExt
        )
    }

    suspend fun getFolderItems(path: String) = withContext(dispatcherIo) {
        val mediaStoreFiles = context.getFiles(getPath(path))
        mediaStoreFiles.map { mediaStoreFile ->
            val id = mediaStoreFile.id.toString()
            val fileExt = getExtensionFromMimeType(mediaStoreFile.mimeType) ?: ""
            when {
                fileExt.isEmpty() -> FolderItem.Folder(
                    id = id,
                    name = mediaStoreFile.displayName,
                    uri = mediaStoreFile.uri,
                    relativePath = "$path$SEPARATOR${mediaStoreFile.displayName}",
                    level = path.split(SEPARATOR).size
                )
                FolderItem.ImageFile.ImageType.entries.map { it.extension }.contains(fileExt) -> FolderItem.ImageFile(
                    id = id,
                    name = mediaStoreFile.displayName,
                    uri = mediaStoreFile.uri,
                    parentFolder = path,
                    size = mediaStoreFile.size,
                    type = FolderItem.ImageFile.ImageType.getTypeFromExtension(fileExt)
                )
                fileExt == FolderItem.ZipFile.EXTENSION -> FolderItem.ZipFile(
                    id = id,
                    name = mediaStoreFile.displayName,
                    uri = mediaStoreFile.uri,
                    relativePath = "$path$SEPARATOR${mediaStoreFile.displayName}",
                    size = mediaStoreFile.size
                )
                else -> {
                    val type = FolderItem.DocumentFile.DocumentType.getTypeFromExtension(fileExt)
                    FolderItem.DocumentFile(
                        id = id,
                        name = mediaStoreFile.displayName,
                        uri = mediaStoreFile.uri,
                        type = type
                    )
                }
            }
        }
    }

    suspend fun getFolderItemsWithChild(path: String) = withContext(dispatcherIo) {
        getFolderItems(path).map { item ->
            if (item is FolderItem.Folder) {
                item.copy(childItems = getFolderItems("$path$SEPARATOR${item.name}"))
            } else item
        }
    }

    suspend fun copyFromUri(sourceUri: Uri, fileName: String, path: String) = withContext(dispatcherIo) {
        context.createFileLikeUri(sourceUri, fileName, getPath(path))?.let { newUri ->
            context.openInputStream(sourceUri)?.use { inputStream ->
                openOutputStream(newUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    suspend fun openOutputStream(uri: Uri, mode: String = "wt") = withContext(dispatcherIo) {
        context.openOutputStream(uri, mode)
    }

    suspend fun openInputStream(uri: Uri) = withContext(dispatcherIo) {
        context.openInputStream(uri)
    }

    suspend fun deleteFile(uri: Uri) = withContext(dispatcherIo) {
        context.deleteFile(uri)
    }

    suspend fun deleteFolder(folderItem: FolderItem.Folder) = withContext(dispatcherIo) {
        if (folderItem.uri == null) return@withContext
        context.deleteFolderRecursively(folderItem.uri)
    }

    fun processAndSaveImage(
        name: String,
        savePath: String,
        imageProxy: ImageProxy,
        onImageSaved: (Uri) -> Unit,
        onImageCompressed: (Uri) -> Unit
    ) = applicationIoScope.launch {
            val buffer = imageProxy.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            imageProxy.close()

            val orientation = imageProxy.imageInfo.rotationDegrees
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val rotatedByteArray = bitmap.rotate(orientation, dispatcherDefault)

            val item = FolderItem.ImageFile(name = name)
            createFolderItem(item, savePath)?.let { uri ->
                openOutputStream(uri)?.use { stream ->
                    stream.write(rotatedByteArray)
                }
                onImageSaved(uri)

                val maxSize = dataStoreRepository.getMaxImageSize() * 1024L
                if (rotatedByteArray.size > maxSize) {
                    val rotatedBitmap = BitmapFactory.decodeByteArray(rotatedByteArray, 0, rotatedByteArray.size)
                    val compressedByteArray = rotatedBitmap.compressToSize(targetSize = maxSize, dispatcher = dispatcherDefault)
                    context.deleteFile(uri)
                    createFolderItem(item, savePath)?.let { newUri ->
                        openOutputStream(newUri)?.use { stream ->
                            stream.write(compressedByteArray)
                        }
                        onImageCompressed(uri)
                    }
                }
            }
    }

    suspend fun readNote(path: String): Map<String, String> = withContext(dispatcherIo) {
        val uri = getFolderItems(path).firstOrNull { it.name == "$NOTE_FILE_NAME.txt" }?.uri ?: return@withContext emptyMap()
        context.openInputStream(uri)?.use { inputStream ->
            inputStream.readMap()
        } ?: emptyMap()
    }

    suspend fun writeNote(path: String, data: Map<String, String>) = withContext(dispatcherIo) {
        val uri = getFolderItems(path).firstOrNull { it.name == "$NOTE_FILE_NAME.txt" }?.uri ?: run {
            val newNoteItem = FolderItem.DocumentFile(name = NOTE_FILE_NAME)
            createFolderItem(newNoteItem, path)
        } ?: return@withContext null
        openOutputStream(uri)?.use { outputStream ->
            outputStream.writeMap(data)
        }
    }

    private suspend fun getCollectionModeFolder(): String {
        val isEdit = dataStoreRepository.getCollectionMode()
        return if (isEdit) EDIT_MODE_FOLDER_NAME else NORMAL_MODE_FOLDER_NAME
    }

    private suspend fun getAccountFolder() = dataStoreRepository.getPhotographName()

    private suspend fun getPath(relativePath: String) = "${getAccountFolder()}$SEPARATOR$relativePath"

    private fun getFileExt(folderItem: FolderItem) = when(folderItem) {
        is FolderItem.Folder -> ""
        is FolderItem.ImageFile -> folderItem.type.extension
        is FolderItem.DocumentFile -> folderItem.type.extension
        is FolderItem.ZipFile -> FolderItem.ZipFile.EXTENSION
    }

    companion object {
        private const val EDIT_MODE_FOLDER_NAME = "корректировки"
        private const val NORMAL_MODE_FOLDER_NAME = "сбор_участков"
        const val NOTE_FILE_NAME = "заметка"
    }
}
