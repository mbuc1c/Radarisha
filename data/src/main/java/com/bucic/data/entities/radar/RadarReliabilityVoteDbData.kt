package com.bucic.data.entities.radar

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import java.util.Date

@Entity(tableName = "radar_reliability_votes")
data class RadarReliabilityVoteDbData(
    @PrimaryKey val uid: String,
    val radarUid: String,
    val voterUid: String,
    val vote: Boolean,
    val createdAt: Date,
    val updatedAt: Date?
)

fun RadarReliabilityVoteDbData.toDomain() = RadarReliabilityVoteEntity(
    uid = uid,
    radarUid = radarUid,
    voterUid = voterUid,
    vote = vote,
    createdAt = createdAt,
    updatedAt = updatedAt
)