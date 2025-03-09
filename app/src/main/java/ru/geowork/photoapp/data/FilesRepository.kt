package ru.geowork.photoapp.data

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.util.createFile
import ru.geowork.photoapp.util.getFiles
import ru.geowork.photoapp.util.openOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    private val dataStoreRepository: DataStoreRepository
) {
    suspend fun createFolderItem(folderItem: FolderItem): Uri? {
        val fileExt = when(folderItem) {
            is FolderItem.Unknown,
            is FolderItem.Folder,
            is FolderItem.PhotoRow -> ""
            is FolderItem.ImageFile -> ".jpg"
            is FolderItem.TextFile -> ".txt"
        }
        return context.createFile(
            fileName = folderItem.name,
            path = "${getAccountFolder()}/${folderItem.path}",
            ext = fileExt
        )
    }

    suspend fun getFolderItems(path: String) = withContext(dispatcherIo) {
        context.getFiles("${getAccountFolder()}/$path", path)
    }

    suspend fun openOutputStream(uri: Uri) = withContext(dispatcherIo) {
        context.openOutputStream(uri)
    }

    suspend fun copyToAppFolder(sourceUri: Uri, folderItem: FolderItem) {

    }

    private suspend fun getAccountFolder() = dataStoreRepository.getPhotographName()
}
