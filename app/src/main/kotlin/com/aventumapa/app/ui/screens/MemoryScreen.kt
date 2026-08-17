package com.aventumapa.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.InfoBanner
import com.aventumapa.app.ui.components.SectionHeader
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.NightBlue
import com.aventumapa.app.ui.theme.PaleTeal
import com.aventumapa.app.ui.theme.SuccessGreen
import com.aventumapa.content.mexico.MexicoContent
import com.aventumapa.core.model.MemoryCard
import com.aventumapa.gameengine.MemoryDeckFactory
import kotlinx.coroutines.delay

@Composable
fun MemoryScreen(
    onBack: () -> Unit,
    onRoundFinished: (correct: Int, total: Int, stars: Int) -> Unit,
) {
    val deck = remember {
        MemoryDeckFactory.entityCapitalDeck(MexicoContent.pilotEntities, pairCount = 4, seed = 31L)
    }
    var visibleIds by remember { mutableStateOf(setOf<String>()) }
    var matchedPairs by remember { mutableStateOf(setOf<String>()) }
    var attempts by remember { mutableIntStateOf(0) }
    var rewardClaimed by remember { mutableStateOf(false) }
    val complete = matchedPairs.size == deck.size / 2

    LaunchedEffect(visibleIds) {
        if (visibleIds.size == 2) {
            val cards = deck.filter { it.id in visibleIds }
            if (cards.map { it.pairId }.distinct().size == 1) {
                delay(350)
                matchedPairs = matchedPairs + cards.first().pairId
                visibleIds = emptySet()
            } else {
                delay(850)
                visibleIds = emptySet()
            }
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
                subtitle = "Encuentra las cuatro parejas de entidad y capital.",
            )
            Text("Intentos: $attempts", style = MaterialTheme.typography.labelLarge)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(deck, key = { it.id }) { card ->
                    MemoryCardView(
                        card = card,
                        faceUp = card.id in visibleIds || card.pairId in matchedPairs,
                        matched = card.pairId in matchedPairs,
                        enabled = visibleIds.size < 2 && card.pairId !in matchedPairs,
                        onClick = {
                            visibleIds = visibleIds + card.id
                            if (visibleIds.size == 1) attempts += 1
                        },
                    )
                }
            }

            if (complete) {
                InfoBanner("¡Memoria brillante! Uniste todas las entidades con sus capitales.")
                Button(
                    onClick = {
                        if (!rewardClaimed) {
                            onRoundFinished(4, 4, if (attempts <= 6) 3 else 2)
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
    faceUp: Boolean,
    matched: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(126.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                matched -> SuccessGreen.copy(alpha = 0.22f)
                faceUp -> PaleTeal
                else -> ExplorerTeal
            },
            disabledContainerColor = if (matched) SuccessGreen.copy(alpha = 0.22f) else PaleTeal,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (faceUp) {
                Text(
                    card.label,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = NightBlue,
                )
                Text(
                    if (card.kind.name == "ENTITY") "ENTIDAD" else "CAPITAL",
                    style = MaterialTheme.typography.labelLarge,
                    color = NightBlue.copy(alpha = 0.62f),
                )
            } else {
                Text("A", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimary)
                Text("AVENTUMAPA", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
