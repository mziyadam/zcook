package com.ziyad.zcook.ui.home.settings.change_password

import androidx.lifecycle.ViewModel
import com.ziyad.zcook.repository.UserRepository

class ChangePasswordViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    suspend fun changePassword(oldPassword: String, newPassword: String) =
        userRepository.changePassword(oldPassword, newPassword)
}