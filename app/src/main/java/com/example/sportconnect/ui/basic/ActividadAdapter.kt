package com.example.sportconnect.ui.basic

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
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
        val estaCancelada = !actividad.activa

        with(holder.binding) {
            tvTipo.text = actividad.tipo
            tvProfesor.text = "Prof: ${actividad.profesorNombre}"
            tvLugar.text = "${actividad.lugar}, ${actividad.poblacion}"
            tvFecha.text = actividad.fecha?.let { formato.format(it.toDate()) } ?: "Sin fecha"
            tvPlazas.text = "Plazas: ${actividad.participantesActuales}/${actividad.maxParticipantes}"
            tvDuracion.text = "${actividad.duracionMinutos} min"

            // Mostrar aviso si la clase está cancelada
            if (estaCancelada) {
                layoutCancelada.visibility = View.VISIBLE
                viewFranja.setBackgroundColor(
                    holder.itemView.context.getColor(android.R.color.holo_red_light)
                )
                tvTipo.alpha = 0.5f
                tvProfesor.alpha = 0.5f
                tvLugar.alpha = 0.5f
                tvFecha.alpha = 0.5f
                tvPlazas.alpha = 0.5f
                tvDuracion.alpha = 0.5f
            } else {
                layoutCancelada.visibility = View.GONE
                viewFranja.setBackgroundColor(
                    holder.itemView.context.getColor(
                        android.R.color.transparent
                    ).also {
                        viewFranja.background =
                            holder.itemView.context.getDrawable(
                                android.R.color.transparent
                            )
                        viewFranja.setBackgroundColor(0xFFFF6B35.toInt())
                    }
                )
                tvTipo.alpha = 1f
                tvProfesor.alpha = 1f
                tvLugar.alpha = 1f
                tvFecha.alpha = 1f
                tvPlazas.alpha = 1f
                tvDuracion.alpha = 1f
            }

            if (estaCancelada) {
                root.setOnClickListener(null)
                root.isClickable = false
            } else {
                root.isClickable = true
                root.setOnClickListener { onItemClick(actividad) }
            }
        }
    }

    override fun getItemCount() = actividades.size

    fun actualizarLista(nuevasActividades: List<Actividad>) {
        actividades = nuevasActividades
        notifyDataSetChanged()
    }
}