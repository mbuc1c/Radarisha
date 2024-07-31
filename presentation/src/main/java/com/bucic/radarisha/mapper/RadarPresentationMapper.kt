package com.bucic.radarisha.mapper

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.util.RadarType
import com.bucic.radarisha.entities.RadarMarker
import com.bucic.radarisha.util.ReliabilityPresentation

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
                    updatedAt = updatedAt,
                    reliabilityVotes = reliabilityCounter(reliabilityVotes)
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
                updatedAt = updatedAt,
                reliabilityVotes = reliabilityCounter(reliabilityVotes)
            )
        }

        RadarType.CAR_ACCIDENT -> {
            RadarMarker.CarAccident(
                uid = uid,
                creatorUid = creatorUid,
                lat = lat,
                lng = lng,
                createdAt = createdAt,
                updatedAt = updatedAt,
                reliabilityVotes = reliabilityCounter(reliabilityVotes)
            )
        }
        else -> null
    }
}

private fun reliabilityCounter(reliabilityVotes: List<RadarReliabilityVoteEntity>): ReliabilityPresentation {
    var iterator = 0
    for (vote in reliabilityVotes) {
        if (vote.vote) iterator++ else iterator--
    }
    return when {
        iterator > 0 -> ReliabilityPresentation.RELIABLE
        iterator < 0 -> ReliabilityPresentation.UNRELIABLE
        else -> ReliabilityPresentation.UNKNOWN
    }
}