package com.ziyad.zcook.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.model.Review

class RecipeRepository {
    //https://codingwithtashi.medium.com/mvvm-architecture-with-firebase-firestore-android-series-java-part-2-2-a527c45edb97
    private val database = FirebaseFirestore.getInstance()
    fun getAllRecipe(): MutableLiveData<ArrayList<Recipe>> {
        val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
        val allRecipe = arrayListOf<Recipe>()
        //TODO NOT YET IMPLEMENTED
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

    val recipeDummy = Recipe(
        "abcde",
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
}