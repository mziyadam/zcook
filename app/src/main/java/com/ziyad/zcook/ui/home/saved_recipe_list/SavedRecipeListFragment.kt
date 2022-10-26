package com.ziyad.zcook.ui.home.saved_recipe_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.databinding.FragmentSavedRecipeListBinding
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.detail.RecipeDetailActivity
import com.ziyad.zcook.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedRecipeListFragment : Fragment() {

    private var _binding: FragmentSavedRecipeListBinding? = null

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

        _binding = FragmentSavedRecipeListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        homeViewModel.allRecipe.observe(viewLifecycleOwner) { allRecipe ->
            homeViewModel.savedRecipe.observe(viewLifecycleOwner) { savedRecipe ->
                binding.apply {
                    rvSavedRecipe.apply {
                        adapter = RecipeAdapter(
                            savedRecipe, savedRecipe, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                lifecycleScope.launch(Dispatchers.IO) {
                                    homeViewModel.saveRecipe(recipe)
                                }
                            })
                        setHasFixedSize(true)
                    }
                }
            }
        }
        /*val textView: TextView = binding.textDashboard
        savedRecipeListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}