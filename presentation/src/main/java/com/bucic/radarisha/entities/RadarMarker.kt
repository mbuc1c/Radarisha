package com.bucic.radarisha.entities

import androidx.annotation.DrawableRes
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.util.RadarType
import com.bucic.radarisha.R
import com.bucic.radarisha.util.ReliabilityPresentation
import java.util.Date

sealed class RadarMarker(
    open val uid: String,
    open val creatorUid: String,
    open val lat: Double,
    open val lng: Double,
    open val createdAt: Date,
    open val updatedAt: Date?,
    open val reliabilityVotes: ReliabilityPresentation,
    @DrawableRes open val icon: Int
) {
    data class SpeedCamera(
        override val uid: String,
        override val creatorUid: String,
        override val lat: Double,
        override val lng: Double,
        val speed: Int,
        override val createdAt: Date,
        override val updatedAt: Date?,
        override val reliabilityVotes: ReliabilityPresentation,
        @DrawableRes override val icon: Int = R.drawable.speed_camera_radar_icon
    ) : RadarMarker(uid, creatorUid, lat, lng, createdAt, updatedAt, reliabilityVotes, icon)

    data class PoliceCar(
        override val uid: String,
        override val creatorUid: String,
        override val lat: Double,
        override val lng: Double,
        override val createdAt: Date,
        override val updatedAt: Date?,
        override val reliabilityVotes: ReliabilityPresentation,
        @DrawableRes override val icon: Int = R.drawable.police_car_radar_icon
    ) : RadarMarker(uid, creatorUid, lat, lng, createdAt, updatedAt, reliabilityVotes, icon)

    data class CarAccident(
        override val uid: String,
        override val creatorUid: String,
        override val lat: Double,
        override val lng: Double,
        override val createdAt: Date,
        override val updatedAt: Date?,
        override val reliabilityVotes: ReliabilityPresentation,
        @DrawableRes override val icon: Int = R.drawable.car_accident_radar_icon
    ) : RadarMarker(uid, creatorUid, lat, lng, createdAt, updatedAt, reliabilityVotes, icon)
}

fun RadarMarker.toDomain(): RadarEntity {
    return when (this) {
        is RadarMarker.SpeedCamera -> RadarEntity(
            uid = uid,
            creatorUid = creatorUid,
            lat = lat,
            lng = lng,
            type = RadarType.SPEED_CAMERA,
            speed = speed,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
        is RadarMarker.PoliceCar -> RadarEntity(
            uid = uid,
            creatorUid = creatorUid,
            lat = lat,
            lng = lng,
            type = RadarType.POLICE_CAR,
            speed = null,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
        is RadarMarker.CarAccident -> RadarEntity(
            uid = uid,
            creatorUid = creatorUid,
            lat = lat,
            lng = lng,
            type = RadarType.CAR_ACCIDENT,
            speed = null,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}