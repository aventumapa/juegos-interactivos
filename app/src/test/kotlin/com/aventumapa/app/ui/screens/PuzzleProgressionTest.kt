package com.aventumapa.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleProgressionTest {
    private val entityCodes = (1..32).map { it.toString().padStart(2, '0') }

    @Test
    fun `shows six entities per completed batch`() {
        assertEquals(entityCodes.take(6), puzzleBatchForProgress(entityCodes, placedCount = 0))
        assertEquals(entityCodes.drop(6).take(6), puzzleBatchForProgress(entityCodes, placedCount = 6))
        assertEquals(entityCodes.drop(24).take(6), puzzleBatchForProgress(entityCodes, placedCount = 24))
    }

    @Test
    fun `keeps the same batch until all six pieces are placed`() {
        val firstBatch = entityCodes.take(6)
        assertEquals(firstBatch, puzzleBatchForProgress(entityCodes, placedCount = 1))
        assertEquals(firstBatch, puzzleBatchForProgress(entityCodes, placedCount = 5))
    }

    @Test
    fun `last batch contains the two remaining entities`() {
        assertEquals(entityCodes.drop(30), puzzleBatchForProgress(entityCodes, placedCount = 30))
        assertTrue(puzzleBatchForProgress(entityCodes, placedCount = 32).isEmpty())
    }
}
