package com.example.sportconnect.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportconnect.R
import com.example.sportconnect.databinding.ActivityAdminMainBinding
import com.example.sportconnect.viewmodel.AdminUiState
import com.example.sportconnect.viewmodel.AdminViewModel
import com.google.android.material.tabs.TabLayout

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var usuarioAdapter: UsuarioAdminAdapter
    private lateinit var actividadAdapter: ActividadAdminAdapter
    private var tabActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        configurarTabs()
        configurarAdapters()
        configurarObservadores()

        viewModel.cargarUsuarios()
    }

    private fun configurarTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Usuarios"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Actividades"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabActual = tab.position
                if (tab.position == 0) {
                    viewModel.cargarUsuarios()
                } else {
                    viewModel.cargarActividades()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun configurarAdapters() {
        usuarioAdapter = UsuarioAdminAdapter(emptyList()) { usuario ->
            mostrarDialogoEliminarUsuario(usuario.uid, usuario.nombre)
        }
        actividadAdapter = ActividadAdminAdapter(emptyList()) { actividad ->
            mostrarDialogoEliminarActividad(actividad.id, actividad.tipo)
        }
        binding.recyclerAdmin.layoutManager = LinearLayoutManager(this)
        binding.recyclerAdmin.adapter = usuarioAdapter
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is AdminUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerAdmin.visibility = View.GONE
                    binding.tvSinDatos.visibility = View.GONE
                }
                is AdminUiState.UsuariosSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerAdmin.adapter = usuarioAdapter
                    if (estado.usuarios.isEmpty()) {
                        binding.recyclerAdmin.visibility = View.GONE
                        binding.tvSinDatos.visibility = View.VISIBLE
                    } else {
                        binding.recyclerAdmin.visibility = View.VISIBLE
                        binding.tvSinDatos.visibility = View.GONE
                        usuarioAdapter.actualizarLista(estado.usuarios)
                    }
                }
                is AdminUiState.ActividadesSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerAdmin.adapter = actividadAdapter
                    if (estado.actividades.isEmpty()) {
                        binding.recyclerAdmin.visibility = View.GONE
                        binding.tvSinDatos.visibility = View.VISIBLE
                    } else {
                        binding.recyclerAdmin.visibility = View.VISIBLE
                        binding.tvSinDatos.visibility = View.GONE
                        actividadAdapter.actualizarLista(estado.actividades)
                    }
                }
                is AdminUiState.EliminacionExitosa -> {
                    binding.progressBar.visibility = View.GONE
                    if (tabActual == 0) viewModel.cargarUsuarios()
                    else viewModel.cargarActividades()
                }
                is AdminUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvSinDatos.text = estado.mensaje
                    binding.tvSinDatos.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    private fun mostrarDialogoEliminarUsuario(uid: String, nombre: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar usuario")
            .setMessage("¿Estás seguro de que quieres eliminar a $nombre?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.eliminarUsuario(uid) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEliminarActividad(id: String, tipo: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar actividad")
            .setMessage("¿Estás seguro de que quieres eliminar la actividad $tipo?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.eliminarActividad(id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
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