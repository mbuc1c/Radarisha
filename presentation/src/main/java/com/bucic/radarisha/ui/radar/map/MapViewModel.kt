package com.bucic.radarisha.ui.radar.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.usecases.radar.GetRadarsUseCase
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRadarsUseCase: GetRadarsUseCase
) : ViewModel() {

    private val _radars = MutableStateFlow<Result<List<RadarEntity>>?>(null)
    val radars: StateFlow<Result<List<RadarEntity>>?> = _radars.asStateFlow()

    fun getRadars() = viewModelScope.launch {
        _radars.value = getRadarsUseCase.invoke()
    }

}