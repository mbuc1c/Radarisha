package com.bucic.data.database.radar

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bucic.data.database.radar.dao.RadarDao
import com.bucic.data.database.radar.dao.RadarReliabilityVoteDao
import com.bucic.data.entities.radar.RadarDbData
import com.bucic.data.entities.radar.RadarReliabilityVoteDbData
import com.bucic.data.util.converter.DateConverter
import com.bucic.data.util.converter.RadarTypeConverter

@Database(
    entities = [RadarDbData::class, RadarReliabilityVoteDbData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, RadarTypeConverter::class)
abstract class RadarDatabase : RoomDatabase() {
    abstract fun radarDao(): RadarDao
    abstract fun radarReliabilityVoteDao(): RadarReliabilityVoteDao
}