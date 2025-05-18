package com.example.healthcareproject.presentation.ui.activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.ActivityMainBinding
import com.example.healthcareproject.presentation.service.ForegroundServiceStarter
import com.example.healthcareproject.presentation.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "App needs notification permission to send reminders", Toast.LENGTH_LONG).show()
            }
        }

    private val requestCallPhonePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Call permission is required for alerts!", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestBodySensorsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Body sensors permission is required!", Toast.LENGTH_LONG).show()
            }
        }

    private val requestActivityRecognitionPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Activity recognition permission is required!", Toast.LENGTH_LONG).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        if (PermissionManager.shouldRequestNotificationPermission()
            && !PermissionManager.hasPermission(this, PermissionManager.REQUEST_POST_NOTIFICATIONS)
        ) {
            requestNotificationPermissionLauncher.launch(PermissionManager.REQUEST_POST_NOTIFICATIONS)
        }

        if (!PermissionManager.hasPermission(this, PermissionManager.REQUEST_CALL_PHONE)) {
            requestCallPhonePermissionLauncher.launch(PermissionManager.REQUEST_CALL_PHONE)
        }

        if (!PermissionManager.hasPermission(this, PermissionManager.REQUEST_BODY_SENSORS)) {
            requestBodySensorsPermissionLauncher.launch(PermissionManager.REQUEST_BODY_SENSORS)
        }

        if (!PermissionManager.hasPermission(this, PermissionManager.REQUEST_ACTIVITY_RECOGNITION)) {
            requestActivityRecognitionPermissionLauncher.launch(PermissionManager.REQUEST_ACTIVITY_RECOGNITION)
        }

        PermissionManager.checkAndRequestExactAlarmPermission(this)

        ForegroundServiceStarter.startMeasurementService(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
