package com.aventumapa.app.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aventumapa.app.R
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.theme.AtlasNight
import com.aventumapa.app.ui.theme.CardNavy
import com.aventumapa.app.ui.theme.CyanElectric
import com.aventumapa.app.ui.theme.EmeraldGlow
import com.aventumapa.app.ui.theme.IceWhite
import com.aventumapa.app.ui.theme.SunYellow
import com.aventumapa.app.ui.theme.VioletQuest
import com.aventumapa.app.ui.theme.WarmCoral
import com.aventumapa.core.model.ChildProfile
import com.aventumapa.core.model.NarratorVoice

@Composable
fun HomeScreen(
    profile: ChildProfile,
    onExplore: () -> Unit,
    onQuiz: () -> Unit,
    onMemory: () -> Unit,
    onPuzzle: () -> Unit,
    onParents: () -> Unit,
    onVoiceSelected: (NarratorVoice) -> Unit,
) {
    var showVoices by remember { mutableStateOf(false) }

    AventuBackground {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                DashboardHeader(profile = profile, onVoices = { showVoices = true })
                Text(
                    text = "Tu próxima aventura",
                    style = MaterialTheme.typography.titleLarge,
                    color = CyanElectric,
                    fontWeight = FontWeight.Black,
                )
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AdventureTile(
                            title = "Explora\nMéxico",
                            illustration = R.drawable.activity_explore,
                            accent = CyanElectric,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onExplore,
                        )
                        AdventureTile(
                            title = "Reto de\ncapitales",
                            illustration = R.drawable.activity_capitals,
                            accent = VioletQuest,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onQuiz,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AdventureTile(
                            title = "Entrena tu\nmemoria",
                            illustration = R.drawable.activity_memory,
                            accent = WarmCoral,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onMemory,
                        )
                        AdventureTile(
                            title = "Rompecabezas",
                            illustration = R.drawable.activity_puzzle,
                            accent = SunYellow,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onPuzzle,
                        )
                    }
                }
            }

            DashboardBottomBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                onExplore = onExplore,
                onParents = onParents,
            )
        }
    }

    if (showVoices) {
        Dialog(onDismissRequest = { showVoices = false }) {
            VoicePickerDialog(
                selected = profile.narratorVoice,
                alias = profile.alias,
                onSelected = {
                    onVoiceSelected(it)
                    showVoices = false
                },
                onDismiss = { showVoices = false },
            )
        }
    }
}

@Composable
private fun DashboardHeader(profile: ChildProfile, onVoices: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(CircleShape)
                .background(CyanElectric.copy(alpha = 0.12f))
                .border(2.dp, CyanElectric, CircleShape)
                .clickable(onClick = onVoices),
        ) {
            VoicePortrait(profile.narratorVoice, Modifier.fillMaxSize())
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Hola, ${profile.alias.ifBlank { "Explorador" }}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = IceWhite,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                RoundIconButton(onClick = onVoices) {
                    SpeakerGlyph(Modifier.size(21.dp), CyanElectric)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill("★", "${profile.experience} XP", SunYellow, Modifier.weight(1f))
                StatPill("◆", "Racha de ${profile.streakDays} días", WarmCoral, Modifier.weight(1.25f))
            }
        }
    }
}

@Composable
private fun StatPill(
    symbol: String,
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.34f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(symbol, color = accent, fontWeight = FontWeight.Black)
            Text(text, color = IceWhite, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

@Composable
private fun AdventureTile(
    title: String,
    @DrawableRes illustration: Int,
    accent: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.2.dp, accent.copy(alpha = 0.78f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.27f), accent.copy(alpha = 0.06f), Color.Transparent),
                    ),
                )
                .padding(horizontal = 9.dp, vertical = 8.dp),
        ) {
            Image(
                painter = painterResource(illustration),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.84f)
                    .fillMaxHeight(0.66f),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = title,
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = IceWhite,
            )
        }
    }
}

private data class VoiceOption(
    val voice: NarratorVoice,
    val accent: Color,
    @param:DrawableRes val portrait: Int,
)

