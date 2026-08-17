package com.aventumapa.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.audio.SoundCue
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.InfoBanner
import com.aventumapa.app.ui.components.SectionHeader
import com.aventumapa.app.ui.map.MexicoStateShape
import com.aventumapa.app.ui.map.StateSilhouette
import com.aventumapa.app.ui.map.rememberMexicoMapGeometry
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.NightBlue
import com.aventumapa.app.ui.theme.PaleTeal
import com.aventumapa.app.ui.theme.SuccessGreen
import com.aventumapa.content.mexico.MexicoContent
import com.aventumapa.core.model.ChildProfile
import com.aventumapa.core.model.MemoryCard
import com.aventumapa.core.model.MemoryCardKind
import com.aventumapa.gameengine.MemoryDeckFactory
import com.aventumapa.gameengine.MotivationCoach
import kotlinx.coroutines.delay

@Composable
fun MemoryScreen(
    profile: ChildProfile,
    onBack: () -> Unit,
    onRoundFinished: (correct: Int, total: Int, stars: Int) -> Unit,
    onSpeak: (String) -> Unit,
    onSound: (SoundCue) -> Unit,
) {
    val deck = remember {
        MemoryDeckFactory.entityCapitalDeck(
            MexicoContent.entities,
            pairCount = 6,
            seed = System.currentTimeMillis(),
        )
    }
    val shapes = rememberMexicoMapGeometry().associateBy { it.code }
    var visibleIds by remember { mutableStateOf(setOf<String>()) }
    var matchedPairs by remember { mutableStateOf(setOf<String>()) }
    var attempts by remember { mutableIntStateOf(0) }
    var rewardClaimed by remember { mutableStateOf(false) }
    val complete = matchedPairs.size == deck.size / 2

    LaunchedEffect(visibleIds) {
        if (visibleIds.size == 2) {
            val cards = deck.filter { it.id in visibleIds }
            if (cards.map { it.pairId }.distinct().size == 1) {
                delay(360)
                onSound(SoundCue.MATCH)
                onSpeak(MotivationCoach.match(profile.alias))
                matchedPairs = matchedPairs + cards.first().pairId
                visibleIds = emptySet()
            } else {
                delay(780)
                onSound(SoundCue.TRY_AGAIN)
                visibleIds = emptySet()
            }
        }
    }

    LaunchedEffect(complete) {
        if (complete) {
            onSound(SoundCue.SUCCESS)
            onSpeak("¡Memoria completada, ${profile.alias.ifBlank { "explorador" }}! Uniste todos los estados con sus capitales.")
        }
    }

    AventuBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
            SectionHeader(
                title = stringResource(R.string.memory_title),
                subtitle = "Voltea tarjetas, escucha sus nombres y encuentra seis parejas.",
            )
            Text("Intentos: $attempts · Parejas: ${matchedPairs.size} de 6", style = MaterialTheme.typography.labelLarge)

            LazyVerticalGrid(
                columns = GridCells.Adaptive(112.dp),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(deck, key = { it.id }) { card ->
                    val faceUp = card.id in visibleIds || card.pairId in matchedPairs
                    MemoryCardView(
                        card = card,
                        shape = shapes[card.pairId],
                        faceUp = faceUp,
                        matched = card.pairId in matchedPairs,
                        enabled = visibleIds.size < 2 && card.pairId !in matchedPairs && card.id !in visibleIds,
                        onClick = {
                            onSound(SoundCue.TAP)
                            onSpeak(
                                if (card.kind == MemoryCardKind.ENTITY) {
                                    "Estado: ${card.label}."
                                } else {
                                    "Capital: ${card.label}."
                                },
                            )
                            visibleIds = visibleIds + card.id
                            if (visibleIds.size == 1) attempts += 1
                        },
                    )
                }
            }

            if (complete) {
                InfoBanner("¡Memoria brillante! Cada tarjeta tuvo movimiento, sonido y pronunciación.")
                Button(
                    onClick = {
                        if (!rewardClaimed) {
                            onRoundFinished(6, 6, if (attempts <= 10) 3 else 2)
                            rewardClaimed = true
                        }
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text("Guardar progreso")
                }
            }
        }
    }
}

@Composable
private fun MemoryCardView(
    card: MemoryCard,
    shape: MexicoStateShape?,
    faceUp: Boolean,
    matched: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (faceUp) 180f else 0f,
        animationSpec = tween(440, easing = FastOutSlowInEasing),
        label = "cardFlip",
    )
    val scale by animateFloatAsState(
        targetValue = if (matched) 1.045f else 1f,
        animationSpec = tween(280),
        label = "matchScale",
    )
    val showFront = rotation > 90f

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(142.dp)
            .graphicsLayer {
                rotationY = rotation
                scaleX = scale
                scaleY = scale
                cameraDistance = 14f * density
            },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (faceUp) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                matched -> SuccessGreen.copy(alpha = 0.22f)
                showFront -> PaleTeal
                else -> ExplorerTeal
            },
            disabledContainerColor = if (matched) SuccessGreen.copy(alpha = 0.22f) else PaleTeal,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .graphicsLayer { if (showFront) rotationY = 180f },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showFront) {
                if (shape != null) StateSilhouette(shape, Modifier.size(62.dp), selected = matched)
                Text(
                    card.label,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    color = NightBlue,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    if (card.kind == MemoryCardKind.ENTITY) "ESTADO" else "CAPITAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = NightBlue.copy(alpha = 0.62f),
                )
            } else {
                Text("A", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimary)
                Text("AVENTUMAPA", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
