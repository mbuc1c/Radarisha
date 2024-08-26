package com.bucic.domain.usecases.radar

import com.bucic.domain.repository.RadarRepository

class SyncRadarsUseCase(
    private val radarRepository: RadarRepository
) {
    suspend operator fun invoke() = radarRepository.sync()
}