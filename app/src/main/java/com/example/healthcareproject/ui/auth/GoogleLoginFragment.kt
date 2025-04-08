package com.example.healthcareproject.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.MainActivity
import com.example.healthcareproject.R

class GoogleLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_gg_login_to_login_method)

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_googleLoginFragment_to_loginMethodFragment)
        }
        // Giả lập đăng nhập bằng Google (thay bằng logic thực tế)
        view.findViewById<View>(R.id.btn_google_login).setOnClickListener {
            saveLoginState(true)
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }
}