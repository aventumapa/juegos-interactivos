package com.aventumapa.gameengine

enum class EntityDisplayMode {
    STANDARD,
    ACCESSIBLE_HIT_AREA,
    EDUCATIONAL_INSET,
}

data class EntityDisplayDecision(
    val mode: EntityDisplayMode,
    val preserveMapScale: Boolean = true,
    val showConnector: Boolean = false,
    val message: String? = null,
)

object SmallEntityDisplayPolicy {
    private const val MIN_TOUCH_TARGET_DP = 48f
    private const val MIN_RECOGNIZABLE_AREA_DP2 = 42f * 42f

    val knownCandidates: Set<String> = setOf("01", "06", "09", "17", "22", "29")

    fun decide(
        entityCode: String,
        projectedWidthDp: Float,
        projectedHeightDp: Float,
        isSelected: Boolean,
    ): EntityDisplayDecision {
        val area = projectedWidthDp.coerceAtLeast(0f) * projectedHeightDp.coerceAtLeast(0f)
        val touchTooSmall = projectedWidthDp < MIN_TOUCH_TARGET_DP || projectedHeightDp < MIN_TOUCH_TARGET_DP
        val hardToRecognize = area < MIN_RECOGNIZABLE_AREA_DP2
        val candidate = entityCode in knownCandidates

        return when {
            isSelected && (hardToRecognize || candidate) -> EntityDisplayDecision(
                mode = EntityDisplayMode.EDUCATIONAL_INSET,
                showConnector = true,
                message = "Lo ampliamos para que puedas observar mejor su forma",
            )

            touchTooSmall -> EntityDisplayDecision(mode = EntityDisplayMode.ACCESSIBLE_HIT_AREA)
            else -> EntityDisplayDecision(mode = EntityDisplayMode.STANDARD)
        }
    }
}

