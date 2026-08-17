package com.aventumapa.gameengine

import com.aventumapa.core.model.GeographicUnit
import com.aventumapa.core.model.GeographicUnitType
import com.aventumapa.core.model.LearningStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    private val entities = (1..8).map { number ->
        GeographicUnit(
            officialCode = number.toString().padStart(2, '0'),
            officialName = "Entidad $number",
            normalizedName = "entidad $number",
            type = GeographicUnitType.FEDERAL_ENTITY,
            capital = "Capital $number",
            educationalRegion = "Piloto",
            sourceId = "test",
            contentVersion = "test",
        )
    }

    @Test
    fun `quiz generation is reproducible and has unique options`() {
        val first = QuizFactory.capitalQuestions(entities, seed = 42L)
        val second = QuizFactory.capitalQuestions(entities, seed = 42L)

        assertEquals(first, second)
        assertTrue(first.all { it.options.size == 4 })
        assertTrue(first.all { it.options.distinct().size == 4 })
        assertTrue(first.all { it.correctAnswer in it.options })
    }

    @Test
    fun `memory deck contains one entity and one capital per pair`() {
        val deck = MemoryDeckFactory.entityCapitalDeck(entities, pairCount = 4, seed = 9L)

        assertEquals(8, deck.size)
        assertEquals(4, deck.groupBy { it.pairId }.size)
        assertTrue(deck.groupBy { it.pairId }.values.all { it.size == 2 })
    }

    @Test
    fun `small entity selection opens inset and never changes map scale`() {
        val decision = SmallEntityDisplayPolicy.decide("29", 12f, 10f, isSelected = true)

        assertEquals(EntityDisplayMode.EDUCATIONAL_INSET, decision.mode)
        assertTrue(decision.showConnector)
        assertTrue(decision.preserveMapScale)
    }

    @Test
    fun `large entity uses standard representation`() {
        val decision = SmallEntityDisplayPolicy.decide("08", 110f, 90f, isSelected = false)

        assertEquals(EntityDisplayMode.STANDARD, decision.mode)
        assertFalse(decision.showConnector)
    }

    @Test
    fun `incorrect answer schedules friendly review tomorrow`() {
        val update = MasteryCalculator.update(
            currentStatus = LearningStatus.LEARNING,
            wasCorrect = false,
            usedHint = false,
            todayEpochDay = 100,
        )

        assertEquals(LearningStatus.REVIEW, update.status)
        assertEquals(101, update.nextReviewEpochDay)
    }

    @Test
    fun `motivation coach personalizes phrases without exposing extra data`() {
        val phrase = MotivationCoach.success("Luna", kotlin.random.Random(7))

        assertTrue(phrase.contains("Luna"))
        assertTrue(phrase.length < 100)
    }
}
