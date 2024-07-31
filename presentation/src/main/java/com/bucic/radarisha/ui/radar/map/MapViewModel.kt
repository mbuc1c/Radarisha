package com.bucic.radarisha.ui.radar.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
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
    private val voteReliability: VoteReliabilityUseCase
) : ViewModel() {

    private val _radars = MutableStateFlow<Result<List<RadarEntity>>?>(null)
    val radars: StateFlow<Result<List<RadarEntity>>?> = _radars.asStateFlow()

    private val _voteStatusMessage = MutableSharedFlow<Result<String>?>(replay = 0)
    val voteStatusMessage: SharedFlow<Result<String>?> = _voteStatusMessage

    // SharedFlow to notify about vote completion
    private val _voteCompleted = MutableSharedFlow<Unit>()
    val voteCompleted: SharedFlow<Unit> = _voteCompleted.asSharedFlow()

    fun getRadars() = viewModelScope.launch {
        _radars.value = getRadarsUseCase.invoke()
    }

    fun vote(radarReliabilityVote: RadarReliabilityVoteEntity) = viewModelScope.launch {
        _voteStatusMessage.emit(voteReliability.invoke(radarReliabilityVote))
        _voteCompleted.emit(Unit)

    }
}