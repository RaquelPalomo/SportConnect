package com.example.sportconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sportconnect.databinding.ActivityRegistroBinding
import com.example.sportconnect.viewmodel.AuthUiState
import com.example.sportconnect.viewmodel.AuthViewModel

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotones()
        configurarObservadores()
    }

    private fun configurarBotones() {
        binding.btnRegistro.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rol = if (binding.rbProfesional.isChecked) "profesional" else "basico"
            viewModel.registro(nombre, email, password, rol)
        }

        binding.tvIrLogin.setOnClickListener {
            finish()
            viewModel.resetState()
        }
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is AuthUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegistro.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is AuthUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val destino = when (estado.rol) {
                        "profesional" -> Intent(this, com.example.sportconnect.ui.professional.ProfessionalMainActivity::class.java)
                        else -> Intent(this, com.example.sportconnect.ui.basic.BasicMainActivity::class.java)
                    }
                    destino.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(destino)
                    finish()
                }
                is AuthUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegistro.isEnabled = true
                    binding.tvError.text = estado.mensaje
                    binding.tvError.visibility = View.VISIBLE
                }
                is AuthUiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegistro.isEnabled = true
                }
                is AuthUiState.PasswordResetSent -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}