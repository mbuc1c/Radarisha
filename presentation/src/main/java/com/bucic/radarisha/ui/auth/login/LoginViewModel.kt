package com.bucic.radarisha.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.usecases.user.GetUserByUsernameAndPasswordUseCase
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUserByUsernameAndPassword: GetUserByUsernameAndPasswordUseCase
) : ViewModel() {

    private val _userResult = MutableStateFlow<Result<UserEntity>?>(null)
    val userResult: StateFlow<Result<UserEntity>?> = _userResult.asStateFlow()

    fun getUserByUsernameAndPassword(
        username: String,
        password: String
    ) = viewModelScope.launch {
        _userResult.value = getUserByUsernameAndPassword.invoke(username, password)
    }
}