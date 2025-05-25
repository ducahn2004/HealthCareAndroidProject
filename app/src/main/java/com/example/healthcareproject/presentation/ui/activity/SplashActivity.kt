package com.example.healthcareproject.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedTheme = loadThemePreference()
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        val sharedPreferences = getSharedPreferences("user_prefs", 0)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val isFirebaseLoggedIn = try {
            FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }
        if (isFirebaseLoggedIn != null) {
            sharedPreferences.edit() { putBoolean("is_logged_in", true) }
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            sharedPreferences.edit() { putBoolean("is_logged_in", false) }
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }

    private fun loadThemePreference(): Int {
        val sharedPreferences = getSharedPreferences("theme_prefs", 0)
        return sharedPreferences.getInt(
            "theme_mode",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }
}