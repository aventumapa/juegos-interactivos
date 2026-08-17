package com.aventumapa.app.ui.components

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.NightBlue
import com.aventumapa.app.ui.theme.PaleTeal
import com.aventumapa.app.ui.theme.PaperCream
import com.aventumapa.app.ui.theme.SunYellow
import com.aventumapa.app.ui.theme.WarmCoral

@Composable
fun AventuBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface),
                ),
            ),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = ExplorerTeal.copy(alpha = 0.07f),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.95f, size.height * 0.08f),
            )
            drawCircle(
                color = SunYellow.copy(alpha = 0.08f),
                radius = size.minDimension * 0.30f,
                center = Offset(size.width * 0.05f, size.height * 0.86f),
            )
        }
        content()
    }
}

@Composable
fun CompassGuide(
    modifier: Modifier = Modifier,
    mood: GuideMood = GuideMood.HAPPY,
) {
    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Guía brújula de AventuMapa"
        },
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.43f
        drawCircle(ExplorerTeal, radius, center)
        drawCircle(PaperCream, radius * 0.76f, center)
        drawCircle(NightBlue, radius, center, style = Stroke(width = size.minDimension * 0.035f))

        val north = Path().apply {
            moveTo(center.x, center.y - radius * 0.65f)
            lineTo(center.x + radius * 0.25f, center.y + radius * 0.12f)
            lineTo(center.x, center.y)
            lineTo(center.x - radius * 0.25f, center.y + radius * 0.12f)
            close()
        }
        drawPath(north, WarmCoral)

        val south = Path().apply {
            moveTo(center.x, center.y + radius * 0.65f)
            lineTo(center.x + radius * 0.25f, center.y - radius * 0.12f)
            lineTo(center.x, center.y)
            lineTo(center.x - radius * 0.25f, center.y - radius * 0.12f)
            close()
        }
        drawPath(south, NightBlue)

        val eyeY = center.y + radius * 0.34f
        drawCircle(NightBlue, radius * 0.055f, Offset(center.x - radius * 0.24f, eyeY))
        drawCircle(NightBlue, radius * 0.055f, Offset(center.x + radius * 0.24f, eyeY))

        val smileY = center.y + radius * 0.48f
        if (mood == GuideMood.HAPPY) {
            drawArc(
                color = NightBlue,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.17f, smileY - radius * 0.08f),
                size = androidx.compose.ui.geometry.Size(radius * 0.34f, radius * 0.20f),
                style = Stroke(width = radius * 0.055f, cap = StrokeCap.Round),
            )
        } else {
            drawLine(
                color = NightBlue,
                start = Offset(center.x - radius * 0.12f, smileY),
                end = Offset(center.x + radius * 0.12f, smileY),
                strokeWidth = radius * 0.055f,
                cap = StrokeCap.Round,
            )
        }
    }
}

enum class GuideMood { HAPPY, THINKING }

@Composable
fun StatPill(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier
            .background(accent.copy(alpha = 0.16f), CircleShape)
            .padding(horizontal = 13.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(9.dp).background(accent, CircleShape))
        Text(value, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        if (subtitle != null) {
            Spacer(Modifier.height(5.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f))
        }
    }
}

@Composable
fun InfoBanner(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PaleTeal),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompassGuide(Modifier.size(42.dp), GuideMood.THINKING)
            Text(text, modifier = Modifier.weight(1f), color = NightBlue, fontWeight = FontWeight.SemiBold)
        }
    }
}

