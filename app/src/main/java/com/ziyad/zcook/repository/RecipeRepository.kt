package com.ziyad.zcook.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.model.Review
import com.ziyad.zcook.utils.recipeDummy

class RecipeRepository {
    //https://codingwithtashi.medium.com/mvvm-architecture-with-firebase-firestore-android-series-java-part-2-2-a527c45edb97
    private val database = FirebaseFirestore.getInstance()
    private val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipeBelow10LiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipe10sLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val currentRecipe = MutableLiveData<Recipe>()
    init {
        allRecipeLiveData.value= arrayListOf()
        recipeBelow10LiveData.value= arrayListOf()
        recipe10sLiveData.value= arrayListOf()
    }
    fun getRecipe(recipeId:String): MutableLiveData<Recipe>{
        //TODO RECIPE
        val dummyRecipe= recipeDummy

        currentRecipe.postValue(dummyRecipe)
        return currentRecipe
    }

    fun getAllRecipe(): MutableLiveData<ArrayList<Recipe>> {
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED
        for(i in 1..10){
            allRecipe.add(recipeDummy)
        }
        allRecipeLiveData.postValue(allRecipe)

        return allRecipeLiveData
    }

    private val _searchResult = MutableLiveData<ArrayList<Recipe>>()
    val searchResult: LiveData<ArrayList<Recipe>> = _searchResult
    suspend fun searchRecipe(query: String) {
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED
        for(i in 1..10){
            allRecipe.add(recipeDummy)
        }
        _searchResult.postValue(allRecipe)
    }

    fun getAllRecipeByPriceRange(
        startPrice: Int,
        endPrice: Int
    ): ArrayList<Recipe> {
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED

        return allRecipe
    }

    fun getRecipeBelow10(): MutableLiveData<ArrayList<Recipe>>{
        recipeBelow10LiveData.postValue(getAllRecipeByPriceRange(0,10000))

        return recipeBelow10LiveData
    }

    fun getRecipe10s(): MutableLiveData<ArrayList<Recipe>>{
        recipe10sLiveData.postValue(getAllRecipeByPriceRange(10000,19999))

        return recipe10sLiveData
    }

    suspend fun addRatingAndReview(rating: Double, review: String) {
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