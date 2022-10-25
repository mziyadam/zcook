package com.ziyad.zcook.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MahasiswaKos(
    val id:String,
    val name:String,
    val listSaved: ArrayList<Recipe>,
    val listPersonalNote: ArrayList<PersonalNote>
): Parcelable
