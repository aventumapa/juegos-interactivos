package com.aventumapa.content.mexico

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MexicoContentTest {
    @Test
    fun `contains 32 unique federal entities`() {
        assertEquals(32, MexicoContent.entities.size)
        assertEquals(32, MexicoContent.entities.map { it.officialCode }.distinct().size)
    }

    @Test
    fun `every entity has a capital and traceability`() {
        assertTrue(MexicoContent.entities.all { !it.capital.isNullOrBlank() })
        assertTrue(MexicoContent.entities.all { !it.officialCapital.isNullOrBlank() })
        assertTrue(MexicoContent.entities.all { it.sourceId.isNotBlank() })
        assertTrue(MexicoContent.entities.all { it.contentVersion == MexicoContent.VERSION })
    }

    @Test
    fun `search ignores accents`() {
        assertEquals("Michoacán de Ocampo", MexicoContent.search("michoacan").single().officialName)
    }

    @Test
    fun `learning names use short familiar forms while official names remain available`() {
        val campeche = requireNotNull(MexicoContent.findByCode("04"))
        val coahuila = requireNotNull(MexicoContent.findByCode("05"))
        val guerrero = requireNotNull(MexicoContent.findByCode("12"))
        val veracruz = requireNotNull(MexicoContent.findByCode("30"))

        assertEquals("Campeche", campeche.capital)
        assertEquals("San Francisco de Campeche", campeche.officialCapital)
        assertEquals("Coahuila", coahuila.learningName)
        assertEquals("Chilpancingo", guerrero.capital)
        assertEquals("Veracruz", veracruz.learningName)
        assertEquals("Xalapa", veracruz.capital)
    }
}
