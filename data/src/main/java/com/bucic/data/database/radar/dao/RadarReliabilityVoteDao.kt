package com.bucic.data.database.radar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bucic.data.entities.radar.RadarReliabilityVoteDbData

@Dao
interface RadarReliabilityVoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(radarReliabilityVote: RadarReliabilityVoteDbData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(radarReliabilityVotes: List<RadarReliabilityVoteDbData>)

    @Query("SELECT * FROM radar_reliability_votes")
    suspend fun getAllRadarReliabilityVotes(): List<RadarReliabilityVoteDbData>

    @Query("SELECT * FROM radar_reliability_votes WHERE radarUid = :radarUid")
    suspend fun getRadarReliabilityVotesByRadarUid(radarUid: String): List<RadarReliabilityVoteDbData>

    @Query("DELETE FROM radar_reliability_votes")
    suspend fun deleteAll()
}