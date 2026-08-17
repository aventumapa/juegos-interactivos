package com.aventumapa.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aventumapa.app.ui.theme.AtlasNight
import com.aventumapa.app.ui.theme.CardNavy
import com.aventumapa.app.ui.theme.CardRaised
import com.aventumapa.app.ui.theme.CyanElectric
import com.aventumapa.app.ui.theme.EmeraldGlow
import com.aventumapa.app.ui.theme.IceWhite
import com.aventumapa.app.ui.theme.SunYellow
import com.aventumapa.app.ui.theme.WarmCoral

@Composable
fun AventuBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AtlasNight, Color(0xFF071C2B), Color(0xFF082238)),
                ),
            ),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = CyanElectric.copy(alpha = 0.08f),
                radius = size.minDimension * 0.48f,
                center = Offset(size.width * 1.02f, size.height * 0.04f),
            )
            drawCircle(
                color = SunYellow.copy(alpha = 0.055f),
                radius = size.minDimension * 0.34f,
                center = Offset(-size.width * 0.04f, size.height * 0.90f),
            )
            val stars = listOf(
                0.08f to 0.17f, 0.23f to 0.09f, 0.71f to 0.14f, 0.90f to 0.28f,
                0.14f to 0.54f, 0.82f to 0.61f, 0.35f to 0.76f, 0.93f to 0.88f,
            )
            stars.forEachIndexed { index, point ->
                drawCircle(
                    color = if (index % 3 == 0) SunYellow.copy(alpha = 0.34f) else CyanElectric.copy(alpha = 0.28f),
                    radius = if (index % 3 == 0) 2.2.dp.toPx() else 1.3.dp.toPx(),
                    center = Offset(size.width * point.first, size.height * point.second),
                )
            }
        }
        content()
    }
}

@Composable
fun AventuLogo(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier = modifier.semantics { contentDescription = "AventuMapa" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 9.dp else 13.dp),
    ) {
        AventuLogoMark(Modifier.size(if (compact) 42.dp else 58.dp))
        Text(
            text = "AventuMapa",
            style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = IceWhite,
        )
    }
}

@Composable
fun AventuLogoMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawCircle(CyanElectric.copy(alpha = 0.15f), radius = w * 0.49f, center = center)
        drawLine(CyanElectric, Offset(w * 0.16f, h * 0.84f), Offset(w * 0.47f, h * 0.15f), w * 0.17f, StrokeCap.Round)
        drawLine(EmeraldGlow, Offset(w * 0.47f, h * 0.15f), Offset(w * 0.84f, h * 0.84f), w * 0.17f, StrokeCap.Round)
        drawLine(IceWhite, Offset(w * 0.32f, h * 0.61f), Offset(w * 0.68f, h * 0.61f), w * 0.10f, StrokeCap.Round)
        val route = Path().apply {
            moveTo(w * 0.22f, h * 0.76f)
            cubicTo(w * 0.40f, h * 0.90f, w * 0.62f, h * 0.72f, w * 0.77f, h * 0.48f)
        }
        drawPath(
            route,
            SunYellow,
            style = Stroke(width = w * 0.045f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.09f, w * 0.07f))),
        )
        val star = Path().apply {
            val cx = w * 0.80f
            val cy = h * 0.38f
            moveTo(cx, cy - w * 0.10f)
            lineTo(cx + w * 0.035f, cy - w * 0.03f)
            lineTo(cx + w * 0.11f, cy - w * 0.02f)
            lineTo(cx + w * 0.05f, cy + w * 0.03f)
            lineTo(cx + w * 0.07f, cy + w * 0.11f)
            lineTo(cx, cy + w * 0.06f)
            lineTo(cx - w * 0.07f, cy + w * 0.11f)
            lineTo(cx - w * 0.05f, cy + w * 0.03f)
            lineTo(cx - w * 0.11f, cy - w * 0.02f)
            lineTo(cx - w * 0.035f, cy - w * 0.03f)
            close()
        }
        drawPath(star, SunYellow)
    }
}

