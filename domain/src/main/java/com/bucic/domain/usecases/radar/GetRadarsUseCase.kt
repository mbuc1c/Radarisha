package com.bucic.domain.usecases.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.RadarsCallback
import com.bucic.domain.util.Result

class GetRadarsUseCase(
    private val radarRepository: RadarRepository
) {
    operator fun invoke(callback: RadarsCallback) {
        radarRepository.getRadars(callback)
    }
}