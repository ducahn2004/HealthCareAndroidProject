package com.example.healthcareproject.present.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.healthcareproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // AuthActivity.kt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as? NavHostFragment
        if (navHostFragment == null) {
            Timber.tag("AuthActivity").e("NavHostFragment not found")
            finish()
            return
        }
        navController = navHostFragment.navController

        intent.getStringExtra("destination")?.let { destination ->
            if (destination == "loginMethodFragment") {
                try {
                    navController.navigate(R.id.loginMethodFragment)
                } catch (e: IllegalArgumentException) {
                    Timber.tag("AuthActivity").e("Navigation failed: ${e.message}")
                }
            }
        }

        handleEmailLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEmailLink(intent)
    }

    private fun handleEmailLink(intent: Intent) {
        val emailLink = intent.data?.toString() ?: return
        if (auth.isSignInWithEmailLink(emailLink)) {
            val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            val email = sharedPreferences.getString("pending_email", "") ?: ""
            val authFlow = sharedPreferences.getString("auth_flow", "REGISTRATION")

            if (email.isBlank()) {
                Timber.e("No email found")
                Snackbar.make(findViewById(R.id.fragment_container), "Email not found", Snackbar.LENGTH_LONG).show()
                return
            }

            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("Signed in with email link")
                        sharedPreferences.edit().remove("pending_email").remove("auth_flow").apply()
                        when (authFlow) {
                            "FORGOT_PASSWORD" -> navController.navigate(R.id.action_global_createNewPasswordFragment)
                            else -> startActivity(Intent(this, MainActivity::class.java)).also { finish() }
                        }
                    } else {
                        Timber.e(task.exception, "Sign-in failed")
                        Snackbar.make(findViewById(R.id.fragment_container), "Sign-in failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}