package com.example.healthcareproject.present.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentVerifyCodeBinding
import com.example.healthcareproject.present.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit
import timber.log.Timber

@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    private var _binding: FragmentVerifyCodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        // Setup data binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("VerifyCodeFragment: onViewCreated, authFlow=${viewModel.authFlow.value}")

        // Ensure email is available to prevent null binding errors
        if (viewModel.email.value.isNullOrEmpty()) {
            Timber.e("Email is null or empty in VerifyCodeFragment")
            Snackbar.make(binding.root, "Email is required for verification", Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
            return
        }

        // Start timer for code verification
        viewModel.startTimer()

        setupClickListeners()
        observeViewModelState()
    }

    private fun setupClickListeners() {
        // Handle resend code click
        binding.tvResend.setOnClickListener {
            val email = viewModel.email.value
            if (!email.isNullOrBlank()) {
                try {
                    viewModel.sendPasswordResetEmail(email)
                    viewModel.startTimer() // Restart timer after resending
                    Snackbar.make(binding.root, "Code resent to $email", Snackbar.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Timber.e(e, "Error resending code")
                    Snackbar.make(binding.root, "Failed to resend code: ${e.message}", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(binding.root, "Email not found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModelState() {
        // Observe code verification success
        viewModel.isCodeVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified) {
                Timber.d("Code verified, current authFlow=${viewModel.authFlow.value}")

                try {
                    when (viewModel.authFlow.value) {
                        AuthViewModel.AuthFlow.REGISTRATION, AuthViewModel.AuthFlow.LOGIN -> {
                            saveLoginState(true)
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                        AuthViewModel.AuthFlow.FORGOT_PASSWORD -> {
                            findNavController().navigate(R.id.action_verifyCodeFragment_to_createNewPasswordFragment)
                        }
                        else -> {
                            Timber.e("Invalid authentication flow: ${viewModel.authFlow.value}")
                            Snackbar.make(binding.root, "Invalid authentication flow", Snackbar.LENGTH_LONG).show()
                        }
                    }
                    viewModel.resetNavigationStates()
                } catch (e: Exception) {
                    Timber.e(e, "Error handling verified code")
                    Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in VerifyCodeFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                // Clear the error after showing it
                viewModel.setError("")
            }
        }

        // Observe verification code errors
        viewModel.verificationCodeError.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Verification code error: $error")
                // Error is already handled by data binding in XML
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnVerify.isEnabled = !isLoading
            // Consider adding a progress indicator here
        }
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        try {
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
            sharedPreferences.edit {
                putBoolean("is_logged_in", isLoggedIn)
            }
            Timber.d("Login state saved: $isLoggedIn")
        } catch (e: Exception) {
            Timber.e(e, "Error saving login state")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            viewModel.stopTimer()
        } catch (e: Exception) {
            Timber.e(e, "Error stopping timer")
        }
        _binding = null
    }
}