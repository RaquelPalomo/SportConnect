package com.example.sportconnect.ui.basic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.databinding.ItemActividadBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ActividadAdapter(
    private var actividades: List<Actividad>,
    private val onItemClick: (Actividad) -> Unit
) : RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    inner class ActividadViewHolder(val binding: ItemActividadBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val binding = ItemActividadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ActividadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]
        val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm'h'", Locale("es", "ES"))

        with(holder.binding) {
            tvTipo.text = actividad.tipo
            tvProfesor.text = "Prof: ${actividad.profesorNombre}"
            tvLugar.text = "${actividad.lugar}, ${actividad.poblacion}"
            tvFecha.text = actividad.fecha?.let { formato.format(it.toDate()) } ?: "Sin fecha"
            tvPlazas.text = "Plazas: ${actividad.participantesActuales}/${actividad.maxParticipantes}"
            tvDuracion.text = "${actividad.duracionMinutos} min"

            root.setOnClickListener { onItemClick(actividad) }
        }
    }

    override fun getItemCount() = actividades.size

    fun actualizarLista(nuevasActividades: List<Actividad>) {
        actividades = nuevasActividades
        notifyDataSetChanged()
    }
}