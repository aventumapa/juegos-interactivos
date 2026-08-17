package com.aventumapa.gameengine

import com.aventumapa.core.model.LearningStatus

data class MasteryUpdate(
    val status: LearningStatus,
    val intervalDays: Int,
    val nextReviewEpochDay: Long,
)

object MasteryCalculator {
    fun update(
        currentStatus: LearningStatus,
        wasCorrect: Boolean,
        usedHint: Boolean,
        todayEpochDay: Long,
    ): MasteryUpdate {
        if (!wasCorrect) {
            return MasteryUpdate(
                status = LearningStatus.REVIEW,
                intervalDays = 1,
                nextReviewEpochDay = todayEpochDay + 1,
            )
        }

        val nextStatus = when (currentStatus) {
            LearningStatus.NEW -> LearningStatus.LEARNING
            LearningStatus.LEARNING -> if (usedHint) LearningStatus.LEARNING else LearningStatus.REVIEW
            LearningStatus.REVIEW -> if (usedHint) LearningStatus.REVIEW else LearningStatus.MASTERED
            LearningStatus.MASTERED -> LearningStatus.MASTERED
        }
        val interval = when (nextStatus) {
            LearningStatus.NEW -> 0
            LearningStatus.LEARNING -> if (usedHint) 1 else 2
            LearningStatus.REVIEW -> if (usedHint) 2 else 4
            LearningStatus.MASTERED -> if (usedHint) 7 else 14
        }

        return MasteryUpdate(nextStatus, interval, todayEpochDay + interval)
    }
}

