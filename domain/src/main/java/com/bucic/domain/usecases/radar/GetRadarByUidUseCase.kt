package com.bucic.domain.usecases.radar

import com.bucic.domain.repository.RadarRepository

class GetRadarByUidUseCase(
    private val radarRepository: RadarRepository
) {
    suspend operator fun invoke(uid: String) = radarRepository.getRadarByUid(uid)
}