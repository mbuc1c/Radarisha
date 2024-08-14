package com.bucic.radarisha.ui.radar.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.usecases.radar.DeleteRadarUseCase
import com.bucic.domain.usecases.radar.GetRadarsUseCase
import com.bucic.domain.usecases.radar.VoteReliabilityUseCase
import com.bucic.domain.util.RadarsCallback
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRadars: GetRadarsUseCase,
    private val voteReliability: VoteReliabilityUseCase,
    private val deleteRadar: DeleteRadarUseCase
) : ViewModel() {

//    private val _radars = MutableSharedFlow<Result<List<RadarEntity>>>()
//    val radars: SharedFlow<Result<List<RadarEntity>>> = _radars.asSharedFlow()

    val radars = MutableLiveData<Result<List<RadarEntity>>>()

    private val _dialogActionStatusMessage = MutableSharedFlow<Result<String>?>(replay = 0)
    val dialogActionStatusMessage: SharedFlow<Result<String>?> = _dialogActionStatusMessage

    private val _dialogActionCompleted = MutableSharedFlow<Unit>()
    val dialogActionCompleted: SharedFlow<Unit> = _dialogActionCompleted.asSharedFlow()

    init {
        getRadars.invoke(object : RadarsCallback {
            override fun onSuccess(data: List<RadarEntity>) {
                radars.postValue(Result.Success(data))
            }

            override fun onError(message: String) {
                radars.postValue(Result.Error(message))
            }

        })
    }

//    fun getRadars() = viewModelScope.launch {
//        _radars.emit(getRadars.invoke())
//    }

    fun deleteRadar(radar: RadarEntity) = viewModelScope.launch {
        _dialogActionStatusMessage.emit(deleteRadar.invoke(radar))
        _dialogActionCompleted.emit(Unit)
    }

    fun vote(radarReliabilityVote: RadarReliabilityVoteEntity) = viewModelScope.launch {
        _dialogActionStatusMessage.emit(voteReliability.invoke(radarReliabilityVote))
        _dialogActionCompleted.emit(Unit)

    }
}