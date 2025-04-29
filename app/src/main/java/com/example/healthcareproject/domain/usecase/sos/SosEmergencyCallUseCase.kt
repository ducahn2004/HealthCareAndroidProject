package com.example.healthcareproject.domain.usecase.sos

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import javax.inject.Inject

class SosEmergencyCallUseCase @Inject constructor(
    private val context: Context
) {
    fun call(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = "tel:$phoneNumber".toUri()
            }
            context.startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission denied to make calls", Toast.LENGTH_SHORT).show()
        }
    }
}