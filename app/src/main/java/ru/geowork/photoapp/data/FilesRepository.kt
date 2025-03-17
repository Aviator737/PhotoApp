package ru.geowork.photoapp.data

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.di.DispatcherDefault
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.util.compressImage
import ru.geowork.photoapp.util.createFileLikeExt
import ru.geowork.photoapp.util.createFileLikeUri
import ru.geowork.photoapp.util.deleteFile
import ru.geowork.photoapp.util.deleteFolderRecursively
import ru.geowork.photoapp.util.getExtensionFromMimeType
import ru.geowork.photoapp.util.getFiles
import ru.geowork.photoapp.util.openInputStream
import ru.geowork.photoapp.util.openOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    @DispatcherDefault private val dispatcherDefault: CoroutineDispatcher,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend fun createFolderItem(folderItem: FolderItem, relativePath: String): Uri? {
        val fileExt = when(folderItem) {
            is FolderItem.Folder -> ""
            is FolderItem.ImageFile -> folderItem.type.extension
            is FolderItem.DocumentFile -> folderItem.type.extension
        }
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
                    relativePath = "$path/${mediaStoreFile.displayName}"
                )
                FolderItem.ImageFile.ImageType.entries.map { it.extension }.contains(fileExt) -> FolderItem.ImageFile(
                    id = id,
                    name = mediaStoreFile.displayName,
                    uri = mediaStoreFile.uri,
                    parentFolder = path,
                    size = mediaStoreFile.size,
                    type = FolderItem.ImageFile.ImageType.getTypeFromExtension(fileExt)
                )
                else -> FolderItem.DocumentFile(
                    id = id,
                    name = mediaStoreFile.displayName,
                    uri = mediaStoreFile.uri,
                    type = FolderItem.DocumentFile.DocumentType.getTypeFromExtension(fileExt)
                )
            }
        }
    }

    suspend fun copyFromUri(sourceUri: Uri, fileName: String, path: String) = withContext(dispatcherIo) {
        context.createFileLikeUri(sourceUri, fileName, getPath(path))?.let { newUri ->
            context.openInputStream(sourceUri)?.use { inputStream ->
                context.openOutputStream(newUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    suspend fun openOutputStream(uri: Uri) = withContext(dispatcherIo) {
        context.openOutputStream(uri)
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

    suspend fun compressImage(uri: Uri): Boolean = withContext(dispatcherIo) {
        val maxSize = dataStoreRepository.getMaxImageSize() * 1024L
        withContext(dispatcherDefault) {
            context.compressImage(uri, maxSize)
        }
    }

    private suspend fun getCollectionModeFolder(): String {
        val isEdit = dataStoreRepository.getCollectionMode()
        return if (isEdit) EDIT_MODE_FOLDER_NAME else NORMAL_MODE_FOLDER_NAME
    }

    private suspend fun getAccountFolder() = dataStoreRepository.getPhotographName()

    private suspend fun getPath(relativePath: String) =
        "${getAccountFolder()}/$relativePath"

    companion object {
        private const val EDIT_MODE_FOLDER_NAME = "корректировки"
        private const val NORMAL_MODE_FOLDER_NAME = "сбор_участков"
    }
}
