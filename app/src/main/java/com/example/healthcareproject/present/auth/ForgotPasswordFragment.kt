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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()
    private var emailSent = false

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
        Timber.d("ForgotPasswordFragment: Setting auth flow to FORGOT_PASSWORD")

        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val btnSendReset = view.findViewById<View>(R.id.btn_send_reset)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_forgot_password_to_login_method)

        // Set initial UI state
        btnSendReset.isEnabled = viewModel.isLoading.value != true

        setupObservers(view)

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginMethodFragment)
        }

        btnSendReset.setOnClickListener {
            val email = etEmail.text.toString()

            // Client-side validation
            if (email.isBlank()) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Invalid email format"
                return@setOnClickListener
            }

            // Clear any previous errors
            etEmail.error = null

            // Set email in ViewModel and call sendPasswordResetEmail
            viewModel.setEmail(email)
            viewModel.sendPasswordResetEmail(email)
            Timber.d("Attempting to send password reset email to: $email")
        }
    }

    private fun setupObservers(view: View) {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<View>(R.id.btn_send_reset).isEnabled = !isLoading
            // Optionally show/hide a progress indicator
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Timber.e("Error in ForgotPasswordFragment: $errorMessage")
                Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show()
                // Clear the error after showing it
                viewModel.setError("")
            }
        }

        // Observe email (successful email send indicator)
        viewModel.email.observe(viewLifecycleOwner) { email ->
            if (!email.isNullOrEmpty() && !emailSent &&
                viewModel.authFlow.value == AuthViewModel.AuthFlow.FORGOT_PASSWORD) {
                Timber.d("Email set to $email, navigating to verification")
                emailSent = true
                try {
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_verifyCodeFragment)
                } catch (e: Exception) {
                    Timber.e(e, "Navigation error")
                    Toast.makeText(
                        requireContext(),
                        "Navigation error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Timber.d("Email observer triggered but conditions not met: email=$email, emailSent=$emailSent, authFlow=${viewModel.authFlow.value}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Reset the email sent flag when view is destroyed
        emailSent = false
    }
}