package com.ziyad.zcook.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.repository.RecipeRepository
import com.ziyad.zcook.repository.UserRepository

class SearchViewModel : ViewModel() {
    private val recipeRepository = RecipeRepository.getInstance()
    private val userRepository = UserRepository.getInstance()

    val searchResult = recipeRepository.searchResult

    val savedRecipe = userRepository.getSavedRecipe()

    suspend fun searchRecipe(query: String) {
        recipeRepository.searchRecipe(query)
    }


    suspend fun saveRecipe(recipe: Recipe) {
        userRepository.saveRecipe(recipe)
    }

    suspend fun removeRecipeFromSaved(recipe: Recipe) {
        userRepository.removeRecipeFromSaved(recipe)
    }
}