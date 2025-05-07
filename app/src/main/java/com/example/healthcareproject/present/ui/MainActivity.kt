package com.example.healthcareproject.present.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy NavHostFragment và NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Thiết lập BottomNavigationView với NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Handle navigation from notification
        intent.getStringExtra("navigate_to")?.let { destination ->
            when (destination) {
                "heart_rate" -> navController.navigate(R.id.action_global_heartRateFragment)
                "oxygen" -> navController.navigate(R.id.action_global_oxygenFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}