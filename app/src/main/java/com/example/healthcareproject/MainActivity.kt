package com.example.healthcareproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.healthcareproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.explore -> {
                    replaceFragment(ExploreFragment())
                    true
                }
                R.id.medicine -> {
                    replaceFragment(MedicineFragment())
                    true
                }
                R.id.home_page -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.notification-> {
                    replaceFragment(NotificationFragment())
                    true
                }
                R.id.setting -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
