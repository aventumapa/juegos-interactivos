package com.aventumapa.gameengine

import kotlin.random.Random

object MotivationCoach {
    private val successTemplates = listOf(
        "¡Lo has hecho muy bien, %s!",
        "¡Excelente trabajo, %s! Tu mapa mental está creciendo.",
        "¡Gran respuesta, %s! Sigue explorando.",
        "¡Eso es, %s! Cada capital cuenta.",
        "¡Brillante, %s! Tu aventura va genial.",
        "¡Qué buena memoria, %s!",
    )

    private val matchTemplates = listOf(
        "¡Pareja encontrada, %s!",
        "¡Muy buena memoria, %s!",
        "¡Conexión perfecta, %s!",
        "¡Genial, %s! Estado y capital unidos.",
    )

    private val puzzleTemplates = listOf(
        "¡Pieza en su lugar, %s!",
        "¡Encajó perfecto, %s!",
        "¡Muy bien, %s! México va tomando forma.",
        "¡Excelente ubicación, %s!",
    )

    fun success(alias: String, random: Random = Random.Default): String =
        successTemplates.random(random).format(safeAlias(alias))

    fun match(alias: String, random: Random = Random.Default): String =
        matchTemplates.random(random).format(safeAlias(alias))

    fun puzzle(alias: String, random: Random = Random.Default): String =
        puzzleTemplates.random(random).format(safeAlias(alias))

    fun shouldUsePersonalPraise(random: Random = Random.Default): Boolean = random.nextInt(100) < 68

    private fun safeAlias(alias: String): String = alias.trim().ifBlank { "explorador" }.take(20)
}
