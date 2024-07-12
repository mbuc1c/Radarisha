package com.bucic.data.mapper

import com.bucic.data.entities.radar.RadarFSData
import com.bucic.domain.entities.RadarEntity
import com.google.firebase.Timestamp


fun RadarEntity.toFSData() = RadarFSData(
    creatorUid = creatorUid,
    lat = lat,
    lng = lng,
    type = type.name,
    speed = speed,
    createdAt = Timestamp(createdAt),
    updatedAt = updatedAt?.let { Timestamp(it) }
)