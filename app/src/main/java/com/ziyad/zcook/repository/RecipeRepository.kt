package com.ziyad.zcook.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ziyad.zcook.model.MahasiswaKos
import com.ziyad.zcook.model.PersonalNote
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.model.Review
import com.ziyad.zcook.utils.recipeDummy
import java.util.*
import kotlin.collections.ArrayList

class RecipeRepository {
    private val database = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipeBelow10LiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipe10sLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val currentRecipe = MutableLiveData<Recipe>()

    init {
        allRecipeLiveData.value = arrayListOf()
        recipeBelow10LiveData.value = arrayListOf()
        recipe10sLiveData.value = arrayListOf()
        currentRecipe.value = Recipe()
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
        database.collection("recipes").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val allRecipe = arrayListOf<Recipe>()
                for (doc in snapshot) {
                    allRecipe.add(doc.toObject(Recipe::class.java))
                    Log.d("TEZ", "Current data: $doc")
                }
                allRecipeLiveData.postValue(allRecipe)
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }

        return allRecipeLiveData
    }

    private val _searchResult = MutableLiveData<ArrayList<Recipe>>()
    val searchResult: LiveData<ArrayList<Recipe>> = _searchResult

    suspend fun searchRecipe(query: String) {
        database.collection("recipes").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val allRecipe = arrayListOf<Recipe>()
                for (doc in snapshot) {
                    val mRecipe = doc.toObject(Recipe::class.java)
                    if (mRecipe.name.lowercase().contains(query.lowercase())) {
                        allRecipe.add(mRecipe)
                    }
                    Log.d("TEZ", "Current data: $doc")
                }
                _searchResult.postValue(allRecipe)
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }
    }

    suspend fun clearSearch() {
        _searchResult.postValue(arrayListOf())
    }

    private fun getAllRecipeByPriceRange(
        startPrice: Int,
        endPrice: Int
    ): MutableLiveData<ArrayList<Recipe>> {
        val allRecipeInRange = MutableLiveData<ArrayList<Recipe>>()
        allRecipeInRange.postValue(arrayListOf())
        database.collection("recipes").whereGreaterThanOrEqualTo("estimatedPrice", startPrice)
            .whereLessThanOrEqualTo("estimatedPrice", endPrice).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TEZ", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val allRecipe = arrayListOf<Recipe>()
                    for (doc in snapshot) {
                        allRecipe.add(doc.toObject(Recipe::class.java))
                        Log.d("TEZ", "Current data: $doc")
                    }
                    allRecipeInRange.postValue(allRecipe)
                } else {
                    Log.d("TEZ", "Current data: null")
                }
            }

        return allRecipeInRange
    }

    fun getRecipeBelow10() = getAllRecipeByPriceRange(0, 9999)

    fun getRecipe10s() = getAllRecipeByPriceRange(10000, 19999)

    suspend fun addRatingAndReview(
        recipeId: String,
        rating: Double,
        review: String
    ): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")

        currentUser?.let {
            database.collection("recipes").document(recipeId).get()
                .addOnSuccessListener { snapshot ->
                    val mReview = Review(currentUser.uid, currentUser.displayName!!, rating, review)
                    if (snapshot != null && snapshot.exists()) {
                        val mRecipe = snapshot.toObject(Recipe::class.java)!!

                        Log.d("TEZ", "Current data: $mRecipe")
                        for (i in mRecipe.listReview) {
                            if (i.userId == currentUser.uid) {
                                mRecipe.listReview.remove(i)
                            }
                        }
                        mRecipe.listReview.add(mReview)
                        database.collection("recipes").document(recipeId).set(mRecipe)
                            .addOnSuccessListener {
                                statusLiveData.postValue("SUCCESS")
                                Log.d("TEZ", "SUCCESS")
                            }.addOnFailureListener {
                                statusLiveData.postValue(it.toString())
                            }
                    } else {
                        Log.d("TEZ", "Current data: null")
                        statusLiveData.postValue("Null")
                    }
                }
        }

        return statusLiveData
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
            .addOnCompleteListener {
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