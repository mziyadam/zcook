package com.ziyad.zcook.model

data class Recipe(
    val id:String,
    val name:String,
    val imageUrl:String,
    val estimatedPrice:Int,
    val estimatedTime:String,
    val listIngredient:String,
    val listIngredientPrice:String,
    val steps:String,
    val listReview:ArrayList<Review>
)
