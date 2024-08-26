package com.bucic.radarisha.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bucic.domain.repository.RadarRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//@HiltWorker
//class SyncWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted params: WorkerParameters,
//    private val radarRepository: RadarRepository
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        return@withContext try {
//            if (radarRepository.sync()) {
//                Result.success()
//            } else Result.retry()
//        } catch (e: Exception) {
//            Result.failure()
//        }
//    }
//}