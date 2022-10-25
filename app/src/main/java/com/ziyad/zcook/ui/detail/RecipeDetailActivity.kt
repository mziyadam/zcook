package com.ziyad.zcook.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ziyad.zcook.R

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
    }
    companion object{
        const val RECIPE="recipe"
    }
}