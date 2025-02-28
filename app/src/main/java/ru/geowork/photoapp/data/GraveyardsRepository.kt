package ru.geowork.photoapp.data

import ru.geowork.photoapp.model.Graveyard
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraveyardsRepository @Inject constructor(
    private val filesRepository: FilesRepository
) {
    fun getGraveyards() = listOf(
        Graveyard("Домодедовское", "Dom"),
        Graveyard("Даниловское (Центральное)", "DanC"),
        Graveyard("Старо-Покровское", "SPokr"),
        Graveyard("Ивановское", "Ivan"),
        Graveyard("Даниловское (мусульманское)", "DanM"),
        Graveyard("Лианозовское", "Lian"),
        Graveyard("Пятницкое", "Pyat"),
        Graveyard("Старо-Марковское", "SMark"),
        Graveyard("Большое Свинорье", "Svin"),
        Graveyard("Исаково", "Isak"),
        Graveyard("Сосенское", "Sos"),
        Graveyard("Троицкое городское", "Troy"),
        Graveyard("Белоусово", "Bel"),
        Graveyard("Губцево", "Gub"),
        Graveyard("Зосимова Пустынь", "Zos"),
        Graveyard("Красное", "Kras"),
        Graveyard("Руднево", "Rudn"),
        Graveyard("Станиславское", "Stan"),
        Graveyard("Середневское", "Ser"),
        Graveyard("Рублевское", "Rubl"),
        Graveyard("Зеленоградское (Центральное)", "ZelC"),
        Graveyard("Рождественское", "Rozh"),
        Graveyard("Головинское", "Gol"),
        Graveyard("Черкизовское (Северное)", "CheS"),
        Graveyard("Перепечинское", "Per")
    )
}
