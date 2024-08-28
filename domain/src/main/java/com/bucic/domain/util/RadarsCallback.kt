package com.bucic.domain.util

import com.bucic.domain.entities.RadarEntity

interface RadarsCallback {
    fun onSuccess(data: List<RadarEntity>)
    fun onError(message: String)
}