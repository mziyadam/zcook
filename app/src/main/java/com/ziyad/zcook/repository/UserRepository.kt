package com.ziyad.zcook.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.utils.recipeDummy

class UserRepository {
    //https://medium.com/firebase-tips-tricks/how-to-create-a-clean-firebase-authentication-using-mvvm-37f9b8eb7336
    private val auth = Firebase.auth
    private val database = FirebaseFirestore.getInstance()
    private val _currentUserLiveData = MutableLiveData<FirebaseUser>()
    val currentUserLiveData: LiveData<FirebaseUser> = _currentUserLiveData
    private val savedRecipe=MutableLiveData<ArrayList<Recipe>>()
    private val savedRecipeId=MutableLiveData<ArrayList<String>>()
    init {
        _currentUserLiveData.value = auth.currentUser
        savedRecipe.value= arrayListOf()
        savedRecipeId.value= arrayListOf()
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    _currentUserLiveData.value = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    suspend fun register(email: String, name: String, password: String) {
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
                                _currentUserLiveData.value = auth.currentUser
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
    }

    suspend fun resetPassword(email: String): MutableLiveData<String> {
        val statusLiveData = MutableLiveData<String>()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    statusLiveData.value = "SUCCESS"
                } else {
                    Log.w(TAG, task.exception)
                    statusLiveData.value = "ERROR"
                }
            }
        return statusLiveData
    }

    suspend fun logout() {
        auth.signOut()
    }

    fun getSavedRecipeId(): MutableLiveData<ArrayList<String>> {
        val allRecipeId = arrayListOf<String>()
        //TODO NOT YET IMPLEMENTED -> GET WHERE ID RESEP=SAVED
        for(i in 1..10){
            allRecipeId.add(recipeDummy.id)
        }

//        database.collection("recipes").addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.w("TEZ", "Listen failed.", e)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null && !snapshot.isEmpty) {
//                for (doc in snapshot) {
//                    val mRecipe=doc.toObject(Recipe::class.java)
//                    if(allRecipeId.contains(mRecipe.id)){
//                        allRecipe.add(mRecipe)
//                    }
//                    Log.d("TEZ", "Current data: $doc")
//                }
//            } else {
//                Log.d("TEZ", "Current data: null")
//            }
//        }
        savedRecipeId.postValue(allRecipeId)

        return savedRecipeId
    }
    fun getSavedRecipe(): MutableLiveData<ArrayList<Recipe>> {
        val allRecipe = arrayListOf<Recipe>()
        val allRecipeId = arrayListOf<String>()
        //TODO NOT YET IMPLEMENTED -> GET WHERE ID RESEP=SAVED
        for(i in 1..10){
            allRecipeId.add(recipeDummy.id)
        }

        database.collection("recipes").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                for (doc in snapshot) {
                    val mRecipe=doc.toObject(Recipe::class.java)
                    if(allRecipeId.contains(mRecipe.id)){
                        allRecipe.add(mRecipe)
                    }
                    Log.d("TEZ", "Current data: $doc")
                }
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }
        savedRecipe.postValue(allRecipe)

        return savedRecipe
    }
    suspend fun saveRecipe(recipeId: String) {
        //TODO NOT YET IMPLEMENTED

    }

    suspend fun removeRecipeFromSaved(recipeId: String) {
        //TODO NOT YET IMPLEMENTED

    }

    suspend fun changeEmailAndName(email: String, name: String): MutableLiveData<String> {
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.value=""
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        //TODO SUS2
        user!!.apply {
            updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User email address updated.")
                        statusLiveData.value.plus("SUS")
                        _currentUserLiveData.value = auth.currentUser
                    }
                }
            updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User email address updated.")
                        statusLiveData.value.plus("SUS")
                        _currentUserLiveData.value = auth.currentUser
                    }
                }
        }
        return statusLiveData
    }

    suspend fun changePassword(oldPassword:String,newPassword:String): MutableLiveData<String>{
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        val credential = EmailAuthProvider.getCredential(
            user?.email.toString(),
            oldPassword
        )

// Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                user.updatePassword(newPassword).addOnCompleteListener {task->
                    if(task.isSuccessful){
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