package com.ziyad.zcook.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.model.Review
import com.ziyad.zcook.utils.recipeDummy

class RecipeRepository {
    //https://codingwithtashi.medium.com/mvvm-architecture-with-firebase-firestore-android-series-java-part-2-2-a527c45edb97
    private val database = FirebaseFirestore.getInstance()
    fun getAllRecipe(): MutableLiveData<ArrayList<Recipe>> {
        val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED
        for(i in 1..10){
            allRecipe.add(recipeDummy)
        }
        allRecipeLiveData.value = allRecipe
        return allRecipeLiveData
    }

    fun searchRecipe(query: String): MutableLiveData<ArrayList<Recipe>> {
        val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED
        allRecipeLiveData.value = allRecipe
        return allRecipeLiveData
    }

    fun getAllRecipeByPriceRange(
        startPrice: Int,
        endPrice: Int
    ): MutableLiveData<ArrayList<Recipe>> {
        val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED
        allRecipeLiveData.value = allRecipe
        return allRecipeLiveData
    }

    fun addRatingAndReview(rating: Double, review: String) {
        //TODO NOT YET IMPLEMENTED
    }

    companion object {
        @Volatile
        private var instance: RecipeRepository? = null
        fun getInstance(): RecipeRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    instance = RecipeRepository()
                }
                return instance as RecipeRepository
            }
        }
    }
}