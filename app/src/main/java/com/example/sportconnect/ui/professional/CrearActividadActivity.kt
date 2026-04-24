package com.example.sportconnect.ui.professional

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sportconnect.databinding.ActivityCrearActividadBinding
import com.example.sportconnect.viewmodel.ProfesionalUiState
import com.example.sportconnect.viewmodel.ProfesionalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearActividadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearActividadBinding
    private val viewModel: ProfesionalViewModel by viewModels()
    private val calendario = Calendar.getInstance()
    private val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm'h'", Locale("es", "ES"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearActividadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nueva actividad"

        configurarBotones()
        configurarObservadores()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun configurarBotones() {
        binding.btnSeleccionarFecha.setOnClickListener {
            mostrarSelectorFecha()
        }

        binding.btnPublicar.setOnClickListener {
            val tipo = binding.etTipo.text.toString().trim()
            val descripcion = binding.etDescripcion.text.toString().trim()
            val lugar = binding.etLugar.text.toString().trim()
            val poblacion = binding.etPoblacion.text.toString().trim()
            val duracion = binding.etDuracion.text.toString().trim().toIntOrNull() ?: 0
            val maxParticipantes = binding.etMaxParticipantes.text.toString().trim().toIntOrNull() ?: 0

            viewModel.crearActividad(
                tipo, descripcion, lugar, poblacion,
                calendario.time, duracion, maxParticipantes
            )
        }
    }

    private fun mostrarSelectorFecha() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendario.set(Calendar.YEAR, year)
                calendario.set(Calendar.MONTH, month)
                calendario.set(Calendar.DAY_OF_MONTH, day)
                mostrarSelectorHora()
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun mostrarSelectorHora() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendario.set(Calendar.HOUR_OF_DAY, hour)
                calendario.set(Calendar.MINUTE, minute)
                binding.tvFechaSeleccionada.text = formato.format(calendario.time)
            },
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun configurarObservadores() {
        viewModel.uiState.observe(this) { estado ->
            when (estado) {
                is ProfesionalUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnPublicar.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is ProfesionalUiState.ActividadCreada -> {
                    binding.progressBar.visibility = View.GONE
                    finish()
                }
                is ProfesionalUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnPublicar.isEnabled = true
                    binding.tvError.text = estado.mensaje
                    binding.tvError.visibility = View.VISIBLE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}