package com.bucic.data.repository.radar

import android.util.Log
import com.bucic.data.exception.NoResultFoundException
import com.bucic.data.mapper.toFSData
import com.bucic.data.mapper.toRadarDomain
import com.bucic.data.mapper.toRadarReliabilityVoteDomain
import com.bucic.data.network.firestore.radar.RadarFireStore
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.util.Result
import kotlinx.coroutines.tasks.await

class RadarRemoteDataSource(
    private val radarFireStore: RadarFireStore
) : RadarDataSource.Remote {

    override suspend fun addRadar(radar: RadarEntity) {
        radarFireStore.addRadar(radar.toFSData())
    }

    override suspend fun getAllRadars(): Result<List<RadarEntity>> = try {
        val result = radarFireStore.getAllRadars()
        val radarList = result.documents.map { radarDoc ->
            val radarData = radarDoc.toRadarDomain()
            val reliabilityVotesSnapshot = radarDoc.reference.collection("reliability").get().await()
            val reliabilityVotes = reliabilityVotesSnapshot.documents.map { voteDoc ->
                voteDoc.toRadarReliabilityVoteDomain()
            }
            radarData.copy(reliabilityVotes = reliabilityVotes)
        }
        Log.d("MyTag", "getAllRadars: $radarList")
        Result.Success(radarList)
    } catch (e: NoResultFoundException) {
        Result.Error(e.message)
    }

    override suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String> = try {
        radarFireStore.vote(radarReliabilityVote.toFSData(), radarReliabilityVote.radarUid)
        Result.Success("Successfully voted")
    } catch (e: Exception) {
        Result.Error(e.message.toString()) // TODO: make custom exception
    }
}