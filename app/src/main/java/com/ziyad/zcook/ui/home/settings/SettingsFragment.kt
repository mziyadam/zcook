package com.ziyad.zcook.ui.home.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.FragmentSettingsBinding
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.ui.home.HomeActivity
import com.ziyad.zcook.ui.home.HomeViewModel
import com.ziyad.zcook.ui.home.settings.change_account.ChangeAccountActivity
import com.ziyad.zcook.ui.home.settings.change_password.ChangePasswordActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

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

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.apply {
            btnToLogin.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            btnToChangeAccount.setOnClickListener {
                startActivity(Intent(requireContext(), ChangeAccountActivity::class.java))
            }
            btnToChangePassword.setOnClickListener {
                startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
            }
            btnLogout.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val message = homeViewModel.logout()
                    lifecycleScope.launch(Dispatchers.Main) {
                        message.observe(requireActivity()) {
                            when (it) {
                                "SUCCESS" -> {
                                    Toast.makeText(
                                        requireContext(),
                                        "Success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            HomeActivity::class.java
                                        )
                                    )
                                    requireActivity().finish()
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
                                        it,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }

            }
            homeViewModel.currentUserLiveData.observe(viewLifecycleOwner) {
                if (it == null) {
                    btnToLogin.isClickable = true
                    btnLogout.visibility = View.GONE
                    btnToChangeAccount.visibility = View.GONE
                    btnToChangePassword.visibility = View.GONE
                    tvName.text = "Masuk ke akunmu"
                    tvEmail.text = "Masuk agar dapat simpan resep dan review."
                } else {
                    btnToLogin.isClickable = false
                    btnLogout.visibility = View.VISIBLE
                    btnToChangeAccount.visibility = View.VISIBLE
                    btnToChangePassword.visibility = View.VISIBLE
                    tvName.text = it.displayName
                    tvEmail.text = it.email
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