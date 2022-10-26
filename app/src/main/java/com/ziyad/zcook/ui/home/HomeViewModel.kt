package com.ziyad.zcook.ui.home

import androidx.lifecycle.ViewModel
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.repository.RecipeRepository
import com.ziyad.zcook.repository.UserRepository

class HomeViewModel : ViewModel() {
    private val recipeRepository = RecipeRepository.getInstance()
    private val userRepository = UserRepository.getInstance()

    val allRecipe = recipeRepository.getAllRecipe()

    val savedRecipe = userRepository.getSavedRecipe()

    val savedRecipeId = userRepository.getSavedRecipeId()

    suspend fun saveRecipe(recipeId: String) {
        userRepository.saveRecipe(recipeId)
    }

    suspend fun removeRecipeFromSaved(recipeId: String) {
        userRepository.removeRecipeFromSaved(recipeId)
    }
}