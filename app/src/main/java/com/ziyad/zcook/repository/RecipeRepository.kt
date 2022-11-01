package com.ziyad.zcook.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.model.Review
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
        if (query == "") {
            _searchResult.postValue(arrayListOf())
        } else {
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

                    Log.d("TEZA", "Current data: ${allRecipe}")
                } else {
                    Log.d("TEZ", "Current data: null")
                }
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
        /*val recipeNasiGoreng = Recipe(
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
        )*/
        /*val recipeFrenchFries = Recipe(
            randomId,
            "Kentang Goreng Restoran",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/french-fries.jpg?alt=media&token=a3f315ec-fa85-43cc-99e0-3fd13e3e7639",
            18000,
            "75 menit",
            "-Kentang 8 buah \n" +
                    "-Gula putih 3 sdm  \n" +
                    "-Sirup jagung 2 sdm",
            "± Rp 12.000 (1kg)\n" +
                    "± Rp 1.000 (24gr)\n" +
                    "± Rp 5.000 (100ml)",
            "1. Kupas dan potong kentang menjadi potongan kentang goreng setebal 1/4 inci\n" +
                    "2. Siapkan mangkuk untuk kentang dan biarkan terendam dalam air selama 15 menit, lalu keluarkan cairannya dan keringkan kentang.\n" +
                    "3. Sekarang rendam kentang dalam air mendidih secukupnya lalu tambahkan sirup jagung dan gula dan aduk semuanya. Lakukan ini dalam mangkuk logam. Masukkan semuanya ke dalam lemari es selama 10 menit. Keluarkan cairan dan keringkan kentang dengan tisu.\n" +
                    "4. Siapkan kotak makan dan taruh kentang goreng di atasnya, letakkan plastik penutup di atas piring dan masukkan semuanya ke dalam freezer selama 45 menit.\n" +
                    "5. Sekarang panaskan minyak untuk menggoreng sekitar 350 hingga 360 derajat dan setelah minyak panas mulai 1,3 kentang goreng dalam minyak selama 3 menit. Tempatkan kentang goreng di atas piring dengan handuk kertas untuk dikeringkan dan biarkan selama sekitar 10 menit. Terus bekerja dalam batch sampai semua kentang goreng selesai.\n" +
                    "6. Sekarang goreng kembali kentang goreng untuk kedua kalinya 1/3 setiap kali selama 6 menit setiap batch kemudian bumbui kentang goreng dengan sedikit garam.\n" +
                    "7. Nikmati.",
            arrayListOf()
        )*/
        /*val recipePearSalad = Recipe(
            randomId,
            "Salad Buah Pir",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/pear-salad.jpg?alt=media&token=b5e47622-093c-4c91-9a55-337eb0cd46db",
            12000,
            "3 menit",
            "-Daun selada 1 lembar \n" +
                    "-Buah pir 1 buah  \n" +
                    "-Mayones 1 sdm \n" +
                    "-Paprika bubuk ",
            "± Rp 3.000 (100gr) \n" +
                    "± Rp 3.000 \n" +
                    "± Rp 1.000 (10gr)\n" +
                    "± Rp 5.000 (20gr)" ,
            "1. Potong buah pir\n" +
                    "2. Susun daun selada di piring saji, diikuti dengan buah pir dan mayones. \n" +
                    "3. Nikmati dengan taburan paprika.",
            arrayListOf()
        )*/
        /*val recipe2DollarsSandwich = Recipe(
            randomId,
            "$2 Sandwich",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/2dollar-sandwhich.jpg?alt=media&token=a12ce24e-0075-46b9-b6ad-3b13ff031d17",
            9500,
            "5 menit",
            "-Margarin 2 sdt \n" +
                    "-Roti tawar 2 potong  \n" +
                    "-Gula putih 2 sdt",
            "± Rp 5.000 (200gr) \n" +
                    "± Rp 4.000\n" +
                    "± Rp 500 (16gr)" ,
            "1. Oleskan margarin pada kedua irisan roti secara merata dan taburi dengan gula.\n" +
                    "2. Nikmati.",
            arrayListOf()
        )*/
        /*val recipeILPancakes = Recipe(
            randomId,
            "I ♥ Pancakes\n",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/il-pancakes.jpg?alt=media&token=0a128097-8206-4aa5-88a3-0f7209b41b69",
            10000,
            "5 menit",
            "-Tepung beras 1 cup \n" +
                    "-Garam 1/2 sdt \n" +
                    "-Telur 1 butir \n" +
                    "-Minyak goreng 1 sdm \n" +
                    "-Air 1 sdm",
            "± Rp 6.000 (500gr) \n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 2.000\n" +
                    "± Rp 500 (1 sdm)\n" +
                    "± Rp 500 (1 gelas)" ,
            "1. Dalam mangkuk, campur tepung beras dan garam. \n" +
                    "2. Buat lubang di tengah campuran tepung. \n" +
                    "3. Tambahkan telur yang telah dikocok, minyak sayur dan air secukupnya ke dalam mangkuk dan aduk hingga adonan tercampur. \n" +
                    "4. Olesi wajan anti lengket dengan minyak goreng dan panaskan dengan api sedang. \n" +
                    "5. Tambahkan sekitar 1/4 cup dari campuran dan miringkan panci untuk menutupi bagian bawah dengan panekuk tipis. \n" +
                    "6. Masak sekitar 1 menit per sisi. \n" +
                    "7. Ulangi dengan sisa campuran.",
            arrayListOf()
        )*/
        //TODO ADD RECIPE
        /*database.collection("recipes")
                .document(randomId)
                .set(recipePearSalad)
                .addOnCompleteListener {
                    Log.w("TEZ", "added $it")
                }.addOnFailureListener {
                    Log.w("TEZ", "Error $it")
                }*/
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