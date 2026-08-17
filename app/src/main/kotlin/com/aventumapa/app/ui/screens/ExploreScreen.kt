package com.aventumapa.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.CompassGuide
import com.aventumapa.app.ui.components.InfoBanner
import com.aventumapa.app.ui.components.SectionHeader
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.PaleTeal
import com.aventumapa.content.mexico.MexicoContent
import com.aventumapa.core.model.GeographicUnit

@Composable
fun ExploreScreen(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedCode by remember { mutableStateOf("29") }
    val results = remember(query) {
        if (query.isBlank()) MexicoContent.entities else MexicoContent.search(query)
    }
    val selected = MexicoContent.findByCode(selectedCode)

    AventuBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp, 30.dp, 20.dp, 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                SectionHeader(
                    title = stringResource(R.string.explore_title),
                    subtitle = stringResource(R.string.explore_subtitle),
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
            if (selected != null) {
                item { SelectedEntityCard(selected) }
            }
            item {
                Text("32 entidades federativas", style = MaterialTheme.typography.titleLarge)
            }
            items(results, key = { it.officialCode }) { entity ->
                EntityRow(entity, selected = entity.officialCode == selectedCode) {
                    selectedCode = entity.officialCode
                }
            }
            item { InfoBanner(stringResource(R.string.source_note)) }
        }
    }
}

@Composable
private fun SelectedEntityCard(entity: GeographicUnit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PaleTeal),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CompassGuide(Modifier.size(58.dp))
                Column(Modifier.weight(1f)) {
                    Text(entity.learningName, style = MaterialTheme.typography.titleLarge)
                    Text("Clave ${entity.officialCode}", style = MaterialTheme.typography.labelLarge)
                }
            }
            EntityFact(stringResource(R.string.capital_label), requireNotNull(entity.capital))
            EntityFact(stringResource(R.string.region_label), entity.educationalRegion)
            if (entity.smallEntityCandidate) {
                InfoBanner(stringResource(R.string.small_entity_message) + ". " + stringResource(R.string.small_entity_pending))
            }
        }
    }
}

@Composable
private fun EntityFact(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EntityRow(entity: GeographicUnit, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = if (selected) BorderStroke(2.dp, ExplorerTeal) else null,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Card(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (entity.smallEntityCandidate) PaleTeal else ExplorerTeal.copy(alpha = 0.12f)),
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(entity.officialCode, fontWeight = FontWeight.Black, color = ExplorerTeal)
                }
            }
            Column(Modifier.weight(1f)) {
                Text(entity.learningName, fontWeight = FontWeight.Bold)
                Text(requireNotNull(entity.capital), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
            }
            Text(entity.educationalRegion, style = MaterialTheme.typography.labelLarge)
        }
    }
}

