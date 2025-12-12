package com.melongamesinc.data

import com.melongamesinc.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(interval: Long): Flow<GeoPoint>

    fun isGpsEnabled(): Boolean
}