package com.ziyad.zcook.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ActivityRecipeDetailBinding
import com.ziyad.zcook.utils.loadImage
import com.ziyad.zcook.utils.toCurrencyFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {
    private val recipeDetailViewModel: RecipeDetailViewModel by viewModels()
    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recipeId = intent.getStringExtra(RECIPE_ID)
        recipeDetailViewModel.currentRecipe(recipeId!!).observe(this) {mRecipe->
            // Rating Count
            var rating = 0.0
            var reviewCount = 0
            for (i in mRecipe.listReview) {
                rating += i.rating
                reviewCount++
            }
            if (reviewCount > 0)
                rating /= reviewCount

            binding.apply {
                ivRecipe.loadImage(mRecipe.imageUrl)
                tvEstimatedPrice.text =
                    "Total harga : ± " + mRecipe.estimatedPrice.toString().toCurrencyFormat()
                tvEstimatedTime.text = "Estimasi waktu memasak : ± " + mRecipe.estimatedTime
                tvListIngredient.text = mRecipe.listIngredient
                tvListIngredientPrice.text = mRecipe.listIngredientPrice
                tvSteps.text = mRecipe.steps
                tvRecipeName.text = mRecipe.name
                tvRating.text=rating.toString()
            }
            recipeDetailViewModel.savedRecipe.observe(this) { savedRecipe ->
                binding.apply {
                    // Drawables for save
                    val saveDrawable =
                        this@RecipeDetailActivity.resources.getDrawable(R.drawable.ic_bookmark)
                    val saveBorderDrawable =
                        this@RecipeDetailActivity.resources.getDrawable(R.drawable.ic_bookmark_border_white)
                    if (savedRecipe.contains(mRecipe)) {
                        btnSave.setImageDrawable(saveDrawable)
                        btnSaveReview.setOnClickListener {
                            lifecycleScope.launch(Dispatchers.IO) {
                                recipeDetailViewModel.removeRecipeFromSaved(mRecipe)
                            }
                        }
                    } else {
                        btnSave.setImageDrawable(saveBorderDrawable)
                        btnSaveReview.setOnClickListener {
                            lifecycleScope.launch(Dispatchers.IO) {
                                recipeDetailViewModel.saveRecipe(mRecipe)
                            }
                        }
                    }
                }
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val RECIPE_ID = "recipe_id"
    }
}