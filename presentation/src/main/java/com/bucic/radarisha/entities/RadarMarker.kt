package com.bucic.radarisha.entities

import com.bucic.radarisha.R
import java.util.Date

sealed class RadarMarker {
    data class SpeedCamera(
        val uid: String,
        val creatorUid: String,
        val lat: Double,
        val lng: Double,
        val speed: Int,
        val createdAt: Date,
        val updatedAt: Date?,
        val icon: Int = R.drawable.ic_speed_camera
    ) : RadarMarker()

    data class PoliceCar(
        val uid: String,
        val creatorUid: String,
        val lat: Double,
        val lng: Double,
        val createdAt: Date,
        val updatedAt: Date?
    ) : RadarMarker()

    data class CarAccident(
        val uid: String,
        val creatorUid: String,
        val lat: Double,
        val lng: Double,
        val createdAt: Date,
        val updatedAt: Date?
    ) : RadarMarker()
}
