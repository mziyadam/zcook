package com.ziyad.zcook.ui.home

import androidx.lifecycle.ViewModel
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.repository.RecipeRepository
import com.ziyad.zcook.repository.UserRepository

class HomeViewModel : ViewModel() {
    private val recipeRepository = RecipeRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    val allRecipe = recipeRepository.getAllRecipe()
    val recipe10s = recipeRepository.getRecipe10s()
    val recipeBelow10 = recipeRepository.getRecipeBelow10()
    val savedRecipe = userRepository.getSavedRecipe()
    val savedRecipeId = userRepository.getSavedRecipeId()
    val currentUserLiveData = userRepository.currentUserLiveData
    val searchResult = recipeRepository.searchResult

    suspend fun searchRecipe(query: String) {
        recipeRepository.searchRecipe(query)
//    recipeRepository.injectData()
    }

    suspend fun clearSearch() {
        recipeRepository.clearSearch()
    }

    suspend fun saveRecipe(recipeId: String)=userRepository.saveRecipe(recipeId)

    suspend fun removeRecipeFromSaved(recipeId: String)=userRepository.removeRecipeFromSaved(recipeId)

    suspend fun logout() = userRepository.logout()
}