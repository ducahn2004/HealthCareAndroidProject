package com.example.healthcareproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.healthcareproject.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedTheme = loadThemePreference()
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        val sharedPreferences = getSharedPreferences("user_prefs", 0)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }

    private fun loadThemePreference(): Int {
        val sharedPreferences = getSharedPreferences("theme_prefs", 0)
        // Lấy theme_mode kiểu int, mặc định là MODE_NIGHT_FOLLOW_SYSTEM
        return sharedPreferences.getInt(
            "theme_mode",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }
}