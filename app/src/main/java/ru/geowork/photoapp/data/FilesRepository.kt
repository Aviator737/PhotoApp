package ru.geowork.photoapp.data

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.geowork.photoapp.di.DispatcherIo
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.util.createFileInDocuments
import ru.geowork.photoapp.util.getFilesFromDocuments
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatcherIo private val dispatcherIo: CoroutineDispatcher,
    private val accountRepository: AccountRepository
) {
    suspend fun createFolderItem(folderItem: FolderItem): Uri? {
        val fileExt = when(folderItem) {
            is FolderItem.Unknown,
            is FolderItem.Folder,
            is FolderItem.PhotoRow -> ""
            is FolderItem.ImageFile -> ".jpg"
            is FolderItem.TextFile -> ".txt"
        }
        return context.createFileInDocuments(
            fileName = folderItem.name,
            path = "${getAccountFolder()}/${folderItem.path}",
            ext = fileExt
        )
    }

    suspend fun getFolderItems(path: String) = withContext(dispatcherIo) {
        context.getFilesFromDocuments("${getAccountFolder()}/$path")
    }

    private suspend fun getAccountFolder() = accountRepository.getAccount().photographName
}
