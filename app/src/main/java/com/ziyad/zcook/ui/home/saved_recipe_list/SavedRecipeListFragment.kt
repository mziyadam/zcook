package com.ziyad.zcook.ui.home.saved_recipe_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.FragmentSavedRecipeListBinding
import com.ziyad.zcook.ui.adapter.RecipeAdapter
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.ui.detail.RecipeDetailActivity
import com.ziyad.zcook.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedRecipeListFragment : Fragment() {

    private var _binding: FragmentSavedRecipeListBinding? = null
    private lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentSavedRecipeListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.apply {
            btnToLogin.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            homeViewModel.savedRecipe.observe(viewLifecycleOwner) { savedRecipe ->
                homeViewModel.savedRecipeId.observe(viewLifecycleOwner) { savedRecipeId ->
                    rvSavedRecipe.apply {
                        adapter = RecipeAdapter(
                            savedRecipe, savedRecipeId, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                if (savedRecipeId.contains(recipe.id)) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.removeRecipeFromSaved(recipe.id)
                                    }
                                } else {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.saveRecipe(recipe.id)
                                    }
                                }
                            })
                        setHasFixedSize(true)
                    }
                }
            }

            homeViewModel.currentUserLiveData.observe(viewLifecycleOwner) {
                if (it == null) {
                    rvSavedRecipe.visibility = View.GONE
                    btnToLogin.visibility = View.VISIBLE
                    tvName.text = requireContext().resources.getText(R.string.masuk_ke_akunmu)
                    tvEmail.text =
                        requireContext().resources.getText(R.string.masuk_agar_dapat_simpan_resep_dan_review)
                } else {
                    rvSavedRecipe.visibility = View.VISIBLE
                    btnToLogin.visibility = View.GONE
                }
            }
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            homeViewModel.savedRecipe.observe(viewLifecycleOwner) { savedRecipe ->
                homeViewModel.savedRecipeId.observe(viewLifecycleOwner) { savedRecipeId ->
                    rvSavedRecipe.apply {
                        adapter = RecipeAdapter(
                            savedRecipe, savedRecipeId, { recipe ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        RecipeDetailActivity::class.java
                                    ).putExtra(RecipeDetailActivity.RECIPE_ID, recipe.id)
                                )
                            }, { recipe ->
                                if (savedRecipeId.contains(recipe.id)) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.removeRecipeFromSaved(recipe.id)
                                    }
                                } else {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        homeViewModel.saveRecipe(recipe.id)
                                    }
                                }
                            })
                        setHasFixedSize(true)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}