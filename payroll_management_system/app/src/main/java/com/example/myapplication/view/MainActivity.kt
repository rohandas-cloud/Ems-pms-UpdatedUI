package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.viewmodel.LoginState
import com.example.myapplication.viewmodel.LoginViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        etEmail = findViewById(R.id.editTextTextEmailAddress)
        etPassword = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Setup login button click listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // viewModel.dualLogin(email, password)
            // Bypassing login for now
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Observe login state
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    btnLogin.isEnabled = false
                    btnLogin.text = getString(R.string.logging_in)
                }
                is LoginState.Success -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.btn_login)
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Dual login successful - PMS empId: ${MyApplication.sessionManager.fetchEmpIdPms()}, EMS empId: ${MyApplication.sessionManager.fetchEmpIdEms()}")
                    
                    // Navigate to SecondActivity (Dashboard)
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is LoginState.PartialSuccess -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.btn_login)
                    Toast.makeText(
                        this,
                        getString(R.string.partial_login, state.result.errorMessage),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w("MainActivity", "Partial login - ${state.result.errorMessage}")
                    
                    // Still navigate but warn user
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is LoginState.Error -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.btn_login)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    Log.e("MainActivity", "Login error: ${state.message}")
                }
            }
        }
    }
}
