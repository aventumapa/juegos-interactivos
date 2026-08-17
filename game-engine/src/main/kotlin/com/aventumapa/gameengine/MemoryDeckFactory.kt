package com.aventumapa.gameengine

import com.aventumapa.core.model.GeographicUnit
import com.aventumapa.core.model.MemoryCard
import com.aventumapa.core.model.MemoryCardKind
import kotlin.random.Random

object MemoryDeckFactory {
    fun entityCapitalDeck(
        entities: List<GeographicUnit>,
        pairCount: Int = 4,
        seed: Long,
    ): List<MemoryCard> {
        require(pairCount in 2..entities.size)
        val random = Random(seed)
        return entities.shuffled(random).take(pairCount).flatMap { entity ->
            listOf(
                MemoryCard(
                    id = "${entity.officialCode}-entity",
                    pairId = entity.officialCode,
                    label = entity.learningName,
                    kind = MemoryCardKind.ENTITY,
                ),
                MemoryCard(
                    id = "${entity.officialCode}-capital",
                    pairId = entity.officialCode,
                    label = requireNotNull(entity.capital),
                    kind = MemoryCardKind.CAPITAL,
                ),
            )
        }.shuffled(random)
    }
}

