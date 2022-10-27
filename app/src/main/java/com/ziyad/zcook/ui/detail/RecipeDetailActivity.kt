package com.ziyad.zcook.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ActivityRecipeDetailBinding
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.adapter.ReviewAdapter
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.utils.loadImage
import com.ziyad.zcook.utils.to1Digit
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
        recipeDetailViewModel.currentRecipe(recipeId!!).observe(this) { mRecipe ->
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
                tvRating.text = rating.to1Digit().toString()
                btnBack.setOnClickListener {
                    finish()
                }
                rvReview.apply {
                    adapter = ReviewAdapter(mRecipe.listReview)
                    setHasFixedSize(true)
                }
                recipeDetailViewModel.currentUserLiveData.observe(this@RecipeDetailActivity) {
                    if (it == null) {
                        btnToLogin.visibility = View.VISIBLE
                        clLoggedIn.visibility = View.GONE
                        btnToLogin.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@RecipeDetailActivity,
                                    LoginActivity::class.java
                                )
                            )
                        }
                        btnSave.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@RecipeDetailActivity,
                                    LoginActivity::class.java
                                )
                            )
                        }
                    } else {
                        btnToLogin.visibility = View.GONE
                        clLoggedIn.visibility = View.VISIBLE
                        for (i in mRecipe.listReview) {
                            if (i.userId == it.uid) {
                                when (i.rating) {
                                    1.0 -> {
                                        setStar(1.0)
                                    }
                                    2.0 -> {
                                        setStar(2.0)
                                    }
                                    3.0 -> {
                                        setStar(3.0)
                                    }
                                    4.0 -> {
                                        setStar(4.0)
                                    }
                                    5.0 -> {
                                        setStar(5.0)
                                    }
                                }
                                etReview.setText(i.review)
                            }
                        }
                        ivStar1.setOnClickListener {
                            setStar(1.0)
                        }
                        ivStar2.setOnClickListener {
                            setStar(2.0)
                        }
                        ivStar3.setOnClickListener {
                            setStar(3.0)
                        }
                        ivStar4.setOnClickListener {
                            setStar(4.0)
                        }
                        ivStar5.setOnClickListener {
                            setStar(5.0)
                        }
                        recipeDetailViewModel.getPersonalNote(mRecipe.id)
                            .observe(this@RecipeDetailActivity) { mNote ->
                                etPersonalNote.setText(mNote)
                            }
                        btnSaveNote.setOnClickListener {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val message = recipeDetailViewModel.addPersonalNote(
                                    mRecipe.id,
                                    etPersonalNote.text.toString()
                                )
                                lifecycleScope.launch(Dispatchers.Main){
                                    message.observe(this@RecipeDetailActivity){
                                        when (it) {
                                            "SUCCESS" -> {
                                                Toast.makeText(
                                                    this@RecipeDetailActivity,
                                                    "Success",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            "LOADING" -> {
                                                Toast.makeText(
                                                    this@RecipeDetailActivity,
                                                    "Loading",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            else->{
                                                Toast.makeText(
                                                    this@RecipeDetailActivity,
                                                    it,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        btnSaveReview.setOnClickListener {
                            val mStar = recipeDetailViewModel.star
                            Log.w("TEZZ", mStar.toString())
                            recipeDetailViewModel.star.observe(this@RecipeDetailActivity) { mStar ->
                                if (mStar != 0.0) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val message = recipeDetailViewModel.addRatingAndReview(
                                            mRecipe.id,
                                            mStar,
                                            etReview.text.toString()
                                        )
                                        lifecycleScope.launch(Dispatchers.Main) {
                                            message.observe(this@RecipeDetailActivity) {
                                                when (it) {
                                                    "SUCCESS" -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            "Success",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
                                                    }
                                                    "LOADING" -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            "Loading",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
                                                    }
                                                    else -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            it,
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this@RecipeDetailActivity,
                                        "Beri nilai",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        recipeDetailViewModel.savedRecipeId.observe(this@RecipeDetailActivity) { savedRecipeId ->
                            // Drawables for save
                            val saveDrawable =
                                this@RecipeDetailActivity.resources.getDrawable(R.drawable.ic_bookmark)
                            val saveBorderDrawable =
                                this@RecipeDetailActivity.resources.getDrawable(R.drawable.ic_bookmark_border_white)
                            Log.w("TEZZ", savedRecipeId.toString())
                            if (savedRecipeId.contains(mRecipe.id)) {
                                btnSave.setImageDrawable(saveDrawable)
                                btnSave.setOnClickListener {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        recipeDetailViewModel.removeRecipeFromSaved(mRecipe.id)
                                    }
                                }
                            } else {
                                btnSave.setImageDrawable(saveBorderDrawable)
                                btnSave.setOnClickListener {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        recipeDetailViewModel.saveRecipe(mRecipe.id)
                                    }
                                }
                            }
                        }

                    }


                }
            }
        }
    }

    private fun setStar(rating: Double) {
        val starDrawable = resources.getDrawable(R.drawable.ic_sharp_star_24)
        val starBorderDrawable = resources.getDrawable(R.drawable.ic_sharp_star_border_24)
        lifecycleScope.launch(Dispatchers.IO) {
            recipeDetailViewModel.setStar(rating)
        }
        binding.apply {
            when (rating) {
                1.0 -> {
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starBorderDrawable)
                    ivStar3.setImageDrawable(starBorderDrawable)
                    ivStar4.setImageDrawable(starBorderDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                2.0 -> {
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starBorderDrawable)
                    ivStar4.setImageDrawable(starBorderDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                3.0 -> {
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starDrawable)
                    ivStar4.setImageDrawable(starBorderDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                4.0 -> {
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starDrawable)
                    ivStar4.setImageDrawable(starDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                5.0 -> {
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starDrawable)
                    ivStar4.setImageDrawable(starDrawable)
                    ivStar5.setImageDrawable(starDrawable)
                }
            }
        }
    }

    companion object {
        const val RECIPE_ID = "recipe_id"
    }
}