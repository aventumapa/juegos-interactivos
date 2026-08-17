package com.aventumapa.content.mexico

import com.aventumapa.core.model.GeographicUnit
import com.aventumapa.core.model.GeographicUnitType
import java.text.Normalizer
import java.util.Locale

object MexicoContent {
    const val VERSION = "mx-entities-2026.08-short-learning-names"
    const val SOURCE_ID = "INEGI-CUC-AAGEE-2026-08-17"

    val entities: List<GeographicUnit> = listOf(
        entity("01", "Aguascalientes", "Aguascalientes", Region.CENTER, small = true),
        entity("02", "Baja California", "Mexicali", Region.NORTHWEST),
        entity("03", "Baja California Sur", "La Paz", Region.NORTHWEST),
        entity("04", "Campeche", "San Francisco de Campeche", Region.SOUTHEAST, learningCapital = "Campeche"),
        entity("05", "Coahuila de Zaragoza", "Saltillo", Region.NORTHEAST, learningName = "Coahuila"),
        entity("06", "Colima", "Colima", Region.WEST, small = true),
        entity("07", "Chiapas", "Tuxtla Gutiérrez", Region.SOUTHEAST),
        entity("08", "Chihuahua", "Chihuahua", Region.NORTHWEST),
        entity(
            code = "09",
            officialName = "Ciudad de México",
            officialCapital = "Ciudad de México",
            region = Region.CENTER,
            learningName = "Ciudad de México",
            small = true,
        ),
        entity("10", "Durango", "Victoria de Durango", Region.NORTHWEST, learningCapital = "Durango"),
        entity("11", "Guanajuato", "Guanajuato", Region.CENTER),
        entity("12", "Guerrero", "Chilpancingo de los Bravo", Region.SOUTH, learningCapital = "Chilpancingo"),
        entity("13", "Hidalgo", "Pachuca de Soto", Region.CENTER, learningCapital = "Pachuca"),
        entity("14", "Jalisco", "Guadalajara", Region.WEST),
        entity("15", "México", "Toluca de Lerdo", Region.CENTER, learningName = "Estado de México", learningCapital = "Toluca"),
        entity("16", "Michoacán de Ocampo", "Morelia", Region.WEST, learningName = "Michoacán"),
        entity("17", "Morelos", "Cuernavaca", Region.CENTER, small = true),
        entity("18", "Nayarit", "Tepic", Region.WEST),
        entity("19", "Nuevo León", "Monterrey", Region.NORTHEAST),
        entity("20", "Oaxaca", "Oaxaca de Juárez", Region.SOUTH, learningCapital = "Oaxaca"),
        entity("21", "Puebla", "Heroica Puebla de Zaragoza", Region.CENTER, learningCapital = "Puebla"),
        entity("22", "Querétaro", "Santiago de Querétaro", Region.CENTER, learningCapital = "Querétaro", small = true),
        entity("23", "Quintana Roo", "Chetumal", Region.SOUTHEAST),
        entity("24", "San Luis Potosí", "San Luis Potosí", Region.NORTHEAST),
        entity("25", "Sinaloa", "Culiacán Rosales", Region.NORTHWEST, learningCapital = "Culiacán"),
        entity("26", "Sonora", "Hermosillo", Region.NORTHWEST),
        entity("27", "Tabasco", "Villahermosa", Region.SOUTHEAST),
        entity("28", "Tamaulipas", "Ciudad Victoria", Region.NORTHEAST),
        entity("29", "Tlaxcala", "Tlaxcala de Xicohténcatl", Region.CENTER, learningCapital = "Tlaxcala", small = true),
        entity("30", "Veracruz de Ignacio de la Llave", "Xalapa-Enríquez", Region.EAST, learningName = "Veracruz", learningCapital = "Xalapa"),
        entity("31", "Yucatán", "Mérida", Region.SOUTHEAST),
        entity("32", "Zacatecas", "Zacatecas", Region.CENTER),
    )

    val pilotEntities: List<GeographicUnit> = entities.filter { it.educationalRegion == Region.CENTER.label }

    fun findByCode(code: String): GeographicUnit? = entities.find { it.officialCode == code }

    fun search(query: String): List<GeographicUnit> {
        val normalizedQuery = query.normalizeForSearch()
        return entities.filter {
            it.normalizedName.contains(normalizedQuery) ||
                it.learningName.normalizeForSearch().contains(normalizedQuery) ||
                it.capital.orEmpty().normalizeForSearch().contains(normalizedQuery) ||
                it.officialCapital.orEmpty().normalizeForSearch().contains(normalizedQuery)
        }
    }

    private fun entity(
        code: String,
        officialName: String,
        officialCapital: String,
        region: Region,
        learningName: String = officialName,
        learningCapital: String = officialCapital,
        small: Boolean = false,
    ) = GeographicUnit(
        officialCode = code,
        officialName = officialName,
        learningName = learningName,
        normalizedName = officialName.normalizeForSearch(),
        type = GeographicUnitType.FEDERAL_ENTITY,
        capital = learningCapital,
        officialCapital = officialCapital,
        educationalRegion = region.label,
        smallEntityCandidate = small,
        sourceId = SOURCE_ID,
        contentVersion = VERSION,
    )
}

enum class Region(val label: String) {
    NORTHWEST("Noroeste"),
    NORTHEAST("Noreste"),
    WEST("Occidente"),
    CENTER("Centro"),
    EAST("Oriente"),
    SOUTH("Sur"),
    SOUTHEAST("Sureste"),
}

private fun String.normalizeForSearch(): String = Normalizer
    .normalize(lowercase(Locale("es", "MX")), Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "")
