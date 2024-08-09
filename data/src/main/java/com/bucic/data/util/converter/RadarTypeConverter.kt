package com.bucic.data.util.converter

import androidx.room.TypeConverter
import com.bucic.domain.util.RadarType

class RadarTypeConverter {
    @TypeConverter
    fun fromString(value: String?): RadarType? {
        return value?.let { RadarType.valueOf(it) }
    }

    @TypeConverter
    fun radarTypeToString(radarType: RadarType?): String? {
        return radarType?.name
    }
}