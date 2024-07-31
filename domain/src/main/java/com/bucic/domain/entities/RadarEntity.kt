package com.bucic.domain.entities

import com.bucic.domain.util.RadarType
import java.util.Date

data class RadarEntity(
    val uid: String,
    val creatorUid: String,
    val lat: Double,
    val lng: Double,
    val type: RadarType,
    val speed: Int?,
    val createdAt: Date,
    val updatedAt: Date?,
    val reliabilityVotes: List<RadarReliabilityVoteEntity> = emptyList()
)

data class RadarReliabilityVoteEntity(
    val uid: String,
    val radarUid: String,
    val voterUid: String,
    val vote: Boolean,
    val createdAt: Date,
    val updatedAt: Date?
)