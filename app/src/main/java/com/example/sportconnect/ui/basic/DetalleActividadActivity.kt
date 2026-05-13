package com.example.sportconnect.ui.basic

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sportconnect.databinding.ActivityDetalleActividadBinding
import com.example.sportconnect.viewmodel.ActividadUiState
import com.example.sportconnect.viewmodel.ActividadViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleActividadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleActividadBinding
    private val viewModel: ActividadViewModel by viewModels()
    private var actividadId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleActividadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle de actividad"

        cargarDatosDesdeIntent()
        configurarObservadores()
        configurarBotones()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun cargarDatosDesdeIntent() {
        actividadId = intent.getStringExtra("actividadId") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val profesorNombre = intent.getStringExtra("profesorNombre") ?: ""
        val lugar = intent.getStringExtra("lugar") ?: ""
        val poblacion = intent.getStringExtra("poblacion") ?: ""
        val duracionMinutos = intent.getIntExtra("duracionMinutos", 0)
        val maxParticipantes = intent.getIntExtra("maxParticipantes", 0)
        val participantesActuales = intent.getIntExtra("participantesActuales", 0)
        val fechaMillis = intent.getLongExtra("fechaMillis", 0L)

        val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm'h'", Locale("es", "ES"))

        with(binding) {
            tvTipo.text = tipo
            tvDescripcion.text = descripcion
            tvProfesor.text = "Profesor: $profesorNombre"
            tvLugar.text = "Lugar: $lugar, $poblacion"
            tvFecha.text = "Fecha: ${if (fechaMillis > 0) formato.format(Date(fechaMillis)) else "Sin fecha"}"
            tvDuracion.text = "Duración: $duracionMinutos minutos"
            tvPlazas.text = "Plazas disponibles: ${maxParticipantes - participantesActuales}/$maxParticipantes"

            if (participantesActuales >= maxParticipantes) {
                btnReservar.isEnabled = false
                btnReservar.text = "Sin plazas disponibles"
            }
        }
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is ActividadUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnReservar.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is ActividadUiState.ReservaExitosa -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnReservar.isEnabled = false
                    binding.btnReservar.text = "Plaza reservada"
                    binding.tvError.setTextColor(getColor(android.R.color.holo_green_dark))
                    binding.tvError.text = "¡Reserva realizada con éxito!"
                    binding.tvError.visibility = View.VISIBLE
                }
                is ActividadUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnReservar.isEnabled = true
                    binding.tvError.setTextColor(getColor(android.R.color.holo_red_dark))
                    binding.tvError.text = estado.mensaje
                    binding.tvError.visibility = View.VISIBLE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun configurarBotones() {
        binding.btnReservar.setOnClickListener {
            if (actividadId.isNotEmpty()) {
                viewModel.reservarActividad(actividadId)
            }
        }
    }
}