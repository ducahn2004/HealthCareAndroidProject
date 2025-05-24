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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val permissionsQueue = mutableListOf<String>()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val currentPermission = permissionsQueue.removeFirstOrNull()
            if (!isGranted && currentPermission != null) {
                Toast.makeText(this, "Permission $currentPermission is required!", Toast.LENGTH_SHORT).show()
            }

            requestNextPermission()
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

        requestAllPermissionsSequentially()

        PermissionManager.checkAndRequestExactAlarmPermission(this)

        startMonitoringServiceIfLoggedIn()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestAllPermissionsSequentially() {
        permissionsQueue.clear()

        if (PermissionManager.shouldRequestNotificationPermission()
            && !PermissionManager.hasPermission(this, PermissionManager.REQUEST_POST_NOTIFICATIONS)
        ) {
            permissionsQueue.add(PermissionManager.REQUEST_POST_NOTIFICATIONS)
        }

        if (!PermissionManager.hasPermission(this, PermissionManager.REQUEST_CALL_PHONE)) {
            permissionsQueue.add(PermissionManager.REQUEST_CALL_PHONE)
        }

        if (!PermissionManager.hasPermission(this, PermissionManager.REQUEST_BODY_SENSORS)) {
            permissionsQueue.add(PermissionManager.REQUEST_BODY_SENSORS)
        }

        if (!PermissionManager.hasPermission(this, PermissionManager.REQUEST_ACTIVITY_RECOGNITION)) {
            permissionsQueue.add(PermissionManager.REQUEST_ACTIVITY_RECOGNITION)
        }

        requestNextPermission()
    }

    private fun requestNextPermission() {
        if (permissionsQueue.isNotEmpty()) {
            val permission = permissionsQueue.first()
            permissionLauncher.launch(permission)
        }
    }

    private fun startMonitoringServiceIfLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            ForegroundServiceStarter.startMeasurementService(this)
        } else {
            Timber.tag("MainActivity").d("User not logged in, not starting service.")
        }
    }

}
