package com.ziyad.zcook.ui.auth.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ActivityRegisterBinding
import com.ziyad.zcook.ui.auth.login.LoginActivity
import com.ziyad.zcook.ui.auth.reset_password.ResetPasswordActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerViewModel.currentUserLiveData.observe(this) {
            if (it != null) {
                finish()
            }
        }
        binding.apply {
            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val name = etName.text.toString()
                val password = etPassword.text.toString()
                val reenterPassword = etReenterPassword.text.toString()
                if (email == "" || name == "" || password == "" || reenterPassword == "") {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Lengkapi data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (password != reenterPassword) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Password dan Re-enter Password tidak cocok",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val message = registerViewModel.register(email, name, password)
                            lifecycleScope.launch(Dispatchers.Main) {
                                message.observe(this@RegisterActivity) {
                                    when (it) {
                                        "SUCCESS" -> {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "Success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        "LOADING" -> {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "Loading",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else -> {
                                            Toast.makeText(
                                                this@RegisterActivity,
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

            tvToLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}