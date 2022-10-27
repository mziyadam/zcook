package com.ziyad.zcook.ui.auth.register

import androidx.lifecycle.ViewModel
import com.ziyad.zcook.repository.UserRepository

class RegisterViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    val currentUserLiveData = userRepository.currentUserLiveData

    suspend fun register(
        email: String,
        name: String,
        password: String
    ) = userRepository.register(email, name, password)
}