package com.ziyad.zcook.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonalNote(
    val recipeId:String,
    val note:String
):Parcelable