@Composable
fun CompassGuide(
    modifier: Modifier = Modifier,
    mood: GuideMood = GuideMood.HAPPY,
) {
    Canvas(modifier.semantics { contentDescription = "Explorín, guía de AventuMapa" }) {
        val radius = size.minDimension * 0.42f
        drawCircle(CyanElectric.copy(alpha = 0.16f), radius * 1.18f, center)
        drawCircle(CardRaised, radius, center)
        drawCircle(CyanElectric, radius, center, style = Stroke(width = size.minDimension * 0.055f))
        val north = Path().apply {
            moveTo(center.x, center.y - radius * 1.02f)
            lineTo(center.x + radius * 0.25f, center.y - radius * 0.42f)
            lineTo(center.x, center.y - radius * 0.16f)
            lineTo(center.x - radius * 0.25f, center.y - radius * 0.42f)
            close()
        }
        drawPath(north, SunYellow)
        drawCircle(CyanElectric.copy(alpha = 0.94f), radius * 0.67f, center)
        val eyeY = center.y - radius * 0.05f
        drawCircle(AtlasNight, radius * 0.085f, Offset(center.x - radius * 0.24f, eyeY))
        drawCircle(AtlasNight, radius * 0.085f, Offset(center.x + radius * 0.24f, eyeY))
        drawCircle(IceWhite, radius * 0.025f, Offset(center.x - radius * 0.22f, eyeY - radius * 0.025f))
        drawCircle(IceWhite, radius * 0.025f, Offset(center.x + radius * 0.26f, eyeY - radius * 0.025f))
        if (mood == GuideMood.HAPPY) {
            drawArc(
                color = AtlasNight,
                startAngle = 18f,
                sweepAngle = 144f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.18f, center.y + radius * 0.08f),
                size = Size(radius * 0.36f, radius * 0.22f),
                style = Stroke(width = radius * 0.06f, cap = StrokeCap.Round),
            )
        } else {
            drawLine(
                AtlasNight,
                Offset(center.x - radius * 0.14f, center.y + radius * 0.23f),
                Offset(center.x + radius * 0.14f, center.y + radius * 0.23f),
                radius * 0.06f,
                StrokeCap.Round,
            )
        }
    }
}

enum class GuideMood { HAPPY, THINKING }

enum class ActivityGlyphKind { EXPLORE, QUIZ, MEMORY, PUZZLE }

@Composable
fun ActivityGlyph(
    kind: ActivityGlyphKind,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        drawCircle(accent.copy(alpha = 0.15f), size.minDimension * 0.48f, center)
        when (kind) {
            ActivityGlyphKind.EXPLORE -> {
                drawRoundRect(accent, Offset(size.width * 0.20f, size.height * 0.28f), Size(size.width * 0.60f, size.height * 0.44f), CornerRadius(7.dp.toPx()), style = Stroke(width = 3.dp.toPx()))
                drawLine(accent, Offset(size.width * 0.40f, size.height * 0.30f), Offset(size.width * 0.40f, size.height * 0.70f), 2.dp.toPx())
                drawLine(accent, Offset(size.width * 0.61f, size.height * 0.30f), Offset(size.width * 0.61f, size.height * 0.70f), 2.dp.toPx())
                drawCircle(SunYellow, size.width * 0.07f, Offset(size.width * 0.58f, size.height * 0.46f))
            }
            ActivityGlyphKind.QUIZ -> {
                drawCircle(accent, size.minDimension * 0.27f, center, style = Stroke(width = 4.dp.toPx()))
                drawCircle(SunYellow, size.minDimension * 0.07f, Offset(center.x, size.height * 0.64f))
                drawLine(accent, Offset(center.x, size.height * 0.30f), Offset(center.x + size.width * 0.10f, size.height * 0.42f), 4.dp.toPx(), StrokeCap.Round)
                drawLine(accent, Offset(center.x + size.width * 0.10f, size.height * 0.42f), Offset(center.x, size.height * 0.54f), 4.dp.toPx(), StrokeCap.Round)
            }
            ActivityGlyphKind.MEMORY -> {
                drawRoundRect(accent, Offset(size.width * 0.20f, size.height * 0.23f), Size(size.width * 0.37f, size.height * 0.50f), CornerRadius(7.dp.toPx()))
                drawRoundRect(SunYellow, Offset(size.width * 0.44f, size.height * 0.29f), Size(size.width * 0.37f, size.height * 0.50f), CornerRadius(7.dp.toPx()))
                drawCircle(CardNavy, size.width * 0.05f, Offset(size.width * 0.62f, size.height * 0.54f))
            }
            ActivityGlyphKind.PUZZLE -> {
                val piece = size.width * 0.25f
                listOf(0f to 0f, 1f to 0f, 0f to 1f, 1f to 1f).forEachIndexed { index, point ->
                    drawRoundRect(
                        if (index % 2 == 0) accent else SunYellow,
                        Offset(size.width * 0.25f + piece * point.first, size.height * 0.25f + piece * point.second),
                        Size(piece * 0.92f, piece * 0.92f),
                        CornerRadius(5.dp.toPx()),
                    )
                }
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier
            .background(accent.copy(alpha = 0.13f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(9.dp).background(accent, CircleShape))
        Text(value, fontWeight = FontWeight.ExtraBold, color = IceWhite)
        Text(label, style = MaterialTheme.typography.labelLarge, color = IceWhite.copy(alpha = 0.72f))
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = IceWhite)
        if (subtitle != null) {
            Spacer(Modifier.height(5.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = IceWhite.copy(alpha = 0.68f))
        }
    }
}

@Composable
fun InfoBanner(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardRaised),
        border = BorderStroke(1.dp, CyanElectric.copy(alpha = 0.34f)),
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompassGuide(Modifier.size(44.dp), GuideMood.THINKING)
            Text(text, modifier = Modifier.weight(1f), color = IceWhite, fontWeight = FontWeight.SemiBold)
        }
    }
}
