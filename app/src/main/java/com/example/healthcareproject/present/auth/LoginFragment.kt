package com.example.healthcareproject.present.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.present.MainActivity
import com.example.healthcareproject.R

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val tvError = view.findViewById<TextView>(R.id.tv_error)

        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_login_to_login_method)

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_loginMethodFragment)
        }
        // Nút "Login"
        view.findViewById<View>(R.id.btn_login).setOnClickListener {
            viewModel.email = etEmail.text.toString()
            viewModel.password = etPassword.text.toString()

            // Giả sử kiểm tra đăng nhập (thay bằng logic thực tế, ví dụ: gọi API)
            if (viewModel.email == "admin" && viewModel.password == "password123") {
                viewModel.isLoginSuccessful = true
                saveLoginState(true)
                // Chuyển sang MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                tvError.visibility = View.VISIBLE
                tvError.text = "Incorrect password. Please check your password."
            }
        }

        // Nút "Forgot Password"
        view.findViewById<View>(R.id.tv_forgot_password).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        view.findViewById<View>(R.id.google_login_container).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_googleLoginFragment)
        }
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }
}