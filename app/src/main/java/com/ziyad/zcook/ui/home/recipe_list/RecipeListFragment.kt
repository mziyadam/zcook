package com.ziyad.zcook.ui.home.recipe_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.databinding.FragmentRecipeListBinding
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.ui.detail.RecipeDetailActivity
import com.ziyad.zcook.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            homeViewModel.savedRecipeId.observe(viewLifecycleOwner) { listSavedRecipeId ->
                homeViewModel.allRecipe.observe(viewLifecycleOwner) { allRecipe ->
                    rvAllRecipe.apply {
                        adapter = RecipeAdapter(
                            allRecipe, listSavedRecipeId, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                homeViewModel.currentUserLiveData.observe(viewLifecycleOwner) { user ->
                                    if (user != null) {
                                        if (listSavedRecipeId.contains(recipe.id)) {
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

                homeViewModel.recipeBelow10.observe(viewLifecycleOwner) { allRecipe ->
                    rvRecommendationBelow10.apply {
                        adapter = RecipeAdapter(
                            allRecipe, listSavedRecipeId, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                homeViewModel.currentUserLiveData.observe(viewLifecycleOwner) { user ->
                                    if (user != null) {
                                        if (listSavedRecipeId.contains(recipe.id)) {
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

                homeViewModel.recipe10s.observe(viewLifecycleOwner) { allRecipe ->
//                        Log.e("TEZZ",listSavedRecipeId.toString()+" --->"+allRecipe.toString())
                    rvRecommendation10s.apply {
                        adapter = RecipeAdapter(
                            allRecipe, listSavedRecipeId, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                homeViewModel.currentUserLiveData.observe(viewLifecycleOwner) { user ->
                                    if (user != null) {
                                        if (listSavedRecipeId.contains(recipe.id)) {
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
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}