@Composable
private fun VoicePickerDialog(
    selected: NarratorVoice,
    alias: String,
    onSelected: (NarratorVoice) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = rememberVoiceOptions()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AtlasNight),
        border = BorderStroke(1.5.dp, CyanElectric.copy(alpha = 0.58f)),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Elige tu voz", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Te acompañará en español latino, ${alias.ifBlank { "explorador" }}.", color = IceWhite.copy(alpha = 0.66f))
                }
                TextButton(onClick = onDismiss) { Text("Cerrar") }
            }
            options.chunked(2).forEach { rowOptions ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowOptions.forEach { option ->
                        Card(
                            onClick = { onSelected(option.voice) },
                            modifier = Modifier.weight(1f).height(140.dp),
                            shape = RoundedCornerShape(19.dp),
                            border = BorderStroke(
                                if (option.voice == selected) 2.dp else 1.dp,
                                if (option.voice == selected) option.accent else option.accent.copy(alpha = 0.28f),
                            ),
                            colors = CardDefaults.cardColors(containerColor = option.accent.copy(alpha = 0.10f)),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Image(
                                    painter = painterResource(option.portrait),
                                    contentDescription = option.voice.characterName,
                                    modifier = Modifier.size(82.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                                Text(option.voice.characterName, color = IceWhite, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                                Text(
                                    if (option.voice == selected) "VOZ ACTIVA" else option.voice.roleLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = option.accent,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoicePortrait(voice: NarratorVoice, modifier: Modifier = Modifier) {
    val option = rememberVoiceOptions().first { it.voice == voice }
    Image(
        painter = painterResource(option.portrait),
        contentDescription = option.voice.characterName,
        modifier = modifier.clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun rememberVoiceOptions(): List<VoiceOption> = remember {
    listOf(
        VoiceOption(NarratorVoice.BOY, CyanElectric, R.drawable.voice_matein_pompin),
        VoiceOption(NarratorVoice.GIRL, VioletQuest, R.drawable.voice_andreita),
        VoiceOption(NarratorVoice.ELEGANT_MAN, SunYellow, R.drawable.voice_maximo),
        VoiceOption(NarratorVoice.FRIENDLY_WOMAN, EmeraldGlow, R.drawable.voice_claudis),
    )
}

@Composable
private fun RoundIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(CardNavy)
            .border(1.dp, CyanElectric.copy(alpha = 0.62f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun SpeakerGlyph(modifier: Modifier = Modifier, color: Color = IceWhite) {
    Canvas(modifier) {
        val speaker = Path().apply {
            moveTo(size.width * 0.13f, size.height * 0.40f)
            lineTo(size.width * 0.34f, size.height * 0.40f)
            lineTo(size.width * 0.55f, size.height * 0.22f)
            lineTo(size.width * 0.55f, size.height * 0.78f)
            lineTo(size.width * 0.34f, size.height * 0.60f)
            lineTo(size.width * 0.13f, size.height * 0.60f)
            close()
        }
        drawPath(speaker, color)
        drawArc(color, -48f, 96f, false, Offset(size.width * 0.40f, size.height * 0.27f), Size(size.width * 0.36f, size.height * 0.46f), style = Stroke(size.width * 0.08f))
        drawArc(color, -48f, 96f, false, Offset(size.width * 0.34f, size.height * 0.14f), Size(size.width * 0.58f, size.height * 0.72f), style = Stroke(size.width * 0.07f))
    }
}

private enum class DashboardNavKind { HOME, COMPASS, TROPHY, PERSON }

@Composable
private fun DashboardBottomBar(
    modifier: Modifier = Modifier,
    onExplore: () -> Unit,
    onParents: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 7.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy.copy(alpha = 0.98f)),
        shape = RoundedCornerShape(23.dp),
        border = BorderStroke(1.dp, CyanElectric.copy(alpha = 0.30f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            DashboardNavItem("Inicio", DashboardNavKind.HOME, active = true, onClick = {})
            DashboardNavItem("Viaje", DashboardNavKind.COMPASS, onClick = onExplore)
            DashboardNavItem("Logros", DashboardNavKind.TROPHY, onClick = onParents)
            DashboardNavItem("Perfil", DashboardNavKind.PERSON, onClick = onParents)
        }
    }
}

@Composable
private fun DashboardNavItem(
    label: String,
    kind: DashboardNavKind,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    val color = if (active) CyanElectric else IceWhite.copy(alpha = 0.58f)
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 42.dp, height = 31.dp)
                    .background(if (active) CyanElectric.copy(alpha = 0.16f) else Color.Transparent, RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center,
            ) {
                DashboardNavGlyph(kind, color, Modifier.size(23.dp))
            }
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = if (active) FontWeight.Black else FontWeight.Medium)
        }
    }
}

@Composable
private fun DashboardNavGlyph(kind: DashboardNavKind, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = size.minDimension * 0.09f
        when (kind) {
            DashboardNavKind.HOME -> {
                val roof = Path().apply {
                    moveTo(size.width * 0.10f, size.height * 0.48f)
                    lineTo(size.width * 0.50f, size.height * 0.12f)
                    lineTo(size.width * 0.90f, size.height * 0.48f)
                }
                drawPath(roof, color, style = Stroke(stroke))
                drawRoundRect(color, Offset(size.width * 0.22f, size.height * 0.43f), Size(size.width * 0.56f, size.height * 0.45f), cornerRadius = CornerRadius(stroke))
                drawRect(CardNavy, Offset(size.width * 0.44f, size.height * 0.63f), Size(size.width * 0.13f, size.height * 0.25f))
            }
            DashboardNavKind.COMPASS -> {
                drawCircle(color, radius = size.minDimension * 0.40f, style = Stroke(stroke))
                val needle = Path().apply {
                    moveTo(size.width * 0.67f, size.height * 0.25f)
                    lineTo(size.width * 0.53f, size.height * 0.57f)
                    lineTo(size.width * 0.31f, size.height * 0.76f)
                    lineTo(size.width * 0.47f, size.height * 0.43f)
                    close()
                }
                drawPath(needle, color)
            }
            DashboardNavKind.TROPHY -> {
                drawArc(color, 0f, 180f, false, Offset(size.width * 0.25f, size.height * 0.14f), Size(size.width * 0.50f, size.height * 0.48f), style = Stroke(stroke))
                drawLine(color, Offset(size.width * 0.50f, size.height * 0.57f), Offset(size.width * 0.50f, size.height * 0.79f), stroke)
                drawLine(color, Offset(size.width * 0.32f, size.height * 0.84f), Offset(size.width * 0.68f, size.height * 0.84f), stroke)
                drawArc(color, 86f, 150f, false, Offset(size.width * 0.06f, size.height * 0.21f), Size(size.width * 0.34f, size.height * 0.31f), style = Stroke(stroke))
                drawArc(color, -56f, 150f, false, Offset(size.width * 0.60f, size.height * 0.21f), Size(size.width * 0.34f, size.height * 0.31f), style = Stroke(stroke))
            }
            DashboardNavKind.PERSON -> {
                drawCircle(color, radius = size.minDimension * 0.17f, center = Offset(size.width * 0.50f, size.height * 0.30f), style = Stroke(stroke))
                drawArc(color, 195f, 150f, false, Offset(size.width * 0.18f, size.height * 0.48f), Size(size.width * 0.64f, size.height * 0.43f), style = Stroke(stroke))
            }
        }
    }
}
