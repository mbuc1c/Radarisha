package com.bucic.data.repository.radar

import android.util.Log
import com.bucic.data.exception.NoResultFoundException
import com.bucic.data.mapper.toFSData
import com.bucic.data.mapper.toRadarDomain
import com.bucic.data.mapper.toRadarReliabilityVoteDomain
import com.bucic.data.network.firestore.radar.RadarFireStore
import com.bucic.data.util.NetworkConnectivityChecker
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.util.Result
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class RadarRemoteDataSource(
    private val radarFireStore: RadarFireStore,
    private val networkConnectivityChecker: NetworkConnectivityChecker
) : RadarDataSource.Remote {

    override suspend fun addRadar(radar: RadarEntity): Result<String> {
        return if (networkConnectivityChecker.isNetworkAvailable()) {
            radarFireStore.addRadar(radar.toFSData())
            Result.Success("Radar added successfully")
        } else {
            Result.Error("No internet connection.")
        }
    }

    override suspend fun getAllRadars(): Result<List<RadarEntity>> {
        return if (networkConnectivityChecker.isNetworkAvailable()) {
            try {
                val result = radarFireStore.getAllRadars()
                val radarList = result.documents.map { radarDoc ->
                    val radarData = radarDoc.toRadarDomain()
                    val reliabilityVotesSnapshot =
                        radarDoc.reference.collection("reliability").get().await()
                    val reliabilityVotes = reliabilityVotesSnapshot.documents.map { voteDoc ->
                        voteDoc.toRadarReliabilityVoteDomain()
                    }
                    radarData.copy(reliabilityVotes = reliabilityVotes)
                }
                Log.d("MyTag", "getAllRadars: $radarList")
                Result.Success(radarList)
            } catch (e: NoResultFoundException) {
                Result.Error(e.message)
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        } else return Result.Error("Couldn't fetch new radars.\nNo internet connection.")
    }

    override suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String> {
        return if (networkConnectivityChecker.isNetworkAvailable()) {
            try {
                radarFireStore.vote(radarReliabilityVote.toFSData(), radarReliabilityVote.radarUid)
                Result.Success("Successfully voted")
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        } else {
            return Result.Error("No internet connection.")
        }
    }
}