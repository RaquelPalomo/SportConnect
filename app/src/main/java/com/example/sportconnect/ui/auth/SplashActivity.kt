package com.example.sportconnect.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Si hay sesión activa consultamos el rol en Firestore
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("usuarios").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    val rol = doc.getString("rol") ?: "basico"
                    val destino = when (rol) {
                        "profesional" -> Intent(this,
                            com.example.sportconnect.ui.professional.ProfessionalMainActivity::class.java)
                        "master" -> Intent(this,
                            com.example.sportconnect.ui.admin.AdminMainActivity::class.java)
                        else -> Intent(this,
                            com.example.sportconnect.ui.basic.BasicMainActivity::class.java)
                    }
                    destino.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(destino)
                    finish()
                }
                .addOnFailureListener {
                    irAlLogin()
                }
        } else {
            irAlLogin()
        }
    }

    private fun irAlLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}