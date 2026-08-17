package com.aventumapa.core.model

enum class GeographicUnitType {
    COUNTRY,
    FEDERAL_ENTITY,
    MUNICIPALITY,
    TERRITORIAL_DEMARCATION,
}

enum class LearningStatus {
    NEW,
    LEARNING,
    REVIEW,
    MASTERED,
}

enum class NarratorVoice(
    val persistedId: String,
    val characterName: String,
    val roleLabel: String,
) {
    BOY("boy", "Matein Pompin", "Voz niño"),
    GIRL("girl", "Andreita", "Voz niña"),
    ELEGANT_MAN("elegant_man", "Maximo", "Voz elegante"),
    FRIENDLY_WOMAN("friendly_woman", "Claudis", "Voz amigable");

    companion object {
        fun fromPersistedId(value: String?): NarratorVoice = entries.firstOrNull {
            it.persistedId == value
        } ?: FRIENDLY_WOMAN
    }
}

data class GeographicUnit(
    val officialCode: String,
    val officialName: String,
    val learningName: String = officialName,
    val normalizedName: String,
    val type: GeographicUnitType,
    val capital: String?,
    val officialCapital: String? = capital,
    val educationalRegion: String,
    val smallEntityCandidate: Boolean = false,
    val sourceId: String,
    val contentVersion: String,
)

data class ChildProfile(
    val alias: String = "",
    val avatarId: String = "brujula",
    val experience: Int = 0,
    val stars: Int = 0,
    val completedRounds: Int = 0,
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0,
    val streakDays: Int = 0,
    val lastActivityEpochDay: Long = 0L,
    val narratorVoice: NarratorVoice = NarratorVoice.FRIENDLY_WOMAN,
) {
    val hasProfile: Boolean get() = alias.isNotBlank()
    val level: Int get() = (experience / 200) + 1
    val accuracy: Int get() = if (totalAnswers == 0) 0 else (correctAnswers * 100) / totalAnswers
}

data class LearningRecord(
    val itemId: String,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val hintsUsed: Int,
    val status: LearningStatus,
    val nextReviewEpochDay: Long,
)

data class QuizQuestion(
    val entityCode: String,
    val prompt: String,
    val correctAnswer: String,
    val options: List<String>,
    val explanation: String,
)

data class MemoryCard(
    val id: String,
    val pairId: String,
    val label: String,
    val kind: MemoryCardKind,
)

enum class MemoryCardKind {
    ENTITY,
    CAPITAL,
}
