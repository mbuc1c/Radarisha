package com.bucic.radarisha.ui.radar.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.usecases.radar.CreateRadarUseCase
import com.bucic.domain.util.RadarType
import com.bucic.radarisha.R
import com.bucic.radarisha.util.NonFilterArrayAdapter
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarCreateViewModel @Inject constructor(
    application: Application,
    private val createRadar: CreateRadarUseCase
) : AndroidViewModel(application) {

    var selectedRadarType: String? = null
    var selectedSpeed: String? = null
    var currentAddress: String? = null
    var newRadarLocation: LatLng? = null
    var currentLocationFetched: Boolean = false

    val radarTypeAdapter: NonFilterArrayAdapter<String>
    val speedAdapter: NonFilterArrayAdapter<String>

    init {
        val radarTypesEnum = RadarType.entries.map { it.displayName }
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
        createRadar.invoke(radar)
    }
}