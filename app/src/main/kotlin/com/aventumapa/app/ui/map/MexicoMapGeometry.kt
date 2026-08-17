package com.aventumapa.app.ui.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.json.JSONObject
import java.util.zip.GZIPInputStream

data class MapPoint(val x: Float, val y: Float)

data class MapBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

data class MexicoStateShape(
    val code: String,
    val centroid: MapPoint,
    val bounds: MapBounds,
    val polygons: List<List<MapPoint>>,
)

@Composable
fun rememberMexicoMapGeometry(): List<MexicoStateShape> {
    val context = LocalContext.current
    return remember(context) { loadMexicoMapGeometry(context) }
}

private fun loadMexicoMapGeometry(context: Context): List<MexicoStateShape> {
    val payload = runCatching {
        context.assets.open("mexico_states.json").bufferedReader().use { it.readText() }
    }.getOrElse {
        GZIPInputStream(context.assets.open("mexico_states.json.gz"))
            .bufferedReader()
            .use { reader -> reader.readText() }
    }
    val states = JSONObject(payload).getJSONArray("states")
    return buildList(states.length()) {
        repeat(states.length()) { stateIndex ->
            val state = states.getJSONObject(stateIndex)
            val centroid = state.getJSONArray("centroid")
            val bounds = state.getJSONArray("bounds")
            val polygons = state.getJSONArray("polygons")
            add(
                MexicoStateShape(
                    code = state.getString("code"),
                    centroid = MapPoint(centroid.getDouble(0).toFloat(), centroid.getDouble(1).toFloat()),
                    bounds = MapBounds(
                        left = bounds.getDouble(0).toFloat(),
                        top = bounds.getDouble(1).toFloat(),
                        right = bounds.getDouble(2).toFloat(),
                        bottom = bounds.getDouble(3).toFloat(),
                    ),
                    polygons = buildList(polygons.length()) {
                        repeat(polygons.length()) { polygonIndex ->
                            val polygon = polygons.getJSONArray(polygonIndex)
                            add(
                                buildList(polygon.length()) {
                                    repeat(polygon.length()) { pointIndex ->
                                        val point = polygon.getJSONArray(pointIndex)
                                        add(MapPoint(point.getDouble(0).toFloat(), point.getDouble(1).toFloat()))
                                    }
                                },
                            )
                        }
                    },
                ),
            )
        }
    }
}
