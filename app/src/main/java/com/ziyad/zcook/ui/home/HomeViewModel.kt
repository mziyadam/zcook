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

    suspend fun saveRecipe(recipe: Recipe) {
        userRepository.saveRecipe(recipe)
    }

    suspend fun removeRecipeFromSaved(recipe: Recipe) {
        userRepository.removeRecipeFromSaved(recipe)
    }
}