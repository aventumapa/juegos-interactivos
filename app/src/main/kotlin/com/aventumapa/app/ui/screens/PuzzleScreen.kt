package com.aventumapa.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.audio.SoundCue
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.CompassGuide
import com.aventumapa.app.ui.map.MexicoStateShape
import com.aventumapa.app.ui.map.mapColorForCode
import com.aventumapa.app.ui.map.nationalPath
import com.aventumapa.app.ui.map.rememberMexicoMapGeometry
import com.aventumapa.app.ui.map.silhouettePath
import com.aventumapa.app.ui.theme.AtlasNight
import com.aventumapa.app.ui.theme.CardNavy
import com.aventumapa.app.ui.theme.CardRaised
import com.aventumapa.app.ui.theme.CyanElectric
import com.aventumapa.app.ui.theme.EmeraldGlow
import com.aventumapa.app.ui.theme.IceWhite
import com.aventumapa.app.ui.theme.SunYellow
import com.aventumapa.content.mexico.MexicoContent
import com.aventumapa.core.model.ChildProfile
import com.aventumapa.gameengine.MotivationCoach
import kotlinx.coroutines.delay
import kotlin.math.sqrt

internal const val PUZZLE_BATCH_SIZE = 6

internal fun puzzleBatchForProgress(codes: List<String>, placedCount: Int): List<String> {
    if (codes.isEmpty() || placedCount >= codes.size) return emptyList()
    val firstIndex = (placedCount / PUZZLE_BATCH_SIZE) * PUZZLE_BATCH_SIZE
    return codes.drop(firstIndex.coerceAtMost(codes.size)).take(PUZZLE_BATCH_SIZE)
}

@Composable
fun PuzzleScreen(
    profile: ChildProfile,
    onBack: () -> Unit,
    onRoundFinished: (correct: Int, total: Int, stars: Int) -> Unit,
    onSpeak: (String) -> Unit,
    onSound: (SoundCue) -> Unit,
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val previousOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = previousOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val geometry = rememberMexicoMapGeometry()
    val shapesByCode = remember(geometry) { geometry.associateBy { it.code } }
    val puzzleCodes = remember(geometry) { geometry.map { it.code }.sorted() }
    val positions = remember { mutableStateMapOf<String, Offset>() }
    val startPositions = remember { mutableMapOf<String, Offset>() }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var placedCodes by remember { mutableStateOf(setOf<String>()) }
    var activeCode by remember { mutableStateOf<String?>(null) }
    var lastPlacedCode by remember { mutableStateOf<String?>(null) }
    var rewardClaimed by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val pieceWidth = with(density) { 72.dp.toPx() }
    val pieceHeight = with(density) { 58.dp.toPx() }
    val grabRadius = with(density) { 52.dp.toPx() }
    val snapRadius = with(density) { 74.dp.toPx() }
    val currentBatchCodes = remember(puzzleCodes, placedCodes.size) {
        puzzleBatchForProgress(puzzleCodes, placedCodes.size)
    }
    val currentBatchNumber = (placedCodes.size / PUZZLE_BATCH_SIZE + 1)
        .coerceAtMost((puzzleCodes.size + PUZZLE_BATCH_SIZE - 1) / PUZZLE_BATCH_SIZE)
    val totalBatches = (puzzleCodes.size + PUZZLE_BATCH_SIZE - 1) / PUZZLE_BATCH_SIZE
    val complete = puzzleCodes.isNotEmpty() && placedCodes.size == puzzleCodes.size

    LaunchedEffect(canvasSize, currentBatchCodes, placedCodes.size) {
        if (canvasSize == IntSize.Zero) return@LaunchedEffect
        val width = canvasSize.width.toFloat()
        val height = canvasSize.height.toFloat()
        val sideLeft = width * 0.735f
        val sideWidth = width - sideLeft
        currentBatchCodes.forEachIndexed { index, code ->
            val column = index % 2
            val row = index / 2
            val center = Offset(
                x = sideLeft + sideWidth * (0.29f + column * 0.42f),
                y = height * (0.39f + row * 0.18f),
            )
            startPositions[code] = center
            if (code !in placedCodes) positions[code] = center
        }
    }

    LaunchedEffect(complete, paused) {
        while (!complete && !paused) {
            delay(1_000)
            elapsedSeconds += 1
        }
    }

    LaunchedEffect(complete) {
        if (complete) {
            onSound(SoundCue.SUCCESS)
            onSpeak("¡Rompecabezas completado, ${profile.alias.ifBlank { "explorador" }}! Ubicaste las treinta y dos entidades de México.")
        }
    }

    LaunchedEffect(currentBatchNumber) {
        if (currentBatchNumber > 1 && !complete) {
            onSound(SoundCue.SUCCESS)
            onSpeak("¡Muy bien, ${profile.alias.ifBlank { "explorador" }}! Ahora continúa con la tanda $currentBatchNumber de $totalBatches.")
        }
    }

    fun speakHint() {
        val code = activeCode ?: currentBatchCodes.firstOrNull { it !in placedCodes } ?: return
        val entity = MexicoContent.findByCode(code) ?: return
        onSound(SoundCue.TAP)
        onSpeak("Busca ${entity.learningName}. Recuerda: su capital es ${entity.capital}.")
    }

    fun speakInstructions() {
        onSound(SoundCue.TAP)
        onSpeak("Arrastra cada pieza luminosa hasta el lugar que le corresponde en el mapa de México.")
    }

    AventuBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onBack) {
                    Text("‹", style = MaterialTheme.typography.headlineMedium, color = CyanElectric)
                }
                Text(
                    "Rompecabezas de México",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                )
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardNavy)
                        .border(1.dp, CyanElectric.copy(alpha = 0.50f), CircleShape)
                        .clickable(onClick = ::speakInstructions),
                    contentAlignment = Alignment.Center,
                ) {
                    GearGlyph(Modifier.size(21.dp))
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(27.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, CyanElectric.copy(alpha = 0.36f)),
            ) {
                Box(Modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { canvasSize = it }
                            .pointerInput(canvasSize, placedCodes, paused, currentBatchCodes) {
                                if (paused) return@pointerInput
                                detectDragGestures(
                                    onDragStart = { touch ->
                                        val candidate = currentBatchCodes
                                            .filterNot { it in placedCodes }
                                            .minByOrNull { code -> positions[code]?.distanceSquared(touch) ?: Float.MAX_VALUE }
                                        if (candidate != null && (positions[candidate]?.distanceTo(touch) ?: Float.MAX_VALUE) <= grabRadius) {
                                            activeCode = candidate
                                            onSound(SoundCue.TAP)
                                            MexicoContent.findByCode(candidate)?.let { entity ->
                                                onSpeak("${entity.learningName}. Su capital es ${entity.capital}.")
                                            }
                                        }
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        activeCode?.let { code -> positions[code] = (positions[code] ?: change.position) + dragAmount }
                                    },
                                    onDragCancel = {
                                        activeCode?.let { code -> startPositions[code]?.let { positions[code] = it } }
                                        activeCode = null
                                    },
                                    onDragEnd = {
                                        val code = activeCode
                                        val shape = code?.let(shapesByCode::get)
                                        if (code != null && shape != null) {
                                            val target = targetCenter(shape, canvasSize)
                                            if ((positions[code]?.distanceTo(target) ?: Float.MAX_VALUE) <= snapRadius) {
                                                placedCodes = placedCodes + code
                                                lastPlacedCode = code
                                                positions[code] = target
                                                onSound(SoundCue.MATCH)
                                                val entity = MexicoContent.findByCode(code)
                                                onSpeak("${MotivationCoach.puzzle(profile.alias)} ${entity?.learningName} quedó en su lugar.")
                                            } else {
                                                startPositions[code]?.let { positions[code] = it }
                                                onSound(SoundCue.TRY_AGAIN)
                                            }
                                        }
                                        activeCode = null
                                    },
                                )
                            },
                    ) {
                        val metrics = boardMetrics(size.width, size.height)
                        drawRoundRect(
                            color = Color(0xFF082238),
                            topLeft = Offset(metrics.mapLeft - 8.dp.toPx(), metrics.mapTop - 8.dp.toPx()),
                            size = Size(metrics.mapWidth + 16.dp.toPx(), metrics.mapHeight + 16.dp.toPx()),
                            cornerRadius = CornerRadius(22.dp.toPx()),
                        )
                        drawRoundRect(
                            color = CardRaised.copy(alpha = 0.76f),
                            topLeft = Offset(metrics.sideLeft, 10.dp.toPx()),
                            size = Size(size.width - metrics.sideLeft - 10.dp.toPx(), size.height - 20.dp.toPx()),
                            cornerRadius = CornerRadius(22.dp.toPx()),
                        )
                        drawLine(
                            CyanElectric.copy(alpha = 0.30f),
                            Offset(metrics.sideLeft - 8.dp.toPx(), 22.dp.toPx()),
                            Offset(metrics.sideLeft - 8.dp.toPx(), size.height - 22.dp.toPx()),
                            1.dp.toPx(),
                        )
                        listOf(0.10f to 0.12f, 0.28f to 0.82f, 0.55f to 0.18f, 0.63f to 0.74f).forEach { star ->
                            drawCircle(SunYellow.copy(alpha = 0.48f), 1.8.dp.toPx(), Offset(metrics.mapWidth * star.first, size.height * star.second))
                        }

                        withTransform({ translate(metrics.mapLeft, metrics.mapTop) }) {
                            geometry.forEach { shape ->
                                val path = nationalPath(shape, metrics.mapWidth, metrics.mapHeight)
                                val fillColor = if (shape.code in placedCodes) mapColorForCode(shape.code) else Color(0xFF173A50)
                                drawPath(path, color = fillColor)
                                drawPath(path, color = IceWhite.copy(alpha = if (shape.code in placedCodes) 0.82f else 0.38f), style = Stroke(width = 1.dp.toPx()))
                                if (shape.code in placedCodes) {
                                    drawPath(path, color = IceWhite, style = Stroke(width = 1.6.dp.toPx()))
                                }
                            }
                        }

                        activeCode?.let { code ->
                            val pieceCenter = positions[code]
                            val shape = shapesByCode[code]
                            if (pieceCenter != null && shape != null) {
                                val target = targetCenter(shape, canvasSize)
                                val route = Path().apply {
                                    moveTo(pieceCenter.x, pieceCenter.y)
                                    cubicTo(
                                        pieceCenter.x - size.width * 0.10f,
                                        pieceCenter.y - size.height * 0.08f,
                                        target.x + size.width * 0.12f,
                                        target.y + size.height * 0.10f,
                                        target.x,
                                        target.y,
                                    )
                                }
                                drawPath(
                                    route,
                                    color = CyanElectric.copy(alpha = 0.72f),
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(9.dp.toPx(), 8.dp.toPx())),
                                    ),
                                )
                                drawCircle(CyanElectric.copy(alpha = 0.16f), 26.dp.toPx(), target)
                                drawCircle(CyanElectric.copy(alpha = 0.42f), 14.dp.toPx(), target, style = Stroke(width = 3.dp.toPx()))
                            }
                        }

                        lastPlacedCode?.let { code ->
                            shapesByCode[code]?.let { shape ->
                                val target = targetCenter(shape, canvasSize)
                                drawCircle(EmeraldGlow.copy(alpha = 0.13f), 28.dp.toPx(), target)
                                drawCircle(EmeraldGlow.copy(alpha = 0.34f), 18.dp.toPx(), target, style = Stroke(width = 3.dp.toPx()))
                            }
                        }

                        currentBatchCodes.filterNot { it in placedCodes }.forEach { code ->
                            val shape = shapesByCode[code] ?: return@forEach
                            val pieceCenter = positions[code] ?: return@forEach
                            val active = code == activeCode
                            val scale = if (active) 1.22f else 1f
                            val path = silhouettePath(shape, pieceCenter, Size(pieceWidth * scale, pieceHeight * scale))
                            drawPath(path, color = CyanElectric.copy(alpha = if (active) 0.36f else 0.14f), style = Stroke(width = if (active) 11.dp.toPx() else 7.dp.toPx()))
                            drawPath(path, color = mapColorForCode(code))
                            drawPath(path, color = IceWhite, style = Stroke(width = 1.8.dp.toPx()))
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .fillMaxHeight()
                            .fillMaxWidth(0.275f)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("◷  ${formatElapsed(elapsedSeconds)}", color = IceWhite, fontWeight = FontWeight.Black)
                        Text(
                            "${placedCodes.size} de ${puzzleCodes.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            "Tanda $currentBatchNumber de $totalBatches",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanElectric,
                            fontWeight = FontWeight.Bold,
                        )
                        LinearProgressIndicator(
                            progress = { placedCodes.size / puzzleCodes.size.toFloat() },
                            modifier = Modifier.fillMaxWidth().padding(top = 5.dp).height(6.dp),
                            color = CyanElectric,
                            trackColor = CardNavy,
                        )
                        Spacer(Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = ::speakHint, modifier = Modifier.weight(1f)) { Text("Pista") }
                            OutlinedButton(
                                onClick = {
                                    onSound(SoundCue.TAP)
                                    activeCode?.let(MexicoContent::findByCode)?.let { onSpeak(it.learningName) }
                                },
                                modifier = Modifier.weight(1f),
                            ) { Text("Sonido") }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(0.735f)
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                paused = !paused
                                activeCode = null
                            },
                            modifier = Modifier.width(176.dp).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (paused) EmeraldGlow else Color(0xFF073149)),
                            border = BorderStroke(1.dp, CyanElectric),
                        ) {
                            Text(if (paused) "▶  Continuar" else "Ⅱ  Pausa", fontWeight = FontWeight.Black)
                        }
                    }

                    if (paused) {
                        Card(
                            modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.42f),
                            colors = CardDefaults.cardColors(containerColor = AtlasNight.copy(alpha = 0.95f)),
                            border = BorderStroke(2.dp, CyanElectric),
                            shape = RoundedCornerShape(24.dp),
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(22.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CompassGuide(Modifier.size(58.dp))
                                Text("Aventura en pausa", style = MaterialTheme.typography.titleLarge)
                                Text("Cuando estés listo, toca Continuar.", textAlign = TextAlign.Center, color = IceWhite.copy(alpha = 0.68f))
                            }
                        }
                    }

                    if (complete) {
                        Card(
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 30.dp).fillMaxWidth(0.47f),
                            colors = CardDefaults.cardColors(containerColor = AtlasNight.copy(alpha = 0.96f)),
                            border = BorderStroke(2.dp, EmeraldGlow),
                            shape = RoundedCornerShape(25.dp),
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                CompassGuide(Modifier.size(62.dp))
                                Text("¡Mapa armado!", style = MaterialTheme.typography.titleLarge, color = EmeraldGlow)
                                Text("Las piezas encajaron sin deformar sus siluetas.", textAlign = TextAlign.Center)
                                Button(
                                    onClick = {
                                        if (!rewardClaimed) {
                                            onRoundFinished(puzzleCodes.size, puzzleCodes.size, 3)
                                            rewardClaimed = true
                                        }
                                        onBack()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Guardar progreso") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GearGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val color = CyanElectric
        val stroke = size.minDimension * 0.11f
        drawCircle(color, radius = size.minDimension * 0.27f, style = Stroke(stroke))
        drawCircle(color, radius = size.minDimension * 0.08f)
        val spokes = listOf(
            Offset(0.50f, 0.04f) to Offset(0.50f, 0.24f),
            Offset(0.50f, 0.76f) to Offset(0.50f, 0.96f),
            Offset(0.04f, 0.50f) to Offset(0.24f, 0.50f),
            Offset(0.76f, 0.50f) to Offset(0.96f, 0.50f),
            Offset(0.17f, 0.17f) to Offset(0.31f, 0.31f),
            Offset(0.69f, 0.69f) to Offset(0.83f, 0.83f),
            Offset(0.83f, 0.17f) to Offset(0.69f, 0.31f),
            Offset(0.31f, 0.69f) to Offset(0.17f, 0.83f),
        )
        spokes.forEach { (start, end) ->
            drawLine(
                color,
                Offset(size.width * start.x, size.height * start.y),
                Offset(size.width * end.x, size.height * end.y),
                stroke,
            )
        }
    }
}

private data class BoardMetrics(
    val mapLeft: Float,
    val mapTop: Float,
    val mapWidth: Float,
    val mapHeight: Float,
    val sideLeft: Float,
)

private fun boardMetrics(width: Float, height: Float): BoardMetrics {
    val mapLeft = width * 0.025f
    val mapWidth = width * 0.67f
    val mapHeight = minOf(mapWidth / 1.72f, height * 0.78f)
    val mapTop = (height - mapHeight) / 2f
    return BoardMetrics(mapLeft, mapTop, mapWidth, mapHeight, width * 0.735f)
}

private fun targetCenter(shape: MexicoStateShape, canvasSize: IntSize): Offset {
    val metrics = boardMetrics(canvasSize.width.toFloat(), canvasSize.height.toFloat())
    return Offset(
        metrics.mapLeft + shape.centroid.x * metrics.mapWidth,
        metrics.mapTop + shape.centroid.y * metrics.mapHeight,
    )
}

private fun formatElapsed(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Offset.distanceSquared(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}

private fun Offset.distanceTo(other: Offset): Float = sqrt(distanceSquared(other))
