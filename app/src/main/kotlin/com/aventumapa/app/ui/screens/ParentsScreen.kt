package com.aventumapa.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.InfoBanner
import com.aventumapa.app.ui.components.SectionHeader
import com.aventumapa.app.ui.theme.PaleTeal
import com.aventumapa.app.ui.theme.PaleYellow
import com.aventumapa.core.model.ChildProfile

@Composable
fun ParentsScreen(
    profile: ChildProfile,
    onBack: () -> Unit,
    onDeleteProfile: () -> Unit,
) {
    var unlocked by remember { mutableStateOf(false) }
    var answer by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    AventuBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
            SectionHeader(stringResource(R.string.parents_title), "Información local para madres, padres, tutores o docentes.")

            if (!unlocked) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PaleYellow),
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                        Text("Comprobación para adultos", style = MaterialTheme.typography.titleLarge)
                        Text("Para entrar, escribe el resultado de 7 + 5.")
                        OutlinedTextField(
                            value = answer,
                            onValueChange = { answer = it.take(2) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Resultado") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                        )
                        Button(
                            onClick = { unlocked = answer == "12" },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(17.dp),
                        ) { Text("Abrir panel") }
                    }
                }
            } else {
                StatCard("Sesiones completadas", profile.completedRounds.toString())
                StatCard("Respuestas correctas", "${profile.correctAnswers} de ${profile.totalAnswers}")
                StatCard("Precisión acumulada", "${profile.accuracy}%")
                StatCard("Experiencia", "${profile.experience} XP · nivel ${profile.level}")

                InfoBanner(
                    if (profile.totalAnswers == 0) {
                        "Todavía no hay rondas. Empiecen con el reto de capitales o el memorama."
                    } else if (profile.accuracy >= 80) {
                        "Reconoce la mayoría del contenido practicado. Conviene alternar exploración y memoria."
                    } else {
                        "Está aprendiendo. Repetir sesiones breves ayudará más que una sesión larga."
                    },
                )

                TextButton(onClick = { showDeleteConfirmation = true }) {
                    Text("Eliminar perfil y progreso local", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("¿Eliminar todos los datos locales?") },
            text = { Text("Se borrarán el alias, estrellas, experiencia y resultados. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    onDeleteProfile()
                }) { Text("Sí, eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PaleTeal),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(17.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label)
            Text(value, fontWeight = FontWeight.Black)
        }
    }
}

