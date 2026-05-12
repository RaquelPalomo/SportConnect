package com.example.sportconnect.ui.professional

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sportconnect.databinding.ActivityEditarActividadBinding
import com.example.sportconnect.viewmodel.ProfesionalUiState
import com.example.sportconnect.viewmodel.ProfesionalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditarActividadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarActividadBinding
    private val viewModel: ProfesionalViewModel by viewModels()
    private val calendario = Calendar.getInstance()
    private val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm'h'", Locale("es", "ES"))
    private lateinit var actividadId: String

    private val tiposActividad = listOf(
        "Crossfit", "Entrenamiento personal", "Musculación", "HIIT",
        "Functional training", "TRX", "Calistenia", "Pilates", "Stretching",
        "Yoga", "Meditación", "Tai Chi", "Chi Kung",
        "Zumba", "Baile flamenco", "Baile contemporáneo", "Salsa", "Bachata", "Sevillanas",
        "Fútbol sala", "Baloncesto", "Voleibol", "Pádel", "Tenis",
        "Boxeo", "Kickboxing", "Karate", "Judo", "Taekwondo", "MMA"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarActividadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Editar actividad"

        actividadId = intent.getStringExtra("actividadId") ?: ""

        configurarDropdownTipo()
        cargarDatosExistentes()
        configurarBotones()
        configurarObservadores()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun configurarDropdownTipo() {
        val adapterDropdown = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            tiposActividad
        )
        binding.etTipo.setAdapter(adapterDropdown)
    }

    private fun cargarDatosExistentes() {
        binding.etTipo.setText(intent.getStringExtra("tipo") ?: "", false)
        binding.etDescripcion.setText(intent.getStringExtra("descripcion") ?: "")
        binding.etLugar.setText(intent.getStringExtra("lugar") ?: "")
        binding.etPoblacion.setText(intent.getStringExtra("poblacion") ?: "")
        binding.etDuracion.setText(intent.getIntExtra("duracionMinutos", 0).toString())
        binding.etMaxParticipantes.setText(intent.getIntExtra("maxParticipantes", 0).toString())

        val fechaMillis = intent.getLongExtra("fechaMillis", 0L)
        if (fechaMillis > 0) {
            calendario.timeInMillis = fechaMillis
            binding.tvFechaSeleccionada.text = formato.format(calendario.time)
        }
    }

    private fun configurarBotones() {
        binding.btnSeleccionarFecha.setOnClickListener {
            mostrarSelectorFecha()
        }

        binding.btnPublicar.text = "Guardar cambios"
        binding.btnPublicar.setOnClickListener {
            val tipo = binding.etTipo.text.toString().trim()
            val descripcion = binding.etDescripcion.text.toString().trim()
            val lugar = binding.etLugar.text.toString().trim()
            val poblacion = binding.etPoblacion.text.toString().trim()
            val duracion = binding.etDuracion.text.toString().trim().toIntOrNull() ?: 0
            val maxParticipantes = binding.etMaxParticipantes.text.toString().trim().toIntOrNull() ?: 0

            viewModel.editarActividad(
                actividadId, tipo, descripcion, lugar, poblacion,
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
                is ProfesionalUiState.ActividadEditada -> {
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