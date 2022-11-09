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
    private val auth = Firebase.auth
    private val database = FirebaseFirestore.getInstance()
    private val allRecipeLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipeBelow10LiveData = MutableLiveData<ArrayList<Recipe>>()
    private val recipe10sLiveData = MutableLiveData<ArrayList<Recipe>>()
    private val currentRecipe = MutableLiveData<Recipe>()
    private val _searchResult = MutableLiveData<ArrayList<Recipe>>()
    val searchResult: LiveData<ArrayList<Recipe>> = _searchResult

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
        )
        val recipeFrenchFries = Recipe(
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
        )
        val recipePearSalad = Recipe(
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
        )
        val recipe2DollarsSandwich = Recipe(
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
        )
        val recipeILPancakes = Recipe(
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
        )

        val tropicalCoconutPancakes = Recipe(
            randomId,
            "Tropical Coconut Pancakes",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/tropical-coconut-pancakes.jpg?alt=media&token=c79194dd-9788-4c5b-a179-59e8a7021ace",
            19500,
            "10 menit",
            "-Telur 2 butir\n" +
                    "-Tepung kelapa 4 sdm\n" +
                    "-Minyak kelapa 2 sdm\n" +
                    "-Pemanis bubuk 1 1/8 sdm\n" +
                    "-Ekstrak vanilla 1/2 sdt\n" +
                    "-Minyak kelapa 1 sdt",
            "± Rp 4.000\n" +
                    "± Rp 5.000 (100gr)\n" +
                    "± Rp 1.000 (25ml)\n" +
                    "± Rp 1.000 \n" +
                    "± Rp 7.500 (60ml)\n" +
                    "± Rp 1.000 (25ml)" ,
            "1. Lelehkan 2 sdm minyak kelapa.\n" +
                    "2. Dalam mangkuk, tambahkan telur, tepung kelapa, 2 sdm minyak kelapa yang sudah dilelehkan, pemanis stevia, dan ekstrak vanila, lalu kocok hingga rata. \n" +
                    "3. Dalam wajan besar, lelehkan 1 sdt minyak kelapa dengan api sedang. \n" +
                    "4. Tambahkan campuran yang cukup ke dalam wajan dan masak selama sekitar 2-3 menit per sisi.",
            arrayListOf()
        )
        val bakwanJagung = Recipe(
            randomId,
            "Bakwan Jagung",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/bakwan-jagung.jpg?alt=media&token=8fc0fad9-bbc1-480e-86f2-91276a6cd4ec",
            22000,
            "35 menit",
            "-Tepung serbaguna 2 cup\n" +
                    "-Bubuk kari 1 sdt\n" +
                    "-Telur 1 butir\n" +
                    "-Susu kedelai 1/2 cup\n" +
                    "-Garam 1/2 sdt\n" +
                    "-Jagung 1 buah\n" +
                    "-Minyak goreng 220ml",
            "± Rp 6.000 (240gr)\n" +
                    "± Rp 2.000 (12,5gr)\n" +
                    "± Rp 2.000 \n" +
                    "± Rp 3.000 (200ml)\n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 4.000\n" +
                    "± Rp 4.000" ,
            "1. Potong jagung kecil-kecil.\n" +
                    "2. Siapkan mangkuk, ayak: kari dan tepung.\n" +
                    "3. Sekarang perlahan tambahkan garam, susu, dan telur Anda.\n" +
                    "4. Kocok campuran sampai halus selama 20 menit.\n" +
                    "5. Sekarang tambahkan minyak Anda ke dalam wajan dan panaskan.\n" +
                    "6. Setelah minyak panas, goreng sesendok besar campuran Anda selama 3 menit setiap sisinya.\n" +
                    "7. Nikmati.",
            arrayListOf()
        )
        val cakwe = Recipe(
            randomId,
            "Cakwe",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/cakwe.jpg?alt=media&token=47e623dd-3e43-4f2d-8474-4b19d5578317",
            21000,
            "35 menit",
            "-Telur 3 butir\n" +
                    "-Tepung serbaguna 1,5 cup\n" +
                    "-Garam 1 sdt\n" +
                    "-Minyak goreng 2 cup",
            "± Rp 6.000 \n" +
                    "± Rp 6.000 \n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 8.000" ,
            "1. Kocok telur dalam mangkuk lalu masukkan tepung dan garam secara perlahan.\n" +
                    "2. Aduk campuran menjadi adonan lalu ratakan adonan menjadi 1/8 inci.\n" +
                    "3. Sekarang iris semuanya menjadi strip.\n" +
                    "4. Masukkan 2 cup minyak ke dalam wajan dan panaskan.\n" +
                    "5. Setelah minyak panas, mulailah menggoreng adonan Anda secara bertahap sampai berwarna keemasan di semua sisi.\n" +
                    "6. Nikmati.",
            arrayListOf()
        )
        val belgianWaffles101 = Recipe(
            randomId,
            "Belgian Waffles 101",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/belgian-waffles-101.jpg?alt=media&token=5997663a-9aa8-4fd8-9996-09c84226214e",
            24500,
            "50 menit",
            "-Tepung terigu 2 cup\n" +
                    "-Baking powder 4 sdt\n" +
                    "-Garam 1/2 sdt\n" +
                    "-Gula pasir 1/4 cup\n" +
                    "-Telur 2 butir\n" +
                    "-Minyak sayur 1/2 cup\n" +
                    "-Susu putih 2 cup\n" +
                    "-Vanili 1 sdt",
            "± Rp 5.000 (250gr)\n" +
                    "± Rp 5.000 (45gr)\n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 1.000 (4 saset 8gr)\n" +
                    "± Rp 4.000 \n" +
                    "± Rp 4.000 (220ml)\n" +
                    "± Rp 10.000 (500ml)\n" +
                    "± Rp 3.000 (6gr)" ,
            "1. Atur wajan wafel ke api sedang-tinggi dan olesi minyak sedikit.\n" +
                    "2. Di dalam mangkuk, tambahkan tepung, gula, baking powder, dan garam, lalu aduk rata.\n" +
                    "3. Sekarang, saring campuran tepung ke dalam mangkuk lain.\n" +
                    "4. Dalam mangkuk lain, tambahkan minyak, susu, kuning telur dan vanili, lalu kocok hingga tercampur rata.\n" +
                    "5. Tambahkan campuran tepung dan aduk hingga tercampur rata.\n" +
                    "6. Dalam mangkuk lain lagi, tambahkan putih telur dan kocok hingga kaku.\n" +
                    "7. Perlahan, masukkan putih telur kocok ke dalam campuran tepung.\n" +
                    "8. Tambahkan jumlah campuran yang diinginkan ke dalam wajan wafel dan masak selama sekitar 6-10 menit.\n" +
                    "9. Ulangi dengan sisa campuran.\n" +
                    "10. Nikmati selagi hangat.",
            arrayListOf()
        )
        val creamOnMarmaladeSandwiches = Recipe(
            randomId,
            "Cream on Marmalade Sandwiches",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/cream-on-marmalade-sandwich.jpg?alt=media&token=ec316620-018c-4190-be0d-b1af5e501d29",
            23000,
            "25 menit",
            "-Krim keju 2 sdm\n" +
                    "-Selai jeruk 4 sdm\n" +
                    "-Roti tawar 16 potong",
            "± Rp 12.000 (160gr)\n" +
                    "± Rp 6.000 (56gr)\n" +
                    "± Rp 5.000 (150gr)" ,
            "1. Siapkan mangkuk pencampur: Krim di dalamnya selai jeruk dengan keju krim sampai menjadi mulus.\n" +
                    "2. Letakkan di piring saji. Sendok campuran selai jeruk di atas irisan roti.\n" +
                    "3. Sajikan sandwich selai jeruk Anda segera.\n" +
                    "4. Nikmati.",
            arrayListOf()
        )
        */
        val addList = arrayListOf<Recipe>()
        val nasiGorengBumbuRacik = Recipe(
            UUID.randomUUID().toString(),
            "Nasi Goreng Bumbu Racik",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/nasi-goreng-bumbu-racik.jpg?alt=media&token=945dc227-ab63-4b54-af50-16c2e7567d4d",
            9000,
            "10 menit",
            "-Nasi putih 1 piring\n" +
                    "-Bumbu racik nasgor 1 sdm\n" +
                    "-Telur 1 butir\n" +
                    "-Minyak goreng 3 sdm",
            "± Rp 3.000 \n" +
                    "± Rp 3.000 (20gr)\n" +
                    "± Rp 2.000 \n" +
                    "± Rp 1.000 ",
            "1. Panaskan minyak goreng secukupnya.\n" +
                    "2. Tambahkan telur dan aduk rata.\n" +
                    "3. Masukkan 1 piring nasi dan aduk rata.\n" +
                    "4. Setelah nasi panas, taburkan bumbu racik nasi goreng sebanyak 1 sdm secara merata dan aduk.\n" +
                    "5. Sajikan selagi hangat.",
            arrayListOf()
        )
        addList.add(nasiGorengBumbuRacik)
        val nasiGorengKecapManis = Recipe(
            UUID.randomUUID().toString(),
            "Nasi Goreng Kecap Manis",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/nasi-goreng-kecap-manis.jpg?alt=media&token=aa6e2f4a-7868-4453-b422-0ad671ca0aab",
            8500,
            "10 menit",
            "-Nasi putih 1 piring\n" +
                    "-Telur 1 butir\n" +
                    "-Garam 1/2 sdt\n" +
                    "-Penyedap rasa 1/2 sdt\n" +
                    "-Kecap manis 3 sdm\n" +
                    "-Minyak goreng 1 sdm",
            "± Rp 3.000 \n" +
                    "± Rp 2.000\n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 500 (8gr)\n" +
                    "± Rp 1.000 (1 saset 20ml)\n" +
                    "± Rp 1.000 ",
            "1. Panaskan minyak goreng secukupnya .\n" +
                    "2. Tambahkan telur dan aduk rata.\n" +
                    "3. Masukkan 1 piring nasi dan aduk rata.\n" +
                    "4. Setelah nasi panas, taburkan garam, penyedap rasa, dan garam secara merata dan aduk.\n" +
                    "5. Sajikan selagi hangat.",
            arrayListOf()
        )
        addList.add(nasiGorengKecapManis)
        /*
        val telurDadarGaring = Recipe(
            UUID.randomUUID().toString(),
            "Telur Dadar Garing",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/telur-dadar-garing.jpg?alt=media&token=be401163-2b30-4203-83e0-e542d4b75dba",
            7000,
            "5 menit",
            "-Minyak goreng 220ml\n" +
                    "-Garam 1/2 sdt\n" +
                    "-Telur 1 butir",
            "± Rp 4.000 \n" +
                    "± Rp 1.000 (100gr) \n" +
                    "± Rp 2.000",
            "1. Campurkan telur dan garam di dalam mangkok, dan kocok.\n" +
                    "2. Panaskan minyak goreng secukupnya .\n" +
                    "3. Masukkan campuran telur ke dalam minyak panas, dan bolak-balik saat sudah berbentuk.\n" +
                    "4. Sajikan selagi hangat.",
            arrayListOf()
        )
        addList.add(telurDadarGaring)
        val perkedelKentang = Recipe(
            UUID.randomUUID().toString(),
            "Perkedel Kentang",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/perkedel-kentang.jpg?alt=media&token=bb38efec-f6cf-4c34-981b-b166c2fba2bf",
            34000,
            "35 menit",
            "-Kentang 500gr\n" +
                    "-Bawang merah goreng 2 sdm \n" +
                    "-Merica butir 1/2 sdt \n" +
                    "-Cengkih 3 kuntum \n" +
                    "-Biji pala 1/2 sdt \n" +
                    "-Garam 1 sdt\n" +
                    "-Telur 1/2 butir\n" +
                    "-Daun bawang 1 batang \n" +
                    "-Minyak goreng 220ml ",
            "± Rp 6.000 (500gr)\n" +
                    "± Rp 5.000 (50gr)\n" +
                    "± Rp 5.000 (30gr)\n" +
                    "± Rp 5.000 (10gr)\n" +
                    "± Rp 5.000 (10gr)\n" +
                    "± Rp 1.000 (100gr) \n" +
                    "± Rp 2.000 \n" +
                    "± Rp 2.500 (100gr)\n" +
                    "± Rp 4.000",
            "1. Haluskan bawang merah goreng, merica butir, cengkih, biji pala, garam, daun bawang.\n" +
                    "2. Kupas, potong-potong, dan goreng kentang.\n" +
                    "3. Haluskan kentang yang sudah digoreng, satukan dengan bumbu yang sudah dihaluskan, daun bawang, dan kuning telur. Aduk rata. \n" +
                    "4. Bentuk adonan menjadi bulat pipih, lalu celupkan ke dalam putih telur. Goreng hingga kuning kecokelatan. Angkat. ",
            arrayListOf()
        )
        addList.add(perkedelKentang)
        val martabakTahu = Recipe(
            UUID.randomUUID().toString(),
            "Martabak Tahu",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/martabak-tahu.jpg?alt=media&token=cadc1841-157a-43fc-94fd-e857178a2e63",
            34500,
            "45 menit",
            "-Telur bebek 3 butir\n" +
                    "-Tahu cina 1 potong\n" +
                    "-Daun bawang 1 batang \n" +
                    "-Bubuk merica 1 sdt\n" +
                    "-Garam 1 1/2 sdt \n" +
                    "-Penyedap rasa 1/2 sdt\n" +
                    "-Kulit lumpia 10 lembar\n" +
                    "-Minyak goreng 220ml ",
            "± Rp 9.000\n" +
                    "± Rp 5.000\n" +
                    "± Rp 2.500 (100gr)\n" +
                    "± Rp 2.000 (6gr)\n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 500 (8gr) \n" +
                    "± Rp 10.000 \n" +
                    "± Rp 4.000 ",
            "1. Hancurkan tahu cina. Iris halus daun bawang.\n" +
                    "2. Campur telur bebek dengan tahu, daun bawang, bubuk merica, garam, dan penyedap bila suka. Aduk rata. \n" +
                    "3. Ambil selembar kulit lumpia. Beri adonan secukupnya. Lipat semua sisinya menyerupai amplop. \n" +
                    "4. Goreng dalam minyak yang banyak dan panas hingga matang dan kuning kecokelatan. Angkat, tiriskan.",
            arrayListOf()
        )
        addList.add(martabakTahu)
        val chickenKatsu = Recipe(
            UUID.randomUUID().toString(),
            "Chicken Katsu",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/chicken-katsu.jpg?alt=media&token=4cb34897-02d6-458f-aebf-a492b729d980",
            42500,
            "45 menit",
            "-Dada ayam 1 potong\n" +
                    "-Bawang putih 2 siung\n" +
                    "-Merica bubuk 1 sdt\n" +
                    "-Garam 1 1/2 sdt \n" +
                    "-Air jeruk nipis 1 sdm\n" +
                    "-Tepung terigu 150gr\n" +
                    "-Telur 1 butir\n" +
                    "-Tepung roti 100gr\n" +
                    "-Minyak goreng 220ml ",
            "± Rp 16.000 (500gr)\n" +
                    "± Rp 1.000\n" +
                    "± Rp 2.000 (2 sast 3gr)\n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 2.500 (1 buah)\n" +
                    "± Rp 5.000 (250gr)\n" +
                    "± Rp 2.000 \n" +
                    "± Rp 9.000 (100gr)\n" +
                    "± Rp 4.000 ",
            "1. Pisahkan tulang dari daging dada ayam, dan buang tulangnya.\n" +
                    "2. Parut bawang putih. Kocok telur.\n" +
                    "3. Aduk rata bawang putih yang telah diparut, merica bubuk, garam, dan air jeruk nipis.\n" +
                    "4. Potong daging ayam menjadi empat bagian. Pukul-pukul daging ayam hingga pipih. Lumuri dengan bumbu, diamkan 20 menit agar bumbu meresap. \n" +
                    "5. Lumuri daging ayam berbumbu dengan tepung terigu, lalu celupkan ke dalam telur kocok. \n" +
                    "6. Gulingkan ke tepung roti kasar. Goreng dalam minyak yang panas dan banyak hingga kuning kecokelatan. Angkat, tiriskan. ",
            arrayListOf()
        )
        addList.add(chickenKatsu)
        val sayurBeningBayam = Recipe(
            UUID.randomUUID().toString(),
            "Sayur Bening Bayam",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/sayur-bening-bayam.jpg?alt=media&token=691de290-9074-4463-8ef6-a6ac794af4ac",
            21500,
            "40 menit",
            "-Bayam 2 ikat\n" +
                    "-Jagung manis 1 tongkol\n" +
                    "-Air 700ml\n" +
                    "-Bawang merah 7 siung\n" +
                    "-Daun salam 1 lembar\n" +
                    "-Temu kunci 2 cm\n" +
                    "-Tomat merah 1 buah\n" +
                    "-Garam 1/2 sdt\n" +
                    "-Gula pasir 1/2 sdt ",
            "± Rp 5.000\n" +
                    "± Rp 3.500\n" +
                    "± Rp 2.000 \n" +
                    "± Rp 4.000 (100gr)\n" +
                    "± Rp 500\n" +
                    "± Rp 3.000 \n" +
                    "± Rp 2.000 \n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 500 ",
            "1. Iris tipis-tipis bawah merah. Memarkan temu kunci. Potong-potong tomat merah.\n" +
                    "2. Petik-petik daun bayam dan batang mudanya, sisihkan. Iris bulir jagung, sisihkan. \n" +
                    "3. Didihkan air bersama bawang merah, daun salam, dan temu kunci. Masukkan daun bayam dan jagung. Aduk rata. \n" +
                    "4. Tambahkan tomat, garam, dan gula pasir. Aduk dan masak sampai mendidih. Angkat. ",
            arrayListOf()
        )
        addList.add(sayurBeningBayam)

        val tempeUlegBumbuKencur = Recipe(
            UUID.randomUUID().toString(),
            "Tempe Uleg Bumbu Kencur",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/tempe-uleg-bumbu-kencur.jpg?alt=media&token=8ba8e0cd-c051-442e-b81a-8693b648f779",
            17500,
            "40 menit",
            "-Tempe 200gr\n" +
                    "-Minyak goreng 220ml\n" +
                    "-Bawang putih 3 siung\n" +
                    "-Cabai merah keriting 1 buah\n" +
                    "-Cabai rawit merah 4 buah\n" +
                    "-Kencur 2 cm\n" +
                    "-Garam 1/2 sdt",
            "± Rp 2.500\n" +
                    "± Rp 4.000\n" +
                    "± Rp 2.000 (50gr) \n" +
                    "± Rp 3.000 (50gr)\n" +
                    "± Rp 3.000 (50gr)\n" +
                    "± Rp 2.000 (100gr) \n" +
                    "± Rp 1.000 (100gr)",
            "1. Potong dadu tempe.\n" +
                    "2. Haluskan bawang putih, cabai merah keriting, cabai rawit merah, kencur, dan garam.\n" +
                    "3. Goreng tempe dalam minyak panas hingga setengah matang. Angkat, tiriskan. \n" +
                    "4. Haluskan bumbu, lalu tambahkan tempe goreng. Tumbuk tempe selagi hangat hingga hancur. \n" +
                    "5. Aduk hingga tempe tercampur rata dengan bumbu. Sajikan.",
            arrayListOf()
        )
        addList.add(tempeUlegBumbuKencur)
        val ayamGorengTepung = Recipe(
            UUID.randomUUID().toString(),
            "Ayam Goreng Tepung",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/ayam-goreng-tepung.jpg?alt=media&token=7c22a920-4a9f-40c3-a50e-bc21964c1e53",
            44500,
            "3 Jam",
            "-Ayam 1/2 ekor\n" +
                    "-Jahe 4 cm\n" +
                    "-Bawang putih 4 siung\n" +
                    "-Garam 1/2 sdt\n" +
                    "-Tepung terigu 250gr\n" +
                    "-Tepung beras 50gr\n" +
                    "-Baking powder 1 sdt\n" +
                    "-Merica bubuk 1/2 sdt\n" +
                    "-Telur 2 butir\n" +
                    "-Minyak goreng 220ml",
            "± Rp 16.000\n" +
                    "± Rp 3.500\n" +
                    "± Rp 2.000 (50gr) \n" +
                    "± Rp 1.000 (100gr)\n" +
                    "± Rp 5.000 (250gr)\n" +
                    "± Rp 2.500 (100gr) \n" +
                    "± Rp 5.500 (45gr)\n" +
                    "± Rp 1.000 (1 saset 3gr)\n" +
                    "± Rp 4.000\n" +
                    "± Rp 4.000",
            "1. Potong ayam menjadi 4 bagian.\n" +
                    "2. Parut jahe. Parut bawang putih.\n" +
                    "3. Ayak tepung terigu, tepung beras, baking powder, merica bubuk.\n" +
                    "4. Lumuri ayam dengan jahe, bawang putih, dan garam. Tusuk-tusuk dengan garpu agar bumbu meresap. Diamkan ayam selama 2 jam di dalam kulkas. \n" +
                    "5. Celupkan ayam ke dalam telur kocok, gulingkan ke campuran tepung terigu. Lakukan sebanyak dua kali. \n" +
                    "6. Masukkan ayam ke dalam minyak panas dan banyak. Tunggu sampai terbentuk lapisan tepung ayam yang berwarna kuning kecokelatan. Kecilkan api. \n" +
                    "7. Lanjutkan menggoreng ayam dengan api kecil sampai ayam matang. Angkat.",
            arrayListOf()
        )
        addList.add(ayamGorengTepung)
        val sayurOyongBumbuEbi = Recipe(
            randomId,
            "Sayur Oyong Bumbu Ebi",
            "https://firebasestorage.googleapis.com/v0/b/z-cook.appspot.com/o/sayur-oyong-bumbu-ebi.jpg?alt=media&token=6ecc64dd-a35f-475d-ab6c-16dd3a539682",
            40500,
            "50 menit",
            "-Oyong 250gr\n" +
                    "-Minyak goreng 220ml\n" +
                    "-Ebi 50gr\n" +
                    "-Bawang putih 4 siung\n" +
                    "-Bubuk merica 1 sdt\n" +
                    "-Garam 1/2 sdt\n" +
                    "-Jamur kuping kering 15gr\n" +
                    "-Air 500ml\n" +
                    "-Kaldu ayam 1/2 sdt\n" +
                    "-Bakso ikan 10 buah",
            "± Rp 6.000\n" +
                    "± Rp 4.000\n" +
                    "± Rp 6.000 (5 saset 12gr) \n" +
                    "± Rp 2.000 (50gr)\n" +
                    "± Rp 2.000 (2 saset 3gr)\n" +
                    "± Rp 1.000 (100gr) \n" +
                    "± Rp 5.000 (40gr)\n" +
                    "± Rp 2.000\n" +
                    "± Rp 500 (8gr)\n" +
                    "± Rp 12.000",
            "1. Haluskan dan sangrai ebi. Haluskan juga bawang putih, lalu campurkan dengan ebi, merica, dan garam.\n" +
                    "2. Rendam jamur kuping kering dengan air panas, dan potong kasar.\n" +
                    "3. Kerat-kerat bakso ikan.\n" +
                    "4. Kupas dan potong oyong. Sisihkan. \n" +
                    "5. Panaskan minyak, tumis bumbu yang dihaluskan hingga harum. Masukkan irisan jamur kuping, aduk rata. \n" +
                    "6. Beri air dan kaldu ayam, masak sampai mendidih. Masukkan bakso ikan, aduk dan masak sampai bakso matang. Angkat. ",
            arrayListOf()
        )
        addList.add(sayurOyongBumbuEbi)*/
        //TODO ADD RECIPE
        for (i in addList) {
            database.collection("recipes")
                .document(randomId)
                .set(i)
                .addOnCompleteListener {
                    Log.w("TEZ", "added $it")
                }.addOnFailureListener {
                    Log.w("TEZ", "Error $it")
                }
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