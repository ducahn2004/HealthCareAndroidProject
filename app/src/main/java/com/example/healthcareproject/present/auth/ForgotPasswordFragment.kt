package com.example.healthcareproject.present.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_forgot_password_to_login_method)

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginMethodFragment)
        }

        view.findViewById<View>(R.id.btn_send_reset).setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isBlank()) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Invalid email format"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    viewModel.sendPasswordResetEmail(email)
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_verifyCodeFragment)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to send reset email: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}