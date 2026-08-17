package com.aventumapa.gameengine

import com.aventumapa.core.model.GeographicUnit
import com.aventumapa.core.model.QuizQuestion
import kotlin.random.Random

object QuizFactory {
    fun capitalQuestions(
        entities: List<GeographicUnit>,
        questionCount: Int = 5,
        optionCount: Int = 4,
        seed: Long,
    ): List<QuizQuestion> {
        require(entities.size >= optionCount) { "There must be at least as many entities as options" }
        require(questionCount > 0) { "Question count must be positive" }

        val random = Random(seed)
        return entities.shuffled(random).take(questionCount.coerceAtMost(entities.size)).map { target ->
            val correctCapital = requireNotNull(target.capital)
            val distractors = entities
                .asSequence()
                .filterNot { it.officialCode == target.officialCode }
                .mapNotNull { it.capital }
                .distinct()
                .shuffled(random)
                .take(optionCount - 1)
                .toList()

            QuizQuestion(
                entityCode = target.officialCode,
                prompt = "¿Cuál es la capital de ${target.learningName}?",
                correctAnswer = correctCapital,
                options = (distractors + correctCapital).shuffled(random),
                explanation = "$correctCapital es la capital de ${target.learningName}.",
            )
        }
    }
}

