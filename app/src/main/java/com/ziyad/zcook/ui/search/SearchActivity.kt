package com.ziyad.zcook.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.databinding.ActivitySearchBinding
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.ui.detail.RecipeDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var binding: ActivitySearchBinding
    override fun onBackPressed() {
        super.onBackPressed()
        lifecycleScope.launch(Dispatchers.IO) {
            searchViewModel.clearRecipe()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnBack.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    searchViewModel.clearRecipe()
                }
                finish()
            }
            btnSearch.setOnClickListener {
                rvSearchResult.visibility = View.GONE
                lifecycleScope.launch(Dispatchers.IO) {
                    searchViewModel.clearRecipe()
                    searchViewModel.searchRecipe(etSearchQuery.text.toString())
                    startActivity(
                        Intent(
                            this@SearchActivity,
                            SearchActivity::class.java
                        ).putExtra("query", etSearchQuery.text.toString())
                    )
                    finish()
                }
            }
            val mQuery = intent.getStringExtra("query")
            if (mQuery != null) {
                binding.etSearchQuery.setText(mQuery)
            }
            searchViewModel.savedRecipeId.observe(this@SearchActivity) { savedRecipeId ->
                searchViewModel.searchResult.observe(this@SearchActivity) { searchResult ->
                    Log.d("TEZ", "Current data: $savedRecipeId")
                    binding.rvSearchResult.apply {
                        adapter = RecipeAdapter(
                            searchResult, savedRecipeId, { recipe ->
                                startActivity(
                                    Intent(
                                        this@SearchActivity,
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                searchViewModel.currentUserLiveData.observe(this@SearchActivity) { user ->
                                    if (user != null) {
                                        if (savedRecipeId.contains(recipe.id)) {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                searchViewModel.removeRecipeFromSaved(recipe.id)
                                            }
                                        } else {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                searchViewModel.saveRecipe(recipe.id)
                                            }
                                        }
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SearchActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                    }
                                }
                            })
                        setHasFixedSize(true)
                    }
                }
            }

        }
    }
}