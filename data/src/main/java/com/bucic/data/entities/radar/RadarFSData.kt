package com.bucic.data.entities.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.RadarType
import com.google.firebase.Timestamp

data class RadarFSData(
    val creatorUid: String,
    val lat: Double,
    val lng: Double,
    val type: String,
    val speed: Int?,
    val createdAt: Timestamp,
    val updatedAt: Timestamp?
)

fun RadarFSData.toDomain(uid: String) = RadarEntity(
    uid = uid,
    creatorUid = creatorUid,
    lat = lat,
    lng = lng,
    type = RadarType.valueOf(type),
    speed = speed,
    createdAt = createdAt.toDate(),
    updatedAt = updatedAt?.toDate()
)
