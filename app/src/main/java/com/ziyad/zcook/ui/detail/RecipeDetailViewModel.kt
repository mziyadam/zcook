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

    val savedRecipe = userRepository.getSavedRecipe()

    fun currentRecipe(recipeId:String)=recipeRepository.getRecipe(recipeId)

    suspend fun saveRecipe(recipe: Recipe){
        userRepository.saveRecipe(recipe)
    }

    suspend fun removeRecipeFromSaved(recipe: Recipe){
        userRepository.removeRecipeFromSaved(recipe)
    }
}