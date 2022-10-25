package com.ziyad.zcook.ui.home

import androidx.lifecycle.ViewModel
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.repository.RecipeRepository
import com.ziyad.zcook.repository.UserRepository

class HomeViewModel: ViewModel() {
    val allRecipe = RecipeRepository.getInstance().getAllRecipe()
    val savedRecipe = UserRepository.getInstance().getSavedRecipe()
    suspend fun saveRecipe(recipe: Recipe){
        UserRepository.getInstance().saveRecipe(recipe)
    }
}