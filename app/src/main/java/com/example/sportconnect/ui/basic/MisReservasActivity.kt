package com.example.sportconnect.ui.basic

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportconnect.databinding.ActivityMisReservasBinding
import com.example.sportconnect.viewmodel.ActividadUiState
import com.example.sportconnect.viewmodel.ActividadViewModel

class MisReservasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisReservasBinding
    private val viewModel: ActividadViewModel by viewModels()
    private lateinit var adapter: ActividadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configurarRecycler()
        configurarObservadores()
        viewModel.cargarMisReservas()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun configurarRecycler() {
        adapter = ActividadAdapter(emptyList()) {}
        binding.recyclerMisReservas.layoutManager = LinearLayoutManager(this)
        binding.recyclerMisReservas.adapter = adapter
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is ActividadUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerMisReservas.visibility = View.GONE
                    binding.tvSinReservas.visibility = View.GONE
                }
                is ActividadUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (estado.actividades.isEmpty()) {
                        binding.recyclerMisReservas.visibility = View.GONE
                        binding.tvSinReservas.visibility = View.VISIBLE
                    } else {
                        binding.recyclerMisReservas.visibility = View.VISIBLE
                        binding.tvSinReservas.visibility = View.GONE
                        adapter.actualizarLista(estado.actividades)
                    }
                }
                is ActividadUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvSinReservas.text = estado.mensaje
                    binding.tvSinReservas.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }
}