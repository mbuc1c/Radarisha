package com.bucic.radarisha.mapper

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.RadarType
import com.bucic.radarisha.entities.RadarMarker

fun RadarEntity.toPresentation(): RadarMarker? {
    return when (this.type) {
        RadarType.SPEED_CAMERA -> {
            speed?.let {
                RadarMarker.SpeedCamera(
                    uid = uid,
                    creatorUid = creatorUid,
                    lat = lat,
                    lng = lng,
                    speed = it,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }

        RadarType.POLICE_CAR -> {
            RadarMarker.PoliceCar(
                uid = uid,
                creatorUid = creatorUid,
                lat = lat,
                lng = lng,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        RadarType.CAR_ACCIDENT -> {
            RadarMarker.CarAccident(
                uid = uid,
                creatorUid = creatorUid,
                lat = lat,
                lng = lng,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
        else -> null
    }
}