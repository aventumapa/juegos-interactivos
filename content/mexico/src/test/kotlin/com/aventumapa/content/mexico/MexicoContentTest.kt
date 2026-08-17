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
        assertTrue(MexicoContent.entities.all { it.sourceId.isNotBlank() })
        assertTrue(MexicoContent.entities.all { it.contentVersion == MexicoContent.VERSION })
    }

    @Test
    fun `search ignores accents`() {
        assertEquals("Michoacán de Ocampo", MexicoContent.search("michoacan").single().officialName)
    }
}

