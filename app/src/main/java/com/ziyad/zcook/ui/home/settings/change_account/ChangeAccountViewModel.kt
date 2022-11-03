package com.ziyad.zcook.ui.home.settings.change_account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ziyad.zcook.repository.UserRepository

class ChangeAccountViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()

    val currentUserLiveData = userRepository.currentUserLiveData
    suspend fun changeEmailAndName(email: String, name: String) =
        userRepository.changeEmailAndName(email, name)
}