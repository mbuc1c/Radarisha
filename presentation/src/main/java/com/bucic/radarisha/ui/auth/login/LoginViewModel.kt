package com.bucic.radarisha.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.usecases.user.GetCurrentUserUseCase
import com.bucic.domain.usecases.user.GetUserByUsernameAndPasswordUseCase
import com.bucic.domain.usecases.user.RemoveCurrentUserUseCase
import com.bucic.domain.usecases.user.SaveCurrentUserUseCase
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUserByUsernameAndPasswordUseCase: GetUserByUsernameAndPasswordUseCase,
    private val saveCurrentUserUseCase: SaveCurrentUserUseCase,
    private val removeCurrentUserUseCase: RemoveCurrentUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Result<UserEntity>?>(null)
    val currentUser: StateFlow<Result<UserEntity>?> = _currentUser.asStateFlow()

    private val _userResult = MutableStateFlow<Result<UserEntity>?>(null)
    val userResult: StateFlow<Result<UserEntity>?> = _userResult.asStateFlow()

    fun getUserByUsernameAndPassword(
        username: String,
        password: String
    ) = viewModelScope.launch {
        _userResult.value = getUserByUsernameAndPasswordUseCase.invoke(username, password)
    }

    fun saveCurrentUser(user: UserEntity, stayLoggedIn: Boolean) = viewModelScope.launch {
        saveCurrentUserUseCase.invoke(user, stayLoggedIn)
    }

    fun removeCurrentUser() = viewModelScope.launch {
        removeCurrentUserUseCase.invoke()
    }

    fun resetResultStateFlow() {
        _userResult.value = null
    }

    fun getCurrentUser() = viewModelScope.launch {
        _currentUser.value = getCurrentUserUseCase.invoke()
    }
}