package com.bucic.radarisha.ui.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.usecases.user.CreateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val createUser: CreateUserUseCase
) : ViewModel() {

    fun createUser(
        username: String,
        password: String
    ) = viewModelScope.launch {
        createUser.invoke(
            UserEntity(
                uid = "Placeholder",
                username = username,
                password = password,
                stayLoggedIn = null
            )
        )
    }
}