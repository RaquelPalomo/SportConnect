package com.example.sportconnect.ui.professional

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.databinding.ItemActividadProfesionalBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfesionalActividadAdapter(
    private var actividades: List<Actividad>,
    private val onItemClick: (Actividad) -> Unit
) : RecyclerView.Adapter<ProfesionalActividadAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemActividadProfesionalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActividadProfesionalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = actividades[position]
        val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm'h'", Locale("es", "ES"))
        val ahora = Date()
        val esFutura = actividad.fecha?.toDate()?.after(ahora) == true

        with(holder.binding) {
            tvTipo.text = actividad.tipo
            tvFecha.text = actividad.fecha?.let { formato.format(it.toDate()) } ?: "Sin fecha"
            tvLugar.text = "${actividad.lugar}, ${actividad.poblacion}"
            tvInscritos.text = "Inscritos: ${actividad.participantesActuales}/${actividad.maxParticipantes}"

            if (esFutura) {
                tvEstado.text = "Pendiente"
                tvEstado.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                tvEstado.text = "Completada"
                tvEstado.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
            }

            root.setOnClickListener { onItemClick(actividad) }
        }
    }

    override fun getItemCount() = actividades.size

    fun actualizarLista(nuevasActividades: List<Actividad>) {
        actividades = nuevasActividades
        notifyDataSetChanged()
    }
}