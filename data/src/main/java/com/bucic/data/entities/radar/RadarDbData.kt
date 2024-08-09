package com.bucic.data.entities.radar

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.RadarType
import java.util.Date

@Entity(tableName = "radars")
data class RadarDbData(
    @PrimaryKey val uid: String,
    val creatorUid: String,
    val lat: Double,
    val lng: Double,
    val type: RadarType,
    val speed: Int?,
    val createdAt: Date,
    val updatedAt: Date?
)

fun RadarDbData.toDomain() = RadarEntity(
    uid = uid,
    creatorUid = creatorUid,
    lat = lat,
    lng = lng,
    type = type,
    speed = speed,
    createdAt = createdAt,
    updatedAt = updatedAt
)