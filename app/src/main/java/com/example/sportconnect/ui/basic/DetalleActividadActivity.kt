package com.example.sportconnect.ui.basic

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sportconnect.databinding.ActivityDetalleActividadBinding
import com.example.sportconnect.viewmodel.ActividadUiState
import com.example.sportconnect.viewmodel.ActividadViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetalleActividadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleActividadBinding
    private val viewModel: ActividadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleActividadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle de actividad"

        configurarObservadores()
        configurarBotones()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun configurarObservadores() {
        viewModel.actividadDetalle.observe(this) { actividad ->
            actividad?.let {
                val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm'h'", Locale("es", "ES"))
                with(binding) {
                    tvTipo.text = it.tipo
                    tvDescripcion.text = it.descripcion
                    tvProfesor.text = "Profesor: ${it.profesorNombre}"
                    tvLugar.text = "Lugar: ${it.lugar}, ${it.poblacion}"
                    tvFecha.text = "Fecha: ${it.fecha?.let { f -> formato.format(f.toDate()) } ?: "Sin fecha"}"
                    tvDuracion.text = "Duración: ${it.duracionMinutos} minutos"
                    tvPlazas.text = "Plazas disponibles: ${it.maxParticipantes - it.participantesActuales}/${it.maxParticipantes}"

                    if (it.participantesActuales >= it.maxParticipantes) {
                        btnReservar.isEnabled = false
                        btnReservar.text = "Sin plazas disponibles"
                    }
                }
            }
        }

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
            viewModel.actividadDetalle.value?.id?.let { id ->
                viewModel.reservarActividad(id)
            }
        }
    }
}