package com.bucic.data.repository.radar

import android.util.Log
import com.bucic.data.util.NetworkConnectivityChecker
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.RadarsCallback
import com.bucic.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RadarRepositoryImpl(
    private val remote: RadarDataSource.Remote,
    private val local: RadarDataSource.Local,
    private val networkConnectivityChecker: NetworkConnectivityChecker
) : RadarRepository {

    override suspend fun createRadar(radar: RadarEntity): Result<String> =
        withContext(Dispatchers.IO) {
            remote.addRadar(radar)
        }

    override fun getRadars(callback: RadarsCallback) {
        if (networkConnectivityChecker.isNetworkAvailable()) {
            remote.getAllRadars(callback)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                when (val localRadars = local.getAllRadars()) {
                    is Result.Success -> {
                        callback.onSuccess(localRadars.data)
                    }

                    is Result.Error -> {
                        callback.onError(localRadars.message)
                    }
                }
            }
        }
    }

    override suspend fun getRadarByUid(uid: String): Result<RadarEntity> =
        withContext(Dispatchers.IO) {
            remote.getRadarByUid(uid)
        }

    override suspend fun deleteRadar(radar: RadarEntity): Result<String> =
        withContext(Dispatchers.IO) {
            remote.deleteRadar(radar)
        }

    override suspend fun updateRadar(radar: RadarEntity): Result<String> =
        withContext(Dispatchers.IO) {
            remote.updateRadar(radar)
        }

    // TODO: continuation issue
    override suspend fun sync() {
        if (networkConnectivityChecker.isNetworkAvailable()) {
            remote.getAllRadars(object : RadarsCallback {
                override fun onSuccess(data: List<RadarEntity>) {
                    CoroutineScope(Dispatchers.IO).launch {
                        local.addRadars(data)
                    }
                }

                override fun onError(message: String) {
                    // You might want to log the error or handle it appropriately here
                    Log.e("MyTag", "Sync failed: $message")
                }
            })
        } else {
            Log.e("MyTag", "No network available for sync")
        }
    }

    override suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String> =
        withContext(Dispatchers.IO) {
            remote.vote(radarReliabilityVote)
        }
}