package com.ziyad.zcook.ui.auth.login

import androidx.lifecycle.ViewModel
import com.ziyad.zcook.repository.UserRepository

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    val currentUserLiveData = userRepository.currentUserLiveData

    suspend fun login(email: String, password: String) = userRepository.login(email, password)
}