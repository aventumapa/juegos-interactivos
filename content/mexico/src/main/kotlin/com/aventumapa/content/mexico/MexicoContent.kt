package com.aventumapa.content.mexico

import com.aventumapa.core.model.GeographicUnit
import com.aventumapa.core.model.GeographicUnitType
import java.text.Normalizer
import java.util.Locale

object MexicoContent {
    const val VERSION = "mx-entities-2026.08-pilot"
    const val SOURCE_ID = "INEGI-MG-catalog-validation-pending"

    val entities: List<GeographicUnit> = listOf(
        entity("01", "Aguascalientes", "Aguascalientes", Region.CENTER, small = true),
        entity("02", "Baja California", "Mexicali", Region.NORTHWEST),
        entity("03", "Baja California Sur", "La Paz", Region.NORTHWEST),
        entity("04", "Campeche", "San Francisco de Campeche", Region.SOUTHEAST),
        entity("05", "Coahuila de Zaragoza", "Saltillo", Region.NORTHEAST),
        entity("06", "Colima", "Colima", Region.WEST, small = true),
        entity("07", "Chiapas", "Tuxtla Gutiérrez", Region.SOUTHEAST),
        entity("08", "Chihuahua", "Chihuahua", Region.NORTHWEST),
        entity(
            code = "09",
            officialName = "Ciudad de México",
            capital = "Ciudad de México",
            region = Region.CENTER,
            learningName = "Ciudad de México",
            small = true,
        ),
        entity("10", "Durango", "Victoria de Durango", Region.NORTHWEST),
        entity("11", "Guanajuato", "Guanajuato", Region.CENTER),
        entity("12", "Guerrero", "Chilpancingo de los Bravo", Region.SOUTH),
        entity("13", "Hidalgo", "Pachuca de Soto", Region.CENTER),
        entity("14", "Jalisco", "Guadalajara", Region.WEST),
        entity("15", "México", "Toluca de Lerdo", Region.CENTER, learningName = "Estado de México"),
        entity("16", "Michoacán de Ocampo", "Morelia", Region.WEST),
        entity("17", "Morelos", "Cuernavaca", Region.CENTER, small = true),
        entity("18", "Nayarit", "Tepic", Region.WEST),
        entity("19", "Nuevo León", "Monterrey", Region.NORTHEAST),
        entity("20", "Oaxaca", "Oaxaca de Juárez", Region.SOUTH),
        entity("21", "Puebla", "Heroica Puebla de Zaragoza", Region.CENTER),
        entity("22", "Querétaro", "Santiago de Querétaro", Region.CENTER, small = true),
        entity("23", "Quintana Roo", "Chetumal", Region.SOUTHEAST),
        entity("24", "San Luis Potosí", "San Luis Potosí", Region.NORTHEAST),
        entity("25", "Sinaloa", "Culiacán Rosales", Region.NORTHWEST),
        entity("26", "Sonora", "Hermosillo", Region.NORTHWEST),
        entity("27", "Tabasco", "Villahermosa", Region.SOUTHEAST),
        entity("28", "Tamaulipas", "Ciudad Victoria", Region.NORTHEAST),
        entity("29", "Tlaxcala", "Tlaxcala de Xicohténcatl", Region.CENTER, small = true),
        entity("30", "Veracruz de Ignacio de la Llave", "Xalapa-Enríquez", Region.EAST),
        entity("31", "Yucatán", "Mérida", Region.SOUTHEAST),
        entity("32", "Zacatecas", "Zacatecas", Region.CENTER),
    )

    val pilotEntities: List<GeographicUnit> = entities.filter { it.educationalRegion == Region.CENTER.label }

    fun findByCode(code: String): GeographicUnit? = entities.find { it.officialCode == code }

    fun search(query: String): List<GeographicUnit> {
        val normalizedQuery = query.normalizeForSearch()
        return entities.filter {
            it.normalizedName.contains(normalizedQuery) ||
                it.capital.orEmpty().normalizeForSearch().contains(normalizedQuery)
        }
    }

    private fun entity(
        code: String,
        officialName: String,
        capital: String,
        region: Region,
        learningName: String = officialName,
        small: Boolean = false,
    ) = GeographicUnit(
        officialCode = code,
        officialName = officialName,
        learningName = learningName,
        normalizedName = officialName.normalizeForSearch(),
        type = GeographicUnitType.FEDERAL_ENTITY,
        capital = capital,
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

