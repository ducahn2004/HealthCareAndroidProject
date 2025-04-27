package com.example.healthcareproject.present.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.present.MainActivity
import com.example.healthcareproject.R
import kotlinx.coroutines.launch

class CreateNewPasswordFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_new_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = view.findViewById<EditText>(R.id.et_confirm_password)

        view.findViewById<View>(R.id.btn_reset_password).setOnClickListener {
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            lifecycleScope.launch {
                if (password == confirmPassword) {
                    try {
                        val email = viewModel.email.value
                        if (email.isNullOrEmpty()) {
                            Toast.makeText(requireContext(), "Email is required to reset password", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_createNewPasswordFragment_to_loginFragment)
                            return@launch
                        }

                        // Use resetPassword instead of updatePassword
                        viewModel.resetPassword(password)
                        Toast.makeText(requireContext(), "Password reset successfully. Please log in.", Toast.LENGTH_LONG).show()

                        // Navigate to login screen instead of MainActivity
                        findNavController().navigate(R.id.action_createNewPasswordFragment_to_loginFragment)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to reset password: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    etConfirmPassword.error = "Passwords do not match"
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}