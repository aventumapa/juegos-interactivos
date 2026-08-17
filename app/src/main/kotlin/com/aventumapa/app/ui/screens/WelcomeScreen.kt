package com.aventumapa.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.CompassGuide
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.NightBlue
import com.aventumapa.app.ui.theme.PaleYellow
import com.aventumapa.app.ui.theme.SunYellow
import com.aventumapa.app.ui.theme.WarmCoral

@Composable
fun WelcomeScreen(
    existingAlias: String,
    existingAvatarId: String,
    onContinueExisting: () -> Unit,
    onCreateProfile: (alias: String, avatarId: String) -> Unit,
) {
    var alias by remember(existingAlias) { mutableStateOf(existingAlias) }
    var avatar by remember(existingAvatarId) { mutableStateOf(existingAvatarId) }

    AventuBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(18.dp))
            CompassGuide(Modifier.size(142.dp))
            Spacer(Modifier.height(22.dp))
            Text(
                text = "AventuMapa",
                style = MaterialTheme.typography.displaySmall,
                color = ExplorerTeal,
                fontWeight = FontWeight.Black,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(28.dp))

            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it.take(20) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.alias_label)) },
                placeholder = { Text(stringResource(R.string.alias_hint)) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            )

            Spacer(Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.avatar_label),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AvatarChoice("brujula", "Brújula", ExplorerTeal, avatar == "brujula", Modifier.weight(1f)) { avatar = "brujula" }
                AvatarChoice("sol", "Sol", SunYellow, avatar == "sol", Modifier.weight(1f)) { avatar = "sol" }
                AvatarChoice("ruta", "Ruta", WarmCoral, avatar == "ruta", Modifier.weight(1f)) { avatar = "ruta" }
            }

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = { onCreateProfile(alias.trim(), avatar) },
                enabled = alias.trim().length >= 2,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ExplorerTeal),
            ) {
                Text(
                    if (existingAlias.isBlank()) stringResource(R.string.start_adventure)
                    else "Guardar y continuar",
                )
            }

            if (existingAlias.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onContinueExisting,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PaleYellow,
                        contentColor = NightBlue,
                    ),
                ) {
                    Text(stringResource(R.string.continue_adventure))
                }
            }
        }
    }
}

@Composable
private fun AvatarChoice(
    id: String,
    label: String,
    color: Color,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, color) else null,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Canvas(Modifier.size(38.dp)) {
                drawCircle(color, radius = size.minDimension / 2f)
                when (id) {
                    "brujula" -> {
                        drawCircle(Color.White, radius = size.minDimension * 0.32f)
                        drawLine(NightBlue, center, Offset(center.x, size.height * 0.20f), strokeWidth = 5f)
                    }
                    "sol" -> drawCircle(Color.White, radius = size.minDimension * 0.20f)
                    else -> {
                        drawCircle(Color.White, radius = size.minDimension * 0.10f, center = Offset(size.width * 0.28f, size.height * 0.68f))
                        drawCircle(Color.White, radius = size.minDimension * 0.10f, center = Offset(size.width * 0.72f, size.height * 0.30f))
                        drawLine(Color.White, Offset(size.width * 0.31f, size.height * 0.63f), Offset(size.width * 0.68f, size.height * 0.34f), strokeWidth = 5f)
                    }
                }
            }
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

