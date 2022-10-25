package com.ziyad.zcook.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val userId:String,
    val userName:String,
    val rating:Double,
    val review:String
):Parcelable
