package com.melongamesinc.map

import androidx.lifecycle.viewModelScope
import com.melongamesinc.common.BaseViewModel
import com.melongamesinc.data.LocationClient
import com.melongamesinc.model.GeoPoint
import com.melongamesinc.model.Poi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationClient: LocationClient
) : BaseViewModel<MapState, MapEvent, MapEffect>() {

    init {
        collectLocation()
    }

    override fun createInitialState() = MapState()

    private fun collectLocation() {
        locationClient
            .getLocationUpdates(interval = 5000L)
            .onEach { location ->
                setEvent(MapEvent.OnLocationUpdate(location))
            }
            .catch {
                setEffect { MapEffect.ShowToast("Не удалось получить GPS: ${it.message}") }
            }
            .launchIn(viewModelScope)
    }

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
        val step = 0.0005

        val gridLat = (kotlin.math.floor(location.lat / step) * step)
        val gridLng = (kotlin.math.floor(location.lng / step) * step)

        val gridId = String.format(Locale.US, "%.5f,%.5f", gridLat, gridLng)

        val newPois = currentState.pois.map { poi ->
            if (!poi.isDiscovered && isNear(
                    location,
                    poi.location,
                    0.0005
                )
            ) {
                setEffect { MapEffect.ShowToast("Открыто: ${poi.name}!") }
                poi.copy(isDiscovered = true)
            } else {
                poi
            }
        }

        if (!currentState.discoveredAreas.contains(gridId)) {
            setState {
                copy(
                    userLocation = location,
                    discoveredAreas = discoveredAreas + gridId,
                    pois = newPois
                )
            }
        } else {
            setState { copy(userLocation = location, pois = newPois) }
        }
    }

    private fun isNear(loc1: GeoPoint, loc2: GeoPoint, threshold: Double): Boolean {
        val latDiff = abs(loc1.lat - loc2.lat)
        val lngDiff = abs(loc1.lng - loc2.lng)
        return (latDiff * latDiff) + (lngDiff * lngDiff) < (threshold * threshold)
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