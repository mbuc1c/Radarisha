package com.bucic.radarisha.entities

import androidx.annotation.DrawableRes
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
        @DrawableRes val icon: Int = R.drawable.speed_camera_radar_icon
    ) : RadarMarker()

    data class PoliceCar(
        val uid: String,
        val creatorUid: String,
        val lat: Double,
        val lng: Double,
        val createdAt: Date,
        val updatedAt: Date?,
        @DrawableRes val icon: Int = R.drawable.police_car_radar_icon
    ) : RadarMarker()

    data class CarAccident(
        val uid: String,
        val creatorUid: String,
        val lat: Double,
        val lng: Double,
        val createdAt: Date,
        val updatedAt: Date?,
        @DrawableRes val icon: Int = R.drawable.car_accident_radar_icon
    ) : RadarMarker()
}
