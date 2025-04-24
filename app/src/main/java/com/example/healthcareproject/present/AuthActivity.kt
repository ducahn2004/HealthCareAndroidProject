package com.example.healthcareproject.present

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.healthcareproject.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as? NavHostFragment
        if (navHostFragment == null) {
            Timber.tag("AuthActivity").e("NavHostFragment not found at R.id.fragment_container")
            finish() // Exit activity to prevent further crashes
            return
        }
        navController = navHostFragment.navController

        intent.getStringExtra("destination")?.let { destination ->
            if (destination == "loginMethodFragment") {
                try {
                    navController.navigate(R.id.loginMethodFragment)
                } catch (e: IllegalArgumentException) {
                    Timber.tag("AuthActivity")
                        .e("Navigation to loginMethodFragment failed: ${e.message}")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}