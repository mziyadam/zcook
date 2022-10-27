package com.ziyad.zcook.ui.home.settings.change_password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ActivityChangePasswordBinding
import com.ziyad.zcook.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnSave.setOnClickListener {
                val oldPassword = etOldPassword.text.toString()
                val password = etPassword.text.toString()
                val reenterPassword = etReenterPassword.text.toString()
                if (oldPassword == "" || password == "" || reenterPassword == "") {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "Lengkapi data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (password != reenterPassword) {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Password dan Re-enter Password tidak cocok",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val message =
                                changePasswordViewModel.changePassword(oldPassword, password)
                            lifecycleScope.launch(Dispatchers.Main) {
                                message.observe(this@ChangePasswordActivity) {
                                    when (it) {
                                        "SUCCESS" -> {
                                            Toast.makeText(
                                                this@ChangePasswordActivity,
                                                "Success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                        "LOADING" -> {
                                            Toast.makeText(
                                                this@ChangePasswordActivity,
                                                "Loading",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else -> {
                                            Toast.makeText(
                                                this@ChangePasswordActivity,
                                                it,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}