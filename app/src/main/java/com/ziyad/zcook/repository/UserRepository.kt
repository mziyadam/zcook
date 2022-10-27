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
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.utils.recipeDummy
import java.lang.Exception

class UserRepository {
    //https://medium.com/firebase-tips-tricks/how-to-create-a-clean-firebase-authentication-using-mvvm-37f9b8eb7336
    private val auth = Firebase.auth
    private val database = FirebaseFirestore.getInstance()
    private val _currentUserLiveData = MutableLiveData<FirebaseUser>()
    val currentUserLiveData: LiveData<FirebaseUser> = _currentUserLiveData

    //    val currentUser: FirebaseUser? = auth.currentUser
    private val savedRecipe = MutableLiveData<ArrayList<Recipe>>()
    private val savedRecipeId = MutableLiveData<ArrayList<String>>()
    private val currentMahasiswaKos = MutableLiveData<MahasiswaKos>()

    init {
        _currentUserLiveData.value = auth.currentUser
        savedRecipe.value = arrayListOf()
        savedRecipeId.value = arrayListOf()
        currentMahasiswaKos.value = MahasiswaKos()
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
                                        currentMahasiswaKos.postValue(mahasiswaKos)
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

    fun getCurrentMahasiswaKos(): MutableLiveData<MahasiswaKos> {
        val mCurrentUser = auth.currentUser
        database.collection("mahasiswa_kos").document(mCurrentUser!!.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TEZ", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val mahasiswaKos = snapshot.toObject(MahasiswaKos::class.java)!!
                    Log.d("TEZ", "Current data: $mahasiswaKos")
                    currentMahasiswaKos.postValue(mahasiswaKos)
                } else {
                    Log.d("TEZ", "Current data: null")
                }
            }
        return currentMahasiswaKos
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
            _currentUserLiveData.postValue(auth.currentUser)
            message.postValue("SUCCESS")
        } catch (e: Exception) {
            message.postValue(e.toString())
        }
        return message
    }

    fun getSavedRecipeId(): MutableLiveData<ArrayList<String>> {
        val currentUser = auth.currentUser
        //TODO NOT YET IMPLEMENTED -> GET WHERE ID RESEP=SAVED
        currentUser?.let {
            database.collection("mahasiswa_kos").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val allRecipeId = arrayListOf<String>()
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
        //TODO NOT YET IMPLEMENTED -> GET WHERE ID RESEP=SAVED
        currentUser?.let {
            database.collection("mahasiswa_kos").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val mahasiswaKos = snapshot.toObject(MahasiswaKos::class.java)
                        val allRecipe = arrayListOf<Recipe>()
                        val allRecipeId = arrayListOf<String>()
                        Log.d(TAG, "DocumentSnapshot data: ${snapshot.data}")
                        for (i in mahasiswaKos!!.listSavedRecipeId) {
                            allRecipeId.add(i)
                        }
//                        Log.d("TEZZ", "Current data: $allRecipeId")
                        database.collection("recipes")
                            .addSnapshotListener { mSnapshot, mE ->
                                if (mE != null) {
                                    Log.w("TEZ", "Listen failed.", mE)
                                    return@addSnapshotListener
                                }

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

    suspend fun saveRecipe(recipeId: String) {
        //TODO NOT YET IMPLEMENTED
        val currentUser = auth.currentUser
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
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                            }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }

    suspend fun removeRecipeFromSaved(recipeId: String) {
        //TODO NOT YET IMPLEMENTED
        val currentUser = auth.currentUser
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
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                            }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }

    suspend fun changeEmailAndName(email: String, name: String): MutableLiveData<String> {
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.value = ""
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        //TODO SUS2
        user!!.apply {
            updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User email address updated.")
                        statusLiveData.value.plus("ERR")
                        _currentUserLiveData.value = auth.currentUser
                    }
                }
            updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User email address updated.")
                        statusLiveData.value.plus("ERR")
                        _currentUserLiveData.value = auth.currentUser
                    }
                }
        }
        return statusLiveData
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): MutableLiveData<String> {
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        val credential = EmailAuthProvider.getCredential(
            user?.email.toString(),
            oldPassword
        )

// Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _currentUserLiveData.value = auth.currentUser

                        statusLiveData.value = "SUCCESS"
                    } else {
                        Log.w(TAG, task.exception)
                        statusLiveData.value = "ERROR"
                    }
                }
            }?.addOnFailureListener {
                Log.w(TAG, it)
                statusLiveData.value = "ERROR"
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