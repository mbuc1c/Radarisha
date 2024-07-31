package com.bucic.domain.usecases.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.Result

class VoteReliabilityUseCase(
    private val radarRepository: RadarRepository
) {
    suspend operator fun invoke(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String> {
        return radarRepository.vote(radarReliabilityVote)
    }
}