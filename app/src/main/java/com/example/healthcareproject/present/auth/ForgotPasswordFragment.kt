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
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R

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

        // Set the auth flow type
        viewModel.setAuthFlow(AuthViewModel.AuthFlow.FORGOT_PASSWORD)

        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_forgot_password_to_login_method)

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginMethodFragment)
        }

        // Set up observers
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state (maybe show/hide a progress indicator)
            view.findViewById<View>(R.id.btn_send_reset).isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setError("") // Reset error state
            }
        }

        // Navigate when email is successfully sent
        viewModel.email.observe(viewLifecycleOwner) { email ->
            if (!email.isNullOrEmpty() && viewModel.authFlow.value == AuthViewModel.AuthFlow.FORGOT_PASSWORD) {
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_verifyCodeFragment)
            }
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

            // Call the ViewModel method
            viewModel.sendPasswordResetEmail(email)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up any references or observers if needed
    }
}