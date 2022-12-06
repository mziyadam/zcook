package com.ziyad.zcook.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ziyad.zcook.model.MahasiswaKos
import com.ziyad.zcook.model.PersonalNote
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.model.Review
import com.ziyad.zcook.utils.recipeDummy
import java.lang.Exception

class UserRepository {
    private val auth = Firebase.auth
    private val database = FirebaseFirestore.getInstance()
    private val _currentUserLiveData = MutableLiveData<FirebaseUser>()
    val currentUserLiveData: LiveData<FirebaseUser> = _currentUserLiveData
    private val savedRecipe = MutableLiveData<ArrayList<Recipe>>()
    private val savedRecipeId = MutableLiveData<ArrayList<String>>()

    init {
        _currentUserLiveData.value = auth.currentUser
        savedRecipe.value = arrayListOf()
        savedRecipeId.value = arrayListOf()
    }

    suspend fun login(email: String, password: String): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    _currentUserLiveData.value = auth.currentUser
                    message.postValue("SUCCESS")
                    getSavedRecipeId()
                    getSavedRecipe()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    message.postValue(task.exception.toString())
                }
            }
        return message
    }

    suspend fun register(
        email: String,
        name: String,
        password: String
    ): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { mTask ->
                            if (mTask.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                val mCurrentUser = auth.currentUser
                                val mahasiswaKos = MahasiswaKos(
                                    mCurrentUser!!.uid,
                                    mCurrentUser.displayName!!
                                )
                                database.collection("mahasiswa_kos")
                                    .document(mahasiswaKos.id)
                                    .set(mahasiswaKos)
                                    .addOnSuccessListener {
                                        _currentUserLiveData.postValue(auth.currentUser)
                                        message.postValue("SUCCESS")
                                    }
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                message.postValue(task.exception.toString())
                            }
                        }
                        .addOnFailureListener {
                            Log.w(TAG, "updateProfile:failure", task.exception)
                            message.postValue(task.exception.toString())
                        }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    message.postValue(task.exception.toString())
                }
            }
        return message
    }

    suspend fun resetPassword(email: String): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    message.postValue("SUCCESS")
                } else {
                    Log.w(TAG, task.exception)
                    message.postValue("ERROR")
                }
            }
        return message
    }

    suspend fun logout(): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        try {
            auth.signOut()
            savedRecipeId.postValue(arrayListOf())
            savedRecipe.postValue(arrayListOf())
            _currentUserLiveData.postValue(auth.currentUser)
            message.postValue("SUCCESS")
        } catch (e: Exception) {
            message.postValue(e.toString())
        }
        return message
    }

    fun getSavedRecipeId(): MutableLiveData<ArrayList<String>> {
        val currentUser = auth.currentUser
        currentUser?.let {
            database.collection("mahasiswa_kos").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val allRecipeId = arrayListOf<String>()
                        savedRecipeId.postValue(allRecipeId)
                        val mahasiswaKos = snapshot.toObject(MahasiswaKos::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${snapshot.data}")
                        for (i in mahasiswaKos!!.listSavedRecipeId) {
                            allRecipeId.add(i)
                        }
                        Log.d("TEZZ", "Current data: $allRecipeId")
                        savedRecipeId.postValue(allRecipeId)
                    } else {
                        Log.d("TEZ", "Current data: null")
                    }
                }
        }

        return savedRecipeId
    }

    fun getSavedRecipe(): MutableLiveData<ArrayList<Recipe>> {
        val currentUser = auth.currentUser
        currentUser?.let {
            database.collection("mahasiswa_kos").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    Log.d("TEZaZ", "Current data: $e")
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val mahasiswaKos = snapshot.toObject(MahasiswaKos::class.java)
                        val allRecipe = arrayListOf<Recipe>()
                        val allRecipeId = arrayListOf<String>()
                        savedRecipe.postValue(allRecipe)
                        Log.d(TAG, "DocumentSnapshot data: ${snapshot.data}")
                        for (i in mahasiswaKos!!.listSavedRecipeId) {
                            allRecipeId.add(i)
                        }
                        Log.d("TEZaZ", "Current data: $allRecipeId")
                        database.collection("recipes")
                            .get().addOnSuccessListener { mSnapshot->
                                if (mSnapshot != null && !mSnapshot.isEmpty) {
                                    for (doc in mSnapshot) {
                                        val mRecipe = doc.toObject(Recipe::class.java)
                                        if (allRecipeId.contains(mRecipe.id)) {
                                            allRecipe.add(mRecipe)
                                            Log.d("TEZ", "Current data: $doc")
                                        }
                                    }
                                    savedRecipe.postValue(allRecipe)
                                } else {
                                    Log.d("TEZ", "Current data: null")
                                }
                            }
                    } else {
                        Log.d("TEZ", "Current data: null")
                    }
                }
        }

        return savedRecipe
    }

    suspend fun saveRecipe(recipeId: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        currentUser?.let {
            database.collection("mahasiswa_kos").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val mahasiswaKos = document.toObject(MahasiswaKos::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        mahasiswaKos?.listSavedRecipeId?.add(recipeId)
                        database.collection("mahasiswa_kos").document(currentUser!!.uid)
                            .set(mahasiswaKos!!).addOnSuccessListener {
                                Log.d("TEZ", "Current data: $it")
                                savedRecipeId.postValue(mahasiswaKos.listSavedRecipeId)
                                statusLiveData.postValue("SUCCESS")
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                                statusLiveData.postValue(it.toString())
                            }
                    } else {
                        Log.d(TAG, "No such document")
                        statusLiveData.postValue("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    statusLiveData.postValue(exception.toString())
                }
        }
        return statusLiveData
    }

    suspend fun removeRecipeFromSaved(recipeId: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        currentUser?.let {
            database.collection("mahasiswa_kos").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val mahasiswaKos = document.toObject(MahasiswaKos::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        mahasiswaKos?.listSavedRecipeId?.remove(recipeId)
                        database.collection("mahasiswa_kos").document(currentUser!!.uid)
                            .set(mahasiswaKos!!).addOnSuccessListener {
                                Log.d("TEZ", "Current data: $it")
                                savedRecipeId.postValue(mahasiswaKos.listSavedRecipeId)
                                statusLiveData.postValue("SUCCESS")
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                                statusLiveData.postValue(it.toString())
                            }
                    } else {
                        Log.d(TAG, "No such document")
                        statusLiveData.postValue("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    statusLiveData.postValue(exception.toString())
                }
        }
        return statusLiveData
    }

    fun getPersonalNote(recipeId: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val note = MutableLiveData<String>()
        note.postValue("")
        currentUser?.let {
            database.collection("mahasiswa_kos").document(currentUser.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        var mNote = ""
                        val mahasiswaKos = snapshot.toObject(MahasiswaKos::class.java)
                        Log.d("TEZZZ", "DocumentSnapshot data: ${snapshot.data}")
                        for (i in mahasiswaKos!!.listPersonalNote) {
                            Log.d("TEZZZ", "ada data: ${i} apakah sama dengan $recipeId")
                            if (i.recipeId == recipeId) {
                                mNote = i.note
                            }
                        }
                        Log.d("TEZZ", "Current data: $mNote")
                        note.postValue(mNote)
                    } else {
                        Log.d("TEZ", "Current data: null")
                    }
                }
        }

        return note
    }

    suspend fun addPersonalNote(recipeId: String, note: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        currentUser?.let {
            database.collection("mahasiswa_kos").document(currentUser.uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot != null && snapshot.exists()) {
                        val mPersonalNote = PersonalNote(recipeId, note)
                        val mMahasiswaKos = snapshot.toObject(MahasiswaKos::class.java)!!
                        Log.d("TEZZ", "Current data: $mMahasiswaKos")
                        for (i in mMahasiswaKos.listPersonalNote) {
                            if (i.recipeId == recipeId) {
                                mMahasiswaKos.listPersonalNote.remove(i)
                            }
                        }
                        mMahasiswaKos.listPersonalNote.add(mPersonalNote)
                        database.collection("mahasiswa_kos").document(currentUser.uid)
                            .set(mMahasiswaKos).addOnSuccessListener {
                                statusLiveData.postValue("SUCCESS")
                                Log.d("TEZZ", "SUCCESS")
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

    suspend fun changeEmailAndName(email: String, name: String): MutableLiveData<String> {
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        user!!.apply {
            updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User email address updated.")
                        updateProfile(profileUpdates)
                            .addOnCompleteListener { mTask ->
                                if (mTask.isSuccessful) {
                                    Log.d(TAG, "User email address updated.")
                                    val mCurrentUser = auth.currentUser
                                    val mahasiswaKos = MahasiswaKos(
                                        mCurrentUser!!.uid,
                                        mCurrentUser.displayName!!
                                    )
                                    statusLiveData.postValue("SUCCESS")
                                    database.collection("mahasiswa_kos")
                                        .document(mahasiswaKos.id)
                                        .update("name",mahasiswaKos.name)
                                        .addOnSuccessListener {
                                            _currentUserLiveData.postValue(auth.currentUser)
                                        }.addOnFailureListener {
                                            Log.d("TEZZ", "Error : $it")
                                            statusLiveData.postValue("$it")
                                            _currentUserLiveData.postValue(auth.currentUser)
                                        }
                                    database.collection("recipes").get().addOnSuccessListener { mSnapshot ->
                                        if (mSnapshot != null && !mSnapshot.isEmpty) {
                                            val listRecipeTemp= arrayListOf<Recipe>()
                                            for (doc in mSnapshot) {
                                                val mRecipe = doc.toObject(Recipe::class.java)
                                                val newReview= arrayListOf<Review>()
                                                for (i in mRecipe.listReview) {
                                                    if (i.userId == mCurrentUser.uid) {
                                                        newReview.add(i.copy(userName = mCurrentUser.displayName.toString()))
                                                    }else{
                                                        newReview.add(i)
                                                    }
                                                }
                                                mRecipe.listReview.clear()
                                                mRecipe.listReview.addAll(newReview)
                                                listRecipeTemp.add(mRecipe)

                                            }
                                            for (mRecipe in listRecipeTemp) {
                                                database.collection("recipes").document(mRecipe.id).update("listReview",mRecipe.listReview)
                                                    .addOnSuccessListener {
                                                        Log.d("TEZX", "Current data: $mRecipe")
                                                    }.addOnFailureListener {
                                                        Log.d("TEZ", "Error : $it")
                                                    }
                                                Log.d("TEZ", "Current data: $mRecipe")
                                            }
                                        } else {
                                            Log.d("TEZ", "Current data: null")
                                        }

                                    }
                                }
                            }.addOnFailureListener {
                                Log.d("TEZZ", "Error : $it")
                                statusLiveData.postValue("$it")
                                _currentUserLiveData.postValue(auth.currentUser)
                            }
                    }
                }.addOnFailureListener {
                    Log.d("TEZZ", "Error : $it")
                    statusLiveData.postValue("$it")
                    _currentUserLiveData.postValue(auth.currentUser)
                }
        }

        return statusLiveData
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): MutableLiveData<String> {
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        val credential = EmailAuthProvider.getCredential(
            user?.email.toString(),
            oldPassword
        )

        user?.reauthenticate(credential)
            ?.addOnSuccessListener {
                user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _currentUserLiveData.value = auth.currentUser

                        Log.w(TAG, task.toString())
                        statusLiveData.postValue("SUCCESS")
                    } else {
                        Log.w(TAG, task.exception)
                        statusLiveData.postValue("$it")
                    }
                }
            }?.addOnFailureListener {
                Log.w(TAG, it)
                statusLiveData.postValue("$it")
            }
        return statusLiveData
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(): UserRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    instance = UserRepository()
                }
                return instance as UserRepository
            }
        }
    }
}