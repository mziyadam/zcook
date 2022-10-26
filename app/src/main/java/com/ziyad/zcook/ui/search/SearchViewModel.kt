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

    val savedRecipeId = userRepository.getSavedRecipeId()

    fun searchRecipe(query: String) {
        recipeRepository.searchRecipe(query)
//    recipeRepository.injectData()
    }

    fun clearRecipe(){
        recipeRepository.clearSearch()
    }

    suspend fun saveRecipe(recipeId: String) {
        userRepository.saveRecipe(recipeId)
    }

    suspend fun removeRecipeFromSaved(recipeId: String) {
        userRepository.removeRecipeFromSaved(recipeId)
    }
}