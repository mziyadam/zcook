package com.ziyad.zcook.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.databinding.ActivitySearchBinding
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.detail.RecipeDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            btnSearch.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    searchViewModel.searchRecipe(etSearchQuery.text.toString())
                }
            }
            searchViewModel.searchResult.observe(this@SearchActivity) { searchResult ->
                if (searchResult != null)
                    searchViewModel.savedRecipe.observe(this@SearchActivity) { savedRecipe ->
                        binding.rvSearchResult.apply {
                            adapter = RecipeAdapter(
                                searchResult, savedRecipe, { recipe ->
                                    startActivity(
                                        Intent(
                                            this@SearchActivity,
                                            RecipeDetailActivity::class.java
                                        ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                    )
                                }, { recipe ->
                                    if (savedRecipe.contains(recipe)) {
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            searchViewModel.removeRecipeFromSaved(recipe)
                                        }
                                    } else {
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            searchViewModel.saveRecipe(recipe)
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