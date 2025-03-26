package ru.geowork.photoapp.data.sync

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.extensions.updatesOrEmpty
import io.github.xxfast.kstore.file.extensions.listStoreOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import ru.geowork.photoapp.data.ArchiveRepository
import ru.geowork.photoapp.data.FilesRepository
import ru.geowork.photoapp.data.UploadRepository
import ru.geowork.photoapp.di.ApplicationIoScope
import ru.geowork.photoapp.di.ApplicationScope
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.model.SyncState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationIoScope private val applicationIoScope: CoroutineScope,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val archiveRepository: ArchiveRepository,
    private val uploadRepository: UploadRepository,
    private val filesRepository: FilesRepository
) {

    private val storePath = Path("${context.filesDir}/$SYNC_STATE_FILE_NAME")
    private val store: KStore<List<SavedSyncStateEntry>> = listStoreOf(file = storePath)

    private val _syncStateFlow = MutableStateFlow<Map<String, SyncState>>(mapOf())
    val syncStateFlow = _syncStateFlow.asStateFlow()

    init {
        syncStoreWithFlow()
    }

    suspend fun sync(path: String) {
        runCatching {
            val archive = filesRepository.getFolderItems(path)
                .filterIsInstance<FolderItem.ZipFile>()
                .firstOrNull() ?: run {
                archiveRepository.archiveFolder(path) { progress ->
                    updateProgress(path, progress)
                }
            }
            if (archive != null) {
                uploadRepository.upload(archive) { progress ->
                    updateProgress(path,  progress)
                }
                saveSyncState(path, SavedSyncState.UPLOADED)
                updateProgress(path, SyncState.Uploaded)
            } else {
                saveSyncState(path, SavedSyncState.FAILED)
                updateProgress(path, SyncState.Failed(Exception("Не удалось создать архив")))
            }
        }.onFailure { e ->
            updateProgress(path, SyncState.Failed(e))
        }
    }

    fun saveSyncState(path: String, state: SavedSyncState) = applicationIoScope.launch {
        store.update { list ->
            val savedEntry = list?.firstOrNull { path == it.path }
            if (savedEntry != null) {
                list.map { entry ->
                    if (path == entry.path) {
                        entry.copy(state = state.value)
                    } else entry
                }
            } else {
                val newEntry = SavedSyncStateEntry(path, state.value)
                list?.plus(newEntry)
            }
        }
    }

    fun deleteSyncStateStore() = applicationIoScope.launch {
        store.update { listOf() }
        _syncStateFlow.value = mapOf()
    }

    private fun updateProgress(id: String, progress: SyncState) {
        _syncStateFlow.value = syncStateFlow.value.toMutableMap().also {
            it[id] = progress
        }
    }

    private fun syncStoreWithFlow() = store.updatesOrEmpty.onEach { list ->
        list.forEach {
            val savedSyncState = SavedSyncState.fromValue(it.state)
            val syncState = SyncState.fromSavedSyncState(savedSyncState)
            updateProgress(it.path, syncState)
        }
    }.launchIn(applicationScope)

    companion object {
        private const val SYNC_STATE_FILE_NAME = "sync_state"
    }
}
