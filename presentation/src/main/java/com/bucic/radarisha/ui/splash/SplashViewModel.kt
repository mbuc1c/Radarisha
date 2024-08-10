package com.bucic.radarisha.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.usecases.user.GetCurrentUserUseCase
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _currentUser = MutableStateFlow<Result<UserEntity>?>(null)
    val currentUser: StateFlow<Result<UserEntity>?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun getCurrentUser() = viewModelScope.launch {
        val result = getCurrentUserUseCase.invoke()
        _currentUser.value = result
        when (result) {
            is Result.Success -> {
                _isLoggedIn.value = true
            }

            is Result.Error -> {
                _isLoggedIn.value = false
            }
        }
    }
}