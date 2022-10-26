package com.ziyad.zcook.ui.auth.reset_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ziyad.zcook.repository.UserRepository

class ResetPasswordViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    suspend fun resetPassword(email: String) = userRepository.resetPassword(email)
}