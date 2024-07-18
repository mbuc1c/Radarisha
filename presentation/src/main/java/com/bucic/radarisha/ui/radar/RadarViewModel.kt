package com.bucic.radarisha.ui.radar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.usecases.user.GetCurrentUserUseCase
import com.bucic.domain.usecases.user.RemoveCurrentUserUseCase
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val removeCurrentUserUseCase: RemoveCurrentUserUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Result<UserEntity>?>(null)
    val currentUser: StateFlow<Result<UserEntity>?> = _currentUser.asStateFlow()

    fun getCurrentUser() = viewModelScope.launch {
        _currentUser.value = getCurrentUserUseCase.invoke()
    }

    fun removeCurrentUser() = viewModelScope.launch {
        removeCurrentUserUseCase.invoke()
    }

    val userEntity: UserEntity?
        get() = (_currentUser.value as? Result.Success)?.data
}