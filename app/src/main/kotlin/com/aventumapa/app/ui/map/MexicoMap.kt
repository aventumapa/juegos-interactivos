package com.aventumapa.app.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.aventumapa.app.ui.theme.CardNavy
import com.aventumapa.app.ui.theme.CyanElectric
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.IceWhite
import com.aventumapa.app.ui.theme.NightBlue
import com.aventumapa.app.ui.theme.SunYellow
import com.aventumapa.app.ui.theme.WarmCoral
import com.aventumapa.content.mexico.MexicoContent

private val mapPalette = listOf(
    Color(0xFF5CC9C2),
    Color(0xFFFFB85C),
    Color(0xFFFF8C78),
    Color(0xFF82C78F),
    Color(0xFF8CAFF2),
    Color(0xFFC79BE8),
    Color(0xFFF2D66E),
)

fun mapColorForCode(code: String): Color {
    val region = MexicoContent.findByCode(code)?.educationalRegion.orEmpty()
    val index = when (region) {
        "Noroeste" -> 4
        "Noreste" -> 0
        "Occidente" -> 2
        "Centro" -> 1
        "Oriente" -> 5
        "Sur" -> 3
        else -> 6
    }
    return mapPalette[index]
}

@Composable
fun InteractiveMexicoMap(
    states: List<MexicoStateShape>,
    selectedCode: String?,
    onStateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val expandedTouchRadius = with(density) { 32.dp.toPx() }
    val paths = remember(states, canvasSize) {
        states.associate { state -> state.code to nationalPath(state, canvasSize.width.toFloat(), canvasSize.height.toFloat()) }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.72f)
            .clip(RoundedCornerShape(28.dp))
            .background(CardNavy)
            .onSizeChanged { canvasSize = it }
            .pointerInput(states, canvasSize) {
                detectTapGestures { tap ->
                    if (size.width == 0 || size.height == 0) return@detectTapGestures
                    val normalized = MapPoint(tap.x / size.width, tap.y / size.height)
                    val directHit = states.lastOrNull { state ->
                        state.polygons.any { polygonContains(normalized, it) }
                    }
                    val nearest = states.minByOrNull { state ->
                        val dx = tap.x - state.centroid.x * size.width
                        val dy = tap.y - state.centroid.y * size.height
                        dx * dx + dy * dy
                    }
                    val nearestDistance = nearest?.let { state ->
                        val dx = tap.x - state.centroid.x * size.width
                        val dy = tap.y - state.centroid.y * size.height
                        kotlin.math.sqrt(dx * dx + dy * dy)
                    } ?: Float.MAX_VALUE
                    val isSmallTarget = nearest?.let { MexicoContent.findByCode(it.code)?.smallEntityCandidate == true } == true
                    when {
                        directHit != null -> onStateSelected(directHit.code)
                        nearest != null && isSmallTarget && nearestDistance <= expandedTouchRadius -> onStateSelected(nearest.code)
                    }
                }
            },
    ) {
        drawCircle(
            color = CyanElectric.copy(alpha = 0.08f),
            radius = size.minDimension * 0.34f,
            center = Offset(size.width * 0.20f, size.height * 0.16f),
        )
        drawCircle(
            color = SunYellow.copy(alpha = 0.08f),
            radius = size.minDimension * 0.25f,
            center = Offset(size.width * 0.86f, size.height * 0.76f),
        )

        states.forEach { state ->
            val path = paths[state.code] ?: return@forEach
            val selected = state.code == selectedCode
            val shadowOffset = 3.dp.toPx()
            withTransform({ translate(left = 0f, top = shadowOffset) }) {
                drawPath(path, color = NightBlue.copy(alpha = 0.14f))
            }
            if (selected) {
                drawPath(path, color = CyanElectric.copy(alpha = 0.14f), style = Stroke(width = 20.dp.toPx()))
                drawPath(path, color = CyanElectric.copy(alpha = 0.42f), style = Stroke(width = 11.dp.toPx()))
            }
            drawPath(path, color = if (selected) CyanElectric else mapColorForCode(state.code))
            drawPath(
                path,
                color = if (selected) IceWhite else IceWhite.copy(alpha = 0.88f),
                style = Stroke(width = if (selected) 3.dp.toPx() else 1.1.dp.toPx()),
            )
        }
    }
}

@Composable
fun StateSilhouette(
    shape: MexicoStateShape,
    modifier: Modifier = Modifier,
    color: Color = mapColorForCode(shape.code),
    selected: Boolean = false,
) {
    Canvas(modifier = modifier) {
        val polygon = shape.polygons.firstOrNull() ?: return@Canvas
        val left = polygon.minOf { it.x }
        val right = polygon.maxOf { it.x }
        val top = polygon.minOf { it.y }
        val bottom = polygon.maxOf { it.y }
        val width = (right - left).coerceAtLeast(0.0001f)
        val height = (bottom - top).coerceAtLeast(0.0001f)
        val scale = minOf(size.width * 0.78f / width, size.height * 0.78f / height)
        val originX = (size.width - width * scale) / 2f
        val originY = (size.height - height * scale) / 2f
        val path = Path().apply {
            polygon.forEachIndexed { index, point ->
                val x = originX + (point.x - left) * scale
                val y = originY + (point.y - top) * scale
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path, color = NightBlue.copy(alpha = 0.14f), style = Stroke(width = 6.dp.toPx()))
        drawPath(path, color = color)
        drawPath(
            path,
            color = if (selected) ExplorerTeal else Color.White,
            style = Stroke(width = if (selected) 3.dp.toPx() else 1.5.dp.toPx()),
        )
        if (selected) {
            drawCircle(WarmCoral, radius = 4.dp.toPx(), center = Offset(size.width * 0.82f, size.height * 0.18f))
        }
    }
}

fun nationalPath(shape: MexicoStateShape, width: Float, height: Float): Path = Path().apply {
    shape.polygons.forEach { polygon ->
        polygon.forEachIndexed { index, point ->
            val x = point.x * width
            val y = point.y * height
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
}

fun silhouettePath(shape: MexicoStateShape, center: Offset, boxSize: Size): Path {
    val polygon = shape.polygons.firstOrNull() ?: return Path()
    val left = polygon.minOf { it.x }
    val right = polygon.maxOf { it.x }
    val top = polygon.minOf { it.y }
    val bottom = polygon.maxOf { it.y }
    val width = (right - left).coerceAtLeast(0.0001f)
    val height = (bottom - top).coerceAtLeast(0.0001f)
    val scale = minOf(boxSize.width / width, boxSize.height / height)
    return Path().apply {
        polygon.forEachIndexed { index, point ->
            val x = center.x + (point.x - (left + right) / 2f) * scale
            val y = center.y + (point.y - (top + bottom) / 2f) * scale
            if (index == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
}

private fun polygonContains(point: MapPoint, polygon: List<MapPoint>): Boolean {
    var inside = false
    var previous = polygon.lastIndex
    polygon.indices.forEach { current ->
        val currentPoint = polygon[current]
        val previousPoint = polygon[previous]
        val crosses = (currentPoint.y > point.y) != (previousPoint.y > point.y) &&
            point.x < (previousPoint.x - currentPoint.x) * (point.y - currentPoint.y) /
            (previousPoint.y - currentPoint.y) + currentPoint.x
        if (crosses) inside = !inside
        previous = current
    }
    return inside
}
