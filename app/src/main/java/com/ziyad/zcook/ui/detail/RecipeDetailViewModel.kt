package com.ziyad.zcook.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.repository.RecipeRepository
import com.ziyad.zcook.repository.UserRepository
import com.ziyad.zcook.utils.recipeDummy

class RecipeDetailViewModel: ViewModel() {
    private val recipeRepository = RecipeRepository.getInstance()
    private val userRepository = UserRepository.getInstance()

    val savedRecipeId = userRepository.getSavedRecipeId()
    val currentUserLiveData = userRepository.currentUserLiveData

    fun currentRecipe(recipeId:String)=recipeRepository.getRecipe(recipeId)

    suspend fun saveRecipe(recipeId: String){
        userRepository.saveRecipe(recipeId)
    }

    suspend fun removeRecipeFromSaved(recipeId: String){
        userRepository.removeRecipeFromSaved(recipeId)
    }
}