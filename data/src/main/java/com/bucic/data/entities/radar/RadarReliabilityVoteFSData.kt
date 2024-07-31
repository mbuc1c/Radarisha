package com.bucic.data.entities.radar

import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.google.firebase.Timestamp

data class RadarReliabilityVoteFSData(
    val voterUid: String,
    val vote: Boolean,
    val createdAt: Timestamp,
    val updatedAt: Timestamp?
)

fun RadarReliabilityVoteFSData.toDomain(uid: String, radarUid: String) = RadarReliabilityVoteEntity(
    uid = uid,
    radarUid = radarUid,
    voterUid = voterUid,
    vote = vote,
    createdAt = createdAt.toDate(),
    updatedAt = updatedAt?.toDate()
)