package com.ziyad.zcook.ui.home.search_recipe

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
            etSearchQuery.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    rvSearchResult.visibility = View.VISIBLE
                    lifecycleScope.launch(Dispatchers.IO) {
                        homeViewModel.clearSearch()
                        homeViewModel.searchRecipe(etSearchQuery.text.toString())
                    }
                    val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken,0)
                    handled = true
                }
                handled
            })
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
                                                val message =
                                                    homeViewModel.removeRecipeFromSaved(recipe.id)
                                                lifecycleScope.launch(Dispatchers.Main) {
                                                    message.observe(viewLifecycleOwner) { mIt ->
                                                        when (mIt) {
                                                            "SUCCESS" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Success",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            "LOADING" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Loading",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            else -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    mIt,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                val message = homeViewModel.saveRecipe(recipe.id)
                                                lifecycleScope.launch(Dispatchers.Main) {
                                                    message.observe(viewLifecycleOwner) { mIt ->
                                                        when (mIt) {
                                                            "SUCCESS" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Success",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            "LOADING" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Loading",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            else -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    mIt,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                }
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