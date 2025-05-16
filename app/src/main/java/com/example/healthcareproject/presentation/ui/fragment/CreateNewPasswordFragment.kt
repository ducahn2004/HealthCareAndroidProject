package com.example.healthcareproject.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentCreateNewPasswordBinding
import com.example.healthcareproject.presentation.viewmodel.auth.CreateNewPasswordViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreateNewPasswordFragment : Fragment() {

    private var _binding: FragmentCreateNewPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateNewPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNewPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("CreateNewPasswordFragment: onViewCreated")

        // Get email from navigation arguments or SharedPreferences
        var email = arguments?.getString("email")
        if (email.isNullOrEmpty()) {
            val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            email = sharedPreferences.getString("pending_email", null)
            Timber.d("Email from SharedPreferences: $email")
        }

        if (email.isNullOrEmpty()) {
            Timber.e("Email is null or empty in CreateNewPasswordFragment")
            Snackbar.make(
                binding.root,
                "Email is required to reset password. Please try again.",
                Snackbar.LENGTH_LONG
            )
                .setAction("Back") { findNavController().navigateUp() }
                .show()
            return
        }
        viewModel.setEmail(email)
        Timber.d("Email set to $email")

        // Back button
        binding.btnBackCreateNewPassword.setOnClickListener {
            findNavController().navigateUp()
        }

        // Reset Password button (explicit click listener)
        binding.btnResetPassword.setOnClickListener {
            Timber.d("Reset Password button clicked")
            viewModel.onResetPasswordClicked()
        }

        // Observe navigation to Login
        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate == true) {
                Timber.d("Navigating to LoginFragment after successful password reset")
                Snackbar.make(
                    binding.root,
                    "Password reset successfully. Please log in.",
                    Snackbar.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_createNewPasswordFragment_to_loginFragment)
                viewModel.resetNavigationStates()
            }
        }

        // Observe success message
        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (!success.isNullOrEmpty()) {
                Timber.d("Success: $success")
                Snackbar.make(binding.root, success, Snackbar.LENGTH_SHORT).show()
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in CreateNewPasswordFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Timber.d("Loading state: $isLoading")
            binding.btnResetPassword.isEnabled = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}