package com.ziyad.zcook.ui.home.recipe_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.databinding.FragmentRecipeListBinding
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.detail.RecipeDetailActivity
import com.ziyad.zcook.ui.home.HomeViewModel
import com.ziyad.zcook.ui.search.SearchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.allRecipe.observe(viewLifecycleOwner) { allRecipe ->
            homeViewModel.savedRecipe.observe(viewLifecycleOwner) { savedRecipe ->
                binding.apply {
                    rvAllRecipe.apply {
                        adapter = RecipeAdapter(
                            allRecipe, savedRecipe, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                if (savedRecipe.contains(recipe)) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.removeRecipeFromSaved(recipe)
                                    }
                                } else {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.saveRecipe(recipe)
                                    }
                                }
                            })
                        setHasFixedSize(true)
                    }
                    rvRecommendation10s.apply {
                        adapter = RecipeAdapter(
                            allRecipe, savedRecipe, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                if (savedRecipe.contains(recipe)) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.removeRecipeFromSaved(recipe)
                                    }
                                } else {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.saveRecipe(recipe)
                                    }
                                }
                            })
                        setHasFixedSize(true)
                    }
                    rvRecommendationBelow10.apply {
                        adapter = RecipeAdapter(
                            allRecipe, savedRecipe, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe)
                                )
                            }, { recipe ->
                                if (savedRecipe.contains(recipe)) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.removeRecipeFromSaved(recipe)
                                    }
                                } else {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.saveRecipe(recipe)
                                    }
                                }
                            })
                        setHasFixedSize(true)
                    }
                }
            }
        }
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(requireContext(),SearchActivity::class.java))
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}