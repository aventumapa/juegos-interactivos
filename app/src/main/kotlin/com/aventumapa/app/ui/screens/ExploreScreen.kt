package com.aventumapa.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aventumapa.app.audio.SoundCue
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.InfoBanner
import com.aventumapa.app.ui.map.InteractiveMexicoMap
import com.aventumapa.app.ui.map.MexicoStateShape
import com.aventumapa.app.ui.map.StateSilhouette
import com.aventumapa.app.ui.map.rememberMexicoMapGeometry
import com.aventumapa.app.ui.theme.CardNavy
import com.aventumapa.app.ui.theme.CyanElectric
import com.aventumapa.app.ui.theme.IceWhite
import com.aventumapa.content.mexico.MexicoContent
import com.aventumapa.core.model.ChildProfile
import com.aventumapa.core.model.GeographicUnit

@Suppress("UNUSED_PARAMETER")
@Composable
fun ExploreScreen(
    profile: ChildProfile,
    onBack: () -> Unit,
    onSpeak: (String) -> Unit,
    onSound: (SoundCue) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selectedCode by remember { mutableStateOf<String?>("14") }
    val mapGeometry = rememberMexicoMapGeometry()
    val shapesByCode = remember(mapGeometry) { mapGeometry.associateBy { it.code } }
    val results = remember(query) {
        if (query.isBlank()) MexicoContent.entities else MexicoContent.search(query)
    }
    val selected = selectedCode?.let(MexicoContent::findByCode)

    fun selectEntity(code: String) {
        val entity = MexicoContent.findByCode(code) ?: return
        selectedCode = code
        onSound(SoundCue.TAP)
        onSpeak("${entity.learningName}. Su capital es ${entity.capital}.")
    }

    AventuBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            item {
                ExploreHeader(
                    onBack = onBack,
                    onHear = {
                        selected?.let { selectEntity(it.officialCode) }
                            ?: onSpeak("Toca un estado para escuchar su nombre y su capital.")
                    },
                )
            }
            item {
                InteractiveMexicoMap(
                    states = mapGeometry,
                    selectedCode = selectedCode,
                    onStateSelected = ::selectEntity,
                )
            }
            if (selected != null) {
                item {
                    SelectedEntityCard(
                        entity = selected,
                        onHearAgain = { selectEntity(selected.officialCode) },
                    )
                }
            }
            item {
                SmallEntityLens(
                    shapesByCode = shapesByCode,
                    selectedCode = selectedCode,
                    onSelected = ::selectEntity,
                )
            }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar entidad o capital") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                )
            }
            item { Text("Las 32 entidades", style = MaterialTheme.typography.titleLarge) }
            items(results, key = { it.officialCode }) { entity ->
                EntityRow(
                    entity = entity,
                    shape = shapesByCode[entity.officialCode],
                    selected = entity.officialCode == selectedCode,
                    onClick = { selectEntity(entity.officialCode) },
                )
            }
            item {
                InfoBanner("Las ampliaciones conservan el contorno real de cada entidad y el mapa nacional mantiene su proporción.")
            }
        }
    }
}

@Composable
private fun ExploreHeader(onBack: () -> Unit, onHear: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 7.dp, vertical = 4.dp)) {
            Text("‹", style = MaterialTheme.typography.headlineMedium, color = CyanElectric)
        }
        Text(
            text = "Explora México",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = IceWhite,
        )
        AudioCircleButton(onClick = onHear)
    }
}

@Composable
private fun SmallEntityLens(
    shapesByCode: Map<String, MexicoStateShape>,
    selectedCode: String?,
    onSelected: (String) -> Unit,
) {
    val smallEntityCodes = remember { listOf("29", "17", "09", "06", "01", "22") }
    val smallEntities = remember { smallEntityCodes.mapNotNull(MexicoContent::findByCode) }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(smallEntities, key = { it.officialCode }) { entity ->
            val shape = shapesByCode[entity.officialCode] ?: return@items
            Card(
                onClick = { onSelected(entity.officialCode) },
                modifier = Modifier.size(width = 82.dp, height = 116.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    if (selectedCode == entity.officialCode) 2.dp else 1.dp,
                    if (selectedCode == entity.officialCode) CyanElectric else CyanElectric.copy(alpha = 0.24f),
                ),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    StateSilhouette(shape, Modifier.size(68.dp), selected = selectedCode == entity.officialCode)
                    Text(
                        entity.learningName,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedEntityCard(entity: GeographicUnit, onHearAgain: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CyanElectric.copy(alpha = 0.48f)),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(entity.learningName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("Capital: ${requireNotNull(entity.capital)}", color = IceWhite.copy(alpha = 0.72f), fontWeight = FontWeight.Medium)
            }
            AudioCircleButton(onClick = onHearAgain)
        }
    }
}

@Composable
private fun AudioCircleButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(CyanElectric.copy(alpha = 0.12f))
            .border(1.dp, CyanElectric.copy(alpha = 0.76f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(22.dp)) {
            val icon = Path().apply {
                moveTo(size.width * 0.10f, size.height * 0.40f)
                lineTo(size.width * 0.34f, size.height * 0.40f)
                lineTo(size.width * 0.55f, size.height * 0.20f)
                lineTo(size.width * 0.55f, size.height * 0.80f)
                lineTo(size.width * 0.34f, size.height * 0.60f)
                lineTo(size.width * 0.10f, size.height * 0.60f)
                close()
            }
            drawPath(icon, CyanElectric)
            drawArc(CyanElectric, -48f, 96f, false, Offset(size.width * 0.41f, size.height * 0.27f), Size(size.width * 0.36f, size.height * 0.46f), style = Stroke(size.width * 0.08f))
            drawArc(CyanElectric, -48f, 96f, false, Offset(size.width * 0.36f, size.height * 0.15f), Size(size.width * 0.56f, size.height * 0.70f), style = Stroke(size.width * 0.07f))
        }
    }
}

@Composable
private fun EntityRow(
    entity: GeographicUnit,
    shape: MexicoStateShape?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) CyanElectric else CyanElectric.copy(alpha = 0.16f)),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            if (shape != null) StateSilhouette(shape, Modifier.size(58.dp), selected = selected)
            Column(Modifier.weight(1f)) {
                Text(entity.learningName, fontWeight = FontWeight.Bold)
                Text(requireNotNull(entity.capital), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
            }
            Text("Escuchar", style = MaterialTheme.typography.labelLarge, color = CyanElectric)
        }
    }
}
