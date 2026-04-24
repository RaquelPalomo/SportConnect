package com.example.sportconnect.ui.professional

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportconnect.R
import com.example.sportconnect.databinding.ActivityProfessionalMainBinding
import com.example.sportconnect.viewmodel.ProfesionalUiState
import com.example.sportconnect.viewmodel.ProfesionalViewModel
import com.google.android.material.tabs.TabLayout

class ProfessionalMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessionalMainBinding
    private val viewModel: ProfesionalViewModel by viewModels()
    private lateinit var adapter: ProfesionalActividadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfessionalMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        configurarTabs()
        configurarRecycler()
        configurarObservadores()
        configurarBotones()

        viewModel.cargarMisActividades()
    }

    private fun configurarTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pendientes"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Completadas"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.filtrarPorEstado(soloFuturas = tab.position == 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun configurarRecycler() {
        adapter = ProfesionalActividadAdapter(emptyList()) { actividad ->
            // Detalle de actividad profesional — próxima mejora
        }
        binding.recyclerMisActividades.layoutManager = LinearLayoutManager(this)
        binding.recyclerMisActividades.adapter = adapter
    }

    private fun configurarBotones() {
        binding.fabCrearActividad.setOnClickListener {
            startActivity(Intent(this, CrearActividadActivity::class.java))
        }
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is ProfesionalUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerMisActividades.visibility = View.GONE
                    binding.tvSinActividades.visibility = View.GONE
                }
                is ProfesionalUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (estado.actividades.isEmpty()) {
                        binding.recyclerMisActividades.visibility = View.GONE
                        binding.tvSinActividades.visibility = View.VISIBLE
                    } else {
                        binding.recyclerMisActividades.visibility = View.VISIBLE
                        binding.tvSinActividades.visibility = View.GONE
                        adapter.actualizarLista(estado.actividades)
                    }
                }
                is ProfesionalUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvSinActividades.text = estado.mensaje
                    binding.tvSinActividades.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarMisActividades()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profesional, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cerrar_sesion -> {
                com.example.sportconnect.data.repository.AuthRepository().cerrarSesion()
                startActivity(
                    Intent(this, com.example.sportconnect.ui.auth.LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}