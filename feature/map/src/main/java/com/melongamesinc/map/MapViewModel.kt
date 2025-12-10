package com.melongamesinc.map

import androidx.lifecycle.viewModelScope
import com.melongamesinc.common.BaseViewModel
import com.melongamesinc.model.GeoPoint
import com.melongamesinc.model.Poi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    // private val fogRepository: FogRepository
) : BaseViewModel<MapState, MapEvent, MapEffect>() {

    override fun createInitialState() = MapState()

    override fun handleEvent(event: MapEvent): MapEvent? {
        when (event) {
            is MapEvent.OnLocationUpdate -> {
                updateLocationAndFog(event.location)
            }

            is MapEvent.OnPoiClick -> {
                val poi = currentState.pois.find { it.id == event.poiId }
                if (poi != null && poi.isDiscovered) {
                    setState { copy(selectedPoi = poi) }
                } else {
                    setEffect { MapEffect.ShowToast("Эта точка скрыта туманом!") }
                }
            }

            is MapEvent.OnDismissPoiDialog -> {
                setState { copy(selectedPoi = null) }
            }

            is MapEvent.OnMapReady -> {
                loadInitialData()
            }
        }
        return null
    }

    private fun updateLocationAndFog(location: GeoPoint) {
        val gridId = "${location.lat.toString().take(6)}_${location.lng.toString().take(6)}"

        setState {
            copy(
                userLocation = location,
                discoveredAreas = discoveredAreas + gridId
            )
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val dummyPois = listOf(
                Poi("1", "Мать Грузия", "Статуя", GeoPoint(41.6881, 44.8048), isDiscovered = true),
                Poi("2", "Скрытый бар", "???", GeoPoint(41.6900, 44.8000), isDiscovered = false)
            )
            setState { copy(pois = dummyPois, isLoading = false) }
        }
    }
}