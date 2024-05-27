package com.bucic.radarisha.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.usecases.user.GetUserByUsernameUseCase
import com.bucic.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUserByUsername: GetUserByUsernameUseCase
) : ViewModel() {

    fun getUserByUsername(
        username: String
    ) = viewModelScope.launch {
        val result = getUserByUsername.invoke(username)
        when (result) {
            is Result.Success -> Log.d("customTag", result.data.toString())
            else -> Log.e("customTag", "ERROR")
        }
    }
}