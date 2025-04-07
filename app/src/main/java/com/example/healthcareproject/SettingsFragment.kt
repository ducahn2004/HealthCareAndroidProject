package com.example.healthcareproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.healthcareproject.ui.auth.AuthActivity

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nút "Logout"
        view.findViewById<View>(R.id.btn_logout).setOnClickListener {
            // Đặt lại trạng thái đăng nhập
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
            val editor = sharedPreferences.edit()
            editor.putBoolean("is_logged_in", false)
            editor.apply()

            // Chuyển về AuthActivity
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish() // Kết thúc MainActivity
        }
    }
}