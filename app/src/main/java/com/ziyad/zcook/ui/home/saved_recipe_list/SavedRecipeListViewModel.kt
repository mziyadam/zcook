package com.ziyad.zcook.ui.home.saved_recipe_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ziyad.zcook.repository.UserRepository

class SavedRecipeListViewModel : ViewModel() {

    val savedRecipe = UserRepository.getInstance().getSavedRecipe()
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}