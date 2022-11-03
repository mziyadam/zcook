package com.ziyad.zcook.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.repository.RecipeRepository
import com.ziyad.zcook.repository.UserRepository
import com.ziyad.zcook.utils.recipeDummy

class RecipeDetailViewModel : ViewModel() {
    private val recipeRepository = RecipeRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    private var _star=MutableLiveData<Double>()
    val star :LiveData<Double> =_star
    val savedRecipeId = userRepository.getSavedRecipeId()
    val currentUserLiveData = userRepository.currentUserLiveData

    suspend fun setStar(star:Double){
        _star.postValue(star)
    }

    fun currentRecipe(recipeId: String) = recipeRepository.getRecipe(recipeId)

    suspend fun saveRecipe(recipeId: String) {
        userRepository.saveRecipe(recipeId)
    }

    suspend fun removeRecipeFromSaved(recipeId: String) {
        userRepository.removeRecipeFromSaved(recipeId)
    }

    suspend fun addRatingAndReview(recipeId: String, rating: Double, review: String) =
        recipeRepository.addRatingAndReview(recipeId, rating, review)

    fun getPersonalNote(recipeId: String)=userRepository.getPersonalNote(recipeId)

    suspend fun addPersonalNote(recipeId: String,note:String)=userRepository.addPersonalNote(recipeId,note)
}