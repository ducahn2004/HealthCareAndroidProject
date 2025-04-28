package com.example.healthcareproject.present.setting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import com.example.healthcareproject.SettingsAdapter
import com.example.healthcareproject.present.AuthActivity

class SettingsFragment : Fragment() {
    private val SETTINGS = listOf(
        SettingItem(1, "Change Theme", R.drawable.ic_theme),
        SettingItem(2, "Information", R.drawable.ic_account),
        SettingItem(3, "Notifications", R.drawable.ic_notification),
        SettingItem(4, "Privacy", R.drawable.ic_privacy),
        SettingItem(5, "Emergency Contacts", R.drawable.ic_emergency),
        SettingItem(6, "Change Password", R.drawable.ic_password),
        SettingItem(7, "Logout", R.drawable.ic_logout)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvSettings = view.findViewById<RecyclerView>(R.id.rv_settings)
        rvSettings.layoutManager = LinearLayoutManager(context)
        rvSettings.adapter = SettingsAdapter(SETTINGS) { item ->
            when (item.id) {
                1 -> findNavController().navigate(R.id.action_settingsFragment_to_themeFragment)
                2 -> findNavController().navigate(R.id.action_settingsFragment_to_informationFragment)
                3 -> { /* Xử lý Notifications */
                }

                4 -> { /* Xử lý Privacy */
                }

                5 -> findNavController().navigate(R.id.action_settingsFragment_to_emergencyFragment)
                6 -> findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
                7 -> showLogoutConfirmationDialog()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun performLogout() {
        requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        val intent = Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra("destination", "loginMethodFragment")
        }
        startActivity(intent)
        requireActivity().finish()
    }
}