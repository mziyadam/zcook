package com.ziyad.zcook.ui.auth.reset_password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ActivityResetPasswordBinding
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.ui.auth.register.RegisterActivity
import com.ziyad.zcook.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()
    private lateinit var binding: ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnSendEmail.setOnClickListener {
                val email = etEmail.text.toString()
                if (email == "" || !email.isValidEmail()) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Masukkan email yang benar",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val message = resetPasswordViewModel.resetPassword(email)
                        lifecycleScope.launch(Dispatchers.Main) {
                            message.observe(this@ResetPasswordActivity) {
                                when (it) {
                                    "SUCCESS" -> {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            "Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                    "LOADING" -> {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            "Loading",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
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
            btnBack.setOnClickListener {
                startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                finish()
            }
            tvToRegister.setOnClickListener {
                startActivity(Intent(this@ResetPasswordActivity, RegisterActivity::class.java))
                finish()
            }
        }
    }
}