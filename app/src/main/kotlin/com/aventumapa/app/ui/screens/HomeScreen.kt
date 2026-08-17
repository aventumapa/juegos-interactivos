package com.aventumapa.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.CompassGuide
import com.aventumapa.app.ui.components.InfoBanner
import com.aventumapa.app.ui.components.StatPill
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.PaleTeal
import com.aventumapa.app.ui.theme.PaleYellow
import com.aventumapa.app.ui.theme.SunYellow
import com.aventumapa.app.ui.theme.WarmCoral
import com.aventumapa.core.model.ChildProfile

@Composable
fun HomeScreen(
    profile: ChildProfile,
    onExplore: () -> Unit,
    onQuiz: () -> Unit,
    onMemory: () -> Unit,
    onParents: () -> Unit,
) {
    AventuBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp, 34.dp, 20.dp, 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.home_greeting, profile.alias),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            stringResource(R.string.home_prompt),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f),
                        )
                    }
                    CompassGuide(Modifier.size(68.dp))
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatPill("nivel", profile.level.toString(), ExplorerTeal)
                            StatPill("estrellas", profile.stars.toString(), SunYellow)
                        }
                        Text("Camino al nivel ${profile.level + 1}", fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { (profile.experience % 200) / 200f },
                            modifier = Modifier.fillMaxWidth().height(10.dp),
                            color = ExplorerTeal,
                            trackColor = PaleTeal,
                        )
                        Text("${profile.experience % 200} de 200 XP", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            item {
                AdventureCard(
                    eyebrow = "DESCUBRE",
                    title = "Explora México",
                    description = "Conoce las 32 entidades, sus regiones y capitales.",
                    container = PaleTeal,
                    accent = ExplorerTeal,
                    onClick = onExplore,
                )
            }
            item {
                AdventureCard(
                    eyebrow = "JUEGA",
                    title = stringResource(R.string.quiz_title),
                    description = "Responde cinco preguntas y gana estrellas.",
                    container = PaleYellow,
                    accent = SunYellow,
                    onClick = onQuiz,
                )
            }
            item {
                AdventureCard(
                    eyebrow = "ENTRENA TU MEMORIA",
                    title = stringResource(R.string.memory_title),
                    description = "Une cada entidad con su capital.",
                    container = WarmCoral.copy(alpha = 0.16f),
                    accent = WarmCoral,
                    onClick = onMemory,
                )
            }
            item {
                AdventureCard(
                    eyebrow = "PROGRESO LOCAL",
                    title = stringResource(R.string.parents_title),
                    description = "Consulta avances y controla los datos guardados.",
                    container = MaterialTheme.colorScheme.surface,
                    accent = MaterialTheme.colorScheme.primary,
                    onClick = onParents,
                )
            }
            item {
                InfoBanner(stringResource(R.string.offline_badge) + ". Tu progreso permanece en este dispositivo.")
            }
        }
    }
}

@Composable
private fun AdventureCard(
    eyebrow: String,
    title: String,
    description: String,
    container: Color,
    accent: Color,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = container),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = accent),
            ) {}
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(eyebrow, style = MaterialTheme.typography.labelLarge, color = accent.copy(alpha = 0.95f))
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f))
            }
        }
    }
}

