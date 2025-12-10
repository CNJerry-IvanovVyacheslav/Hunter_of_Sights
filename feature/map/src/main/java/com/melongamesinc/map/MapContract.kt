package com.melongamesinc.map

import com.melongamesinc.common.UiEffect
import com.melongamesinc.common.UiEvent
import com.melongamesinc.common.UiState
import com.melongamesinc.model.GeoPoint
import com.melongamesinc.model.Poi

data class MapState(
    val userLocation: GeoPoint? = null,
    val isLoading: Boolean = false,
    val pois: List<Poi> = emptyList(),
    val discoveredAreas: Set<String> = emptySet(),
    val selectedPoi: Poi? = null
) : UiState

sealed class MapEvent : UiEvent {
    data class OnLocationUpdate(val location: GeoPoint) : MapEvent()
    data class OnPoiClick(val poiId: String) : MapEvent()
    data object OnMapReady : MapEvent()
    data object OnDismissPoiDialog : MapEvent()
}

sealed class MapEffect : UiEffect {
    data class ShowToast(val message: String) : MapEffect()
    // data class NavigateToDetails(val id: String) : MapEffect()
}