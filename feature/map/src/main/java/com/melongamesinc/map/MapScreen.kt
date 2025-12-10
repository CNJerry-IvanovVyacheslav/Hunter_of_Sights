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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
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
                    title = "Я"
                )
            }

            state.pois.forEach { poi ->
                val icon = if (poi.isDiscovered) BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED
                )
                else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)

                Marker(
                    state = MarkerState(position = LatLng(poi.location.lat, poi.location.lng)),
                    icon = icon,
                    title = if (poi.isDiscovered) poi.name else "???",
                    onClick = {
                        viewModel.setEvent(MapEvent.OnPoiClick(poi.id))
                        true
                    }
                )
            }

            state.discoveredAreas.forEach { areaId ->
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

@Composable
fun PoiDetailsDialog(poi: Poi, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = poi.name) },
        text = { Text(text = poi.description) },
        confirmButton = { Button(onClick = onDismiss) { Text("Круто") } }
    )
}