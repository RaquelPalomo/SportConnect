package com.example.sportconnect.ui.basic

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportconnect.R
import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.databinding.ActivityBasicMainBinding
import com.example.sportconnect.viewmodel.ActividadUiState
import com.example.sportconnect.viewmodel.ActividadViewModel

class BasicMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicMainBinding
    private val viewModel: ActividadViewModel by viewModels()
    private lateinit var adapter: ActividadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        configurarRecycler()
        configurarObservadores()
        configurarBotones()

        viewModel.cargarActividades()
    }

    private fun configurarRecycler() {
        adapter = ActividadAdapter(emptyList()) { actividad ->
            navegarADetalle(actividad)
        }
        binding.recyclerActividades.layoutManager = LinearLayoutManager(this)
        binding.recyclerActividades.adapter = adapter
    }

    private fun configurarBotones() {
        binding.btnFiltrar.setOnClickListener {
            val tipo = binding.etFiltroTipo.text.toString().trim()
            val poblacion = binding.etFiltroPoblacion.text.toString().trim()
            viewModel.filtrarActividades(tipo, poblacion)
        }
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is ActividadUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerActividades.visibility = View.GONE
                    binding.tvSinActividades.visibility = View.GONE
                }
                is ActividadUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (estado.actividades.isEmpty()) {
                        binding.recyclerActividades.visibility = View.GONE
                        binding.tvSinActividades.visibility = View.VISIBLE
                    } else {
                        binding.recyclerActividades.visibility = View.VISIBLE
                        binding.tvSinActividades.visibility = View.GONE
                        adapter.actualizarLista(estado.actividades)
                    }
                }
                is ActividadUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvSinActividades.text = estado.mensaje
                    binding.tvSinActividades.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    private fun navegarADetalle(actividad: Actividad) {
        viewModel.seleccionarActividad(actividad)
        startActivity(Intent(this, DetalleActividadActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_basic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_mis_reservas -> {
                startActivity(Intent(this, MisReservasActivity::class.java))
                true
            }
            R.id.action_cerrar_sesion -> {
                com.example.sportconnect.data.repository.AuthRepository().cerrarSesion()
                startActivity(Intent(this, com.example.sportconnect.ui.auth.LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}