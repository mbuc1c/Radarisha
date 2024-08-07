package com.bucic.radarisha.ui.radar.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.usecases.radar.CreateRadarUseCase
import com.bucic.domain.usecases.radar.GetRadarByUidUseCase
import com.bucic.domain.usecases.radar.UpdateRadarUseCase
import com.bucic.domain.util.RadarType
import com.bucic.domain.util.Result
import com.bucic.radarisha.R
import com.bucic.radarisha.util.NonFilterArrayAdapter
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarCreateViewModel @Inject constructor(
    application: Application,
    private val createRadar: CreateRadarUseCase,
    private val getRadar: GetRadarByUidUseCase,
    private val updateRadar: UpdateRadarUseCase
) : AndroidViewModel(application) {

    var selectedRadarType: String? = null
    var selectedSpeed: String? = null
    var currentAddress: String? = null
    var radarLocation: LatLng? = null
    var currentLocationFetched: Boolean = false
    var radarDataFetched: Boolean = false

    private val _radarForUpdate = MutableSharedFlow<Result<RadarEntity>?>(replay = 0)
    val radarForUpdate: SharedFlow<Result<RadarEntity>?> = _radarForUpdate

    val radarTypeAdapter: NonFilterArrayAdapter<String>
    val speedAdapter: NonFilterArrayAdapter<String>

    private val _createRadarStatusMessage = MutableSharedFlow<Result<String>?>(replay = 0)
    val createRadarStatusMessage: SharedFlow<Result<String>?> = _createRadarStatusMessage

    init {
        val radarTypesEnum = RadarType.entries.map { it.display }
        radarTypeAdapter = NonFilterArrayAdapter(
            application,
            R.layout.dropdown_item,
            radarTypesEnum
        )

        val speedValues = application.resources.getIntArray(R.array.radar_speed)
        speedAdapter = NonFilterArrayAdapter(
            application,
            R.layout.dropdown_item,
            speedValues.map { it.toString() })
    }

    fun createRadar(radar: RadarEntity) = viewModelScope.launch {
        _createRadarStatusMessage.emit(createRadar.invoke(radar))
    }

    fun updateRadar(radar: RadarEntity) = viewModelScope.launch {
        _createRadarStatusMessage.emit(updateRadar.invoke(radar))
    }

    fun getRadar(radarUid: String) = viewModelScope.launch {
        _radarForUpdate.emit(getRadar.invoke(radarUid))
    }
}