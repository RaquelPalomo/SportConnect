package com.example.sportconnect.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportconnect.data.model.Usuario
import com.example.sportconnect.databinding.ItemUsuarioAdminBinding

class UsuarioAdminAdapter(
    private var usuarios: List<Usuario>,
    private val onEliminarClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdminAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUsuarioAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUsuarioAdminBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = usuarios[position]
        with(holder.binding) {
            tvNombre.text = usuario.nombre
            tvEmail.text = usuario.email
            tvRol.text = "Rol: ${usuario.rol}"
            btnEliminarUsuario.setOnClickListener { onEliminarClick(usuario) }
        }
    }

    override fun getItemCount() = usuarios.size

    fun actualizarLista(nuevosUsuarios: List<Usuario>) {
        usuarios = nuevosUsuarios
        notifyDataSetChanged()
    }
}