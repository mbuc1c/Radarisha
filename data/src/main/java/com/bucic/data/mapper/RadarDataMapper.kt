package com.bucic.data.mapper

import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.entities.radar.RadarReliabilityVoteFSData
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.util.RadarType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


fun RadarEntity.toFSData() = RadarFSData(
    creatorUid = creatorUid,
    lat = lat,
    lng = lng,
    type = type.name,
    speed = speed,
    createdAt = Timestamp(createdAt),
    updatedAt = updatedAt?.let { Timestamp(it) }
)

fun RadarReliabilityVoteEntity.toFSData() = RadarReliabilityVoteFSData(
    voterUid = voterUid,
    vote = vote,
    createdAt = Timestamp(createdAt),
    updatedAt = updatedAt?.let { Timestamp(it) }
)

fun DocumentSnapshot.toRadarDomain() = RadarEntity(
    uid = id,
    creatorUid = data!!["creatorUid"].toString(),
    lat = data!!["lat"].toString().toDouble(),
    lng = data!!["lng"].toString().toDouble(),
    type = RadarType.valueOf(data!!["type"].toString()),
    speed = data!!["speed"]?.toString()?.toInt(),
    createdAt = (data!!["createdAt"] as Timestamp).toDate(),
    updatedAt = (data!!["updatedAt"] as? Timestamp)?.toDate()
)

fun DocumentSnapshot.toRadarReliabilityVoteDomain() = RadarReliabilityVoteEntity(
    uid = id,
    radarUid = data!!["radarUid"].toString(),
    voterUid = data!!["voterUid"].toString(),
    vote = data!!["vote"] as Boolean,
    createdAt = (data!!["createdAt"] as Timestamp).toDate(),
    updatedAt = (data!!["updatedAt"] as? Timestamp)?.toDate()
)