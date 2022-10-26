package com.ziyad.zcook.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.model.Review
import com.ziyad.zcook.utils.recipeDummy
import java.util.*
import kotlin.collections.ArrayList

class RecipeRepository {
    //https://codingwithtashi.medium.com/mvvm-architecture-with-firebase-firestore-android-series-java-part-2-2-a527c45edb97
    private val database = FirebaseFirestore.getInstance()
    private val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipeBelow10LiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipe10sLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val currentRecipe = MutableLiveData<Recipe>()

    init {
        allRecipeLiveData.value = arrayListOf()
        recipeBelow10LiveData.value = arrayListOf()
        recipe10sLiveData.value = arrayListOf()
    }

    fun getRecipe(recipeId: String): MutableLiveData<Recipe> {
        database.collection("recipes").document(recipeId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val mRecipe = snapshot.toObject(Recipe::class.java)!!
                Log.d("TEZ", "Current data: $mRecipe")
                currentRecipe.postValue(mRecipe)
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }
        return currentRecipe
    }

    fun getAllRecipe(): MutableLiveData<ArrayList<Recipe>> {
        val allRecipe = arrayListOf<Recipe>()
        database.collection("recipes").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                for (doc in snapshot) {
                    allRecipe.add(doc.toObject(Recipe::class.java))
                    Log.d("TEZ", "Current data: $doc")
                }
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }
        allRecipeLiveData.postValue(allRecipe)

        return allRecipeLiveData
    }

    private val _searchResult = MutableLiveData<ArrayList<Recipe>>()
    val searchResult: LiveData<ArrayList<Recipe>> = _searchResult
    fun searchRecipe(query: String) {
        val allRecipe = arrayListOf<Recipe>()
        database.collection("recipes")
            .orderBy("name")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                for (doc in snapshot) {
                    allRecipe.add(doc.toObject(Recipe::class.java))
//                    Log.d("TEZ", "Current data: $doc")
                }
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }

        _searchResult.postValue(allRecipe)
    }

    fun clearSearch(){
        _searchResult.postValue(arrayListOf())
    }

    fun getAllRecipeByPriceRange(
        startPrice: Int,
        endPrice: Int
    ): ArrayList<Recipe> {
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED

        return allRecipe
    }

    fun getRecipeBelow10(): MutableLiveData<ArrayList<Recipe>> {
        recipeBelow10LiveData.postValue(getAllRecipeByPriceRange(0, 10000))

        return recipeBelow10LiveData
    }

    fun getRecipe10s(): MutableLiveData<ArrayList<Recipe>> {
        recipe10sLiveData.postValue(getAllRecipeByPriceRange(10000, 19999))

        return recipe10sLiveData
    }

    suspend fun addRatingAndReview(rating: Double, review: String) {
        //TODO NOT YET IMPLEMENTED
    }

    suspend fun injectData() {
        val randomId = UUID.randomUUID().toString()
        val mRecipe = Recipe(
            randomId,
            "Nasi Goreng Spesial",
            "https://img.okezone.com/content/2022/08/11/298/2646282/resep-nasi-goreng-rendah-kalori-cocok-untuk-diet-D8kxJ8GTcT.jpg",
            15500,
            "20 menit",
            "-Nasi putih 1 piring \n" +
                    "-Bawang putih 2 siung  \n" +
                    "-Kecap manis 1 sdm \n" +
                    "-Saus sambal 1 sdm \n" +
                    "-Saus tiram 1 sdm \n" +
                    "-Garam 1 sdt \n" +
                    "-Penyedap rasa 1 sdt       \n" +
                    "-Daun bawang 1 batang     \n" +
                    "-Telur ayam 1 butir\n" +
                    "-Sosis ayam 1 buah, iris \n" +
                    "-Minyak goreng 3 sdm  ",
            "± Rp 3.000\n" +
                    "± Rp 500\n" +
                    "± Rp 1.000 (20ml)\n" +
                    "± Rp 500 (10gr)\n" +
                    "± Rp 3.000 (23ml)\n" +
                    "± Rp 2.000 (200gr)\n" +
                    "± Rp 500 (8gr)\n" +
                    "± Rp 1.000 (80gr)\n" +
                    "± Rp 2.000\n" +
                    "± Rp 1.000\n" +
                    "± Rp 1000 (3 sdm)",
            "1. Siapkan penggorengan dengan api sedang, tuang margarin atau minyak goreng.\n" +
                    "2. Masukkan bawang putih dan daun bawang yang sudah dicincang halus. Tumis hingga berbau harum atau hingga warnanya keemasan.\n" +
                    "3. Masukkan sosis dan 1 butir telur ayam. Tumis sebentar.\n" +
                    "4. Masukkan bumbu halus dan nasi. Aduk hingga tercampur rata.\n" +
                    "5. Tuang kecap manis, saus sambal, saus tiram, garam, dan kaldu bubuk. Aduk hingga warna nasi berubah secara merata.\n" +
                    "6. Nasi goreng biasa yang sederhana, dan enak siap disajikan.",
            arrayListOf()
        )
        database.collection("recipes")
            .document(randomId)
            .set(mRecipe)
            .addOnSuccessListener {
                Log.w("TEZ", "added $it")
            }.addOnFailureListener {
                Log.w("TEZ", "Error $it")
            }
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