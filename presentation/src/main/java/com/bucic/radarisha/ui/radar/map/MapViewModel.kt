package com.bucic.radarisha.ui.radar.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.usecases.radar.DeleteRadarUseCase
import com.bucic.domain.usecases.radar.GetRadarsUseCase
import com.bucic.domain.usecases.radar.VoteReliabilityUseCase
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRadarsUseCase: GetRadarsUseCase,
    private val voteReliability: VoteReliabilityUseCase,
    private val deleteRadar: DeleteRadarUseCase
) : ViewModel() {

    // TODO: change to SharedFlow
    private val _radars = MutableStateFlow<Result<List<RadarEntity>>?>(null)
    val radars: StateFlow<Result<List<RadarEntity>>?> = _radars.asStateFlow()

    private val _voteStatusMessage = MutableSharedFlow<Result<String>?>(replay = 0)
    val voteStatusMessage: SharedFlow<Result<String>?> = _voteStatusMessage

    private val _dialogActionCompleted = MutableSharedFlow<Unit>()
    val dialogActionCompleted: SharedFlow<Unit> = _dialogActionCompleted.asSharedFlow()

    fun getRadars() = viewModelScope.launch {
        _radars.value = getRadarsUseCase.invoke()
    }

    fun vote(radarReliabilityVote: RadarReliabilityVoteEntity) = viewModelScope.launch {
        _voteStatusMessage.emit(voteReliability.invoke(radarReliabilityVote))
        _dialogActionCompleted.emit(Unit)

    }

    fun deleteRadar(radar: RadarEntity) = viewModelScope.launch {
        val result = deleteRadar.invoke(radar)
        _voteStatusMessage.emit(result)
        if (result is Result.Success) {
            val currentRadars = _radars.value
            if (currentRadars is Result.Success) {
                _radars.value = Result.Success(currentRadars.data.filter { it.uid != radar.uid })
            }
        }
        _dialogActionCompleted.emit(Unit)
    }
}