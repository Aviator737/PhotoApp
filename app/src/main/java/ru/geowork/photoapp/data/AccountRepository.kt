package ru.geowork.photoapp.data

import ru.geowork.photoapp.model.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend fun savePhotographName(value: String) = dataStoreRepository.savePhotographName(value)
    suspend fun saveSupervisorName(value: String) = dataStoreRepository.saveSupervisorName(value)

    suspend fun getAccount(): Account = Account(
        photographName = dataStoreRepository.getPhotographName(),
        supervisorName = dataStoreRepository.getSupervisorName()
    )

    fun getSupervisors() = listOf(
        "Михаил Кулешов", "Сергей Новиков"
    )
}
