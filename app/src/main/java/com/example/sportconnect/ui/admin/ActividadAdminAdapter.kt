package com.example.sportconnect.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.databinding.ItemActividadAdminBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ActividadAdminAdapter(
    private var actividades: List<Actividad>,
    private val onEliminarClick: (Actividad) -> Unit
) : RecyclerView.Adapter<ActividadAdminAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemActividadAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActividadAdminBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actividad = actividades[position]
        val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm'h'", Locale("es", "ES"))
        with(holder.binding) {
            tvTipo.text = actividad.tipo
            tvProfesor.text = "Prof: ${actividad.profesorNombre}"
            tvFecha.text = actividad.fecha?.let { formato.format(it.toDate()) } ?: "Sin fecha"
            btnEliminarActividad.setOnClickListener { onEliminarClick(actividad) }
        }
    }

    override fun getItemCount() = actividades.size

    fun actualizarLista(nuevasActividades: List<Actividad>) {
        actividades = nuevasActividades
        notifyDataSetChanged()
    }
}