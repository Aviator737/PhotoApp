package ru.geowork.photoapp.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.util.createPathInDocuments
import ru.geowork.photoapp.util.getFilesFromDocuments
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    private val accountRepository: AccountRepository
) {
    suspend fun createFolderItem(relativePath: String, folderItem: FolderItem) {
        when(folderItem) {
            is FolderItem.Folder -> createPath(folderItem.name, relativePath)
            is FolderItem.ImageFile -> TODO()
            is FolderItem.TextFile -> TODO()
            else -> {}
        }
    }

    private suspend fun createPath(folderName: String, path: String) = withContext(dispatcherIo) {
        context.createPathInDocuments(folderName,"${getAccountFolder()}${if (path.isNotEmpty()) "/$path" else ""}")
    }

    suspend fun getFolderItems(path: String) = withContext(dispatcherIo) {
        context.getFilesFromDocuments("${getAccountFolder()}/$path")
    }

    private suspend fun getAccountFolder() = accountRepository.getAccount().photographName
}
