package com.ziyad.zcook.ui.home.search_recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.FragmentSavedRecipeListBinding
import com.ziyad.zcook.databinding.FragmentSearchRecipeBinding
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.ui.detail.RecipeDetailActivity
import com.ziyad.zcook.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchRecipeFragment : Fragment() {

    private var _binding: FragmentSearchRecipeBinding? = null
    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentSearchRecipeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.apply {
            btnSearch.setOnClickListener {
                rvSearchResult.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.IO) {
                    homeViewModel.clearSearch()
                    homeViewModel.searchRecipe(etSearchQuery.text.toString())
                }
            }
            homeViewModel.savedRecipeId.observe(viewLifecycleOwner) { savedRecipeId ->
                homeViewModel.searchResult.observe(viewLifecycleOwner) { searchResult ->
                    Log.d("TEZ", "Current data: $savedRecipeId")
                    binding.rvSearchResult.apply {
                        adapter = RecipeAdapter(
                            searchResult, savedRecipeId, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                homeViewModel.currentUserLiveData.observe(viewLifecycleOwner) { user ->
                                    if (user != null) {
                                        if (savedRecipeId.contains(recipe.id)) {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                homeViewModel.removeRecipeFromSaved(recipe.id)
                                            }
                                        } else {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                homeViewModel.saveRecipe(recipe.id)
                                            }
                                        }
                                    } else {
                                        startActivity(
                                            Intent(
                                                requireContext(),
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
            return root
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}