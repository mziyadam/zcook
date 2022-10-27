package com.ziyad.zcook.ui.home.settings.change_account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ActivityChangeAccountBinding
import com.ziyad.zcook.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeAccountBinding
    private val changeAccountViewModel: ChangeAccountViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            val currentUser = changeAccountViewModel.currentUserLiveData.value
            etName.setText(currentUser?.displayName)
            etEmail.setText(currentUser?.email)
            btnSave.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                if (name == "" || email == "") {
                    Toast.makeText(
                        this@ChangeAccountActivity,
                        "Lengkapi data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (!email.isValidEmail()) {
                        Toast.makeText(
                            this@ChangeAccountActivity,
                            "Email tidak benar",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        lifecycleScope.launch(Dispatchers.IO){
                            val message=changeAccountViewModel.changeEmailAndName(email,name)
                            lifecycleScope.launch(Dispatchers.Main){
                                message.observe(this@ChangeAccountActivity){
                                    when (it) {
                                        "SUCCESS" -> {
                                            Toast.makeText(
                                                this@ChangeAccountActivity,
                                                "Success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        finish()
                                        }
                                        "LOADING" -> {
                                            Toast.makeText(
                                                this@ChangeAccountActivity,
                                                "Loading",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else->{
                                            Toast.makeText(
                                                this@ChangeAccountActivity,
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