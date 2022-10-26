package com.ziyad.zcook.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MahasiswaKos(
    val id:String= "",
    val name:String= "",
    val listSavedRecipeId: ArrayList<String> = arrayListOf(),
    val listPersonalNote: ArrayList<PersonalNote> = arrayListOf()
): Parcelable
