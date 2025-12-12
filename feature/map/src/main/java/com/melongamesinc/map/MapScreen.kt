package com.melongamesinc.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.melongamesinc.model.Poi

@Composable
fun MapScreen() {
    val viewModel = hiltViewModel<MapViewModel>()

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.ShowToast -> { /* Show Toast */
                }
            }
        }
    }

    val tbilisi = LatLng(41.7151, 44.8271)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tbilisi, 14f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { viewModel.setEvent(MapEvent.OnMapReady) }
        ) {
            state.userLocation?.let { loc ->
                Marker(
                    state = MarkerState(position = LatLng(loc.lat, loc.lng)),
                    title = "Я",
                    zIndex = 100f // Рисуем поверх всего
                )
            }

            // 2. Маркеры POI
            state.pois.forEach { poi ->
                // ... (твой код маркеров)
            }

            // 3. ТУМАН ВОЙНЫ (СЛЕД)
            // Важно: step должен совпадать с ViewModel!
            val step = 0.0005

            state.discoveredAreas.forEach { areaId ->
                val bounds = getGridCellBounds(areaId, step)
                if (bounds != null) {
                    Polygon(
                        points = bounds,
                        // Сделаем ярко-синий цвет (Blue), 40% непрозрачности (0x66)
                        fillColor = androidx.compose.ui.graphics.Color(0x660000FF),
                        // Добавим обводку, чтобы видеть границы квадратов
                        strokeColor = androidx.compose.ui.graphics.Color.Blue,
                        strokeWidth = 2f,
                        zIndex = 1f // Рисуем чуть выше карты, но ниже маркера игрока
                    )
                }
            }
        }

        if (state.selectedPoi != null) {
            PoiDetailsDialog(
                poi = state.selectedPoi!!,
                onDismiss = { viewModel.setEvent(MapEvent.OnDismissPoiDialog) }
            )
        }
    }
}

private fun getGridCellBounds(areaId: String, step: Double): List<LatLng>? {
    return try {
        // В String.format в зависимости от локали может быть запятая как разделитель дроби
        // Поэтому лучше надежно заменить запятую на точку, если она разделитель координат
        // Но в нашем формате "lat,lng" лучше использовать split по запятой аккуратно.

        val parts = areaId.split(",")
        // Если вдруг локаль русская, числа могут быть "41,123", "44,456".
        // Это сломает логику.
        // В ViewModel лучше использовать Locale.US в String.format, но пока сделаем проще:

        if (parts.size != 2) return null

        // replace(',', '.') нужен, если вдруг локаль телефона подставила запятые в числа
        val lat = parts[0].replace(',', '.').toDouble()
        val lng = parts[1].replace(',', '.').toDouble()

        listOf(
            LatLng(lat, lng),
            LatLng(lat + step, lng),
            LatLng(lat + step, lng + step),
            LatLng(lat, lng + step)
        )
    } catch (e: Exception) {
        null
    }
}

@Composable
fun PoiDetailsDialog(poi: Poi, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = poi.name) },
        text = { Text(text = poi.description) },
        confirmButton = { Button(onClick = onDismiss) { Text("Круто") } }
    )
}