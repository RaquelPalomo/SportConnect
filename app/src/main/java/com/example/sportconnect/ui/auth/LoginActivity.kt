package com.example.sportconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportconnect.databinding.ActivityLoginBinding
import com.example.sportconnect.viewmodel.AuthUiState
import com.example.sportconnect.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        configurarBotones()
        configurarObservadores()
    }

    private fun configurarBotones() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        binding.tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
            viewModel.resetState()
        }

        binding.tvOlvidaste.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                viewModel.recuperarPassword(email)
            } else {
                binding.tvError.text = "Introduce tu email para recuperar la contraseña"
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is AuthUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is AuthUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    navegarSegunRol(estado.rol)
                }
                is AuthUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.tvError.text = estado.mensaje
                    binding.tvError.visibility = View.VISIBLE
                }
                is AuthUiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
                is AuthUiState.PasswordResetSent -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.setTextColor(getColor(android.R.color.holo_green_dark))
                    binding.tvError.text = "Email de recuperación enviado"
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navegarSegunRol(rol: String) {
        val destino = when (rol) {
            "profesional" -> Intent(this, com.example.sportconnect.ui.professional.ProfessionalMainActivity::class.java)
            "master" -> Intent(this, com.example.sportconnect.ui.admin.AdminMainActivity::class.java)
            else -> Intent(this, com.example.sportconnect.ui.basic.BasicMainActivity::class.java)
        }
        destino.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(destino)
        finish()
    }
}
