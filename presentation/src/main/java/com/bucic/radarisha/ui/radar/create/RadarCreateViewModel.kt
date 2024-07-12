package com.bucic.radarisha.ui.radar.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.usecases.radar.CreateRadarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarCreateViewModel @Inject constructor(
    private val createRadar: CreateRadarUseCase
) : ViewModel() {

    fun createRadar(radar: RadarEntity) = viewModelScope.launch {
        createRadar.invoke(radar)
    }
}