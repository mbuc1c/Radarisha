package com.bucic.data.database.radar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bucic.data.entities.radar.RadarDbData

@Dao
interface RadarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(radar: RadarDbData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(radars: List<RadarDbData>)

    @Query("SELECT * FROM radars")
    suspend fun getAllRadars(): List<RadarDbData>

    @Query("DELETE FROM radars")
    suspend fun deleteAll()
}