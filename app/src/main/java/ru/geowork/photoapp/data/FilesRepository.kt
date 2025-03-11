package ru.geowork.photoapp.data

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.util.createFileLikeExt
import ru.geowork.photoapp.util.createFileLikeUri
import ru.geowork.photoapp.util.deleteFile
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
            path = getAccountPath(relativePath),
            ext = fileExt
        )
    }

    suspend fun getFolderItems(path: String) = withContext(dispatcherIo) {
        val mediaStoreFiles = context.getFiles(getAccountPath(path))
        mediaStoreFiles.map { mediaStoreFile ->
            val id = mediaStoreFile.id.toString()
            val fileExt = getExtensionFromMimeType(mediaStoreFile.mimeType) ?: ""
            when {
                fileExt.isEmpty() -> FolderItem.Folder(
                    id = id,
                    name = mediaStoreFile.displayName,
                    uri = mediaStoreFile.uri,
                    relativePath = "$path/${mediaStoreFile.displayName}",
                    visibleName = mediaStoreFile.displayName
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
        context.createFileLikeUri(sourceUri, fileName, getAccountPath(path))?.let { newUri ->
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

    private suspend fun getAccountFolder() = dataStoreRepository.getPhotographName()

    private suspend fun getAccountPath(relativePath: String) = "${getAccountFolder()}/$relativePath"
}
