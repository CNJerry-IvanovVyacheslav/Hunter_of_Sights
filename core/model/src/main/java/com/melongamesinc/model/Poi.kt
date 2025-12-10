package com.melongamesinc.model

data class Poi(
    val id: String,
    val name: String,
    val description: String,
    val location: GeoPoint,
    val isDiscovered: Boolean = false
)
