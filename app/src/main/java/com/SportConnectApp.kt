package com.example.sportconnect

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class SportConnectApp : Application() {

    override fun onCreate() {
        super.onCreate()
        configurarFirestore()
    }

    private fun configurarFirestore() {
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(50 * 1024 * 1024) // 50MB de caché
                    .build()
            )
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}