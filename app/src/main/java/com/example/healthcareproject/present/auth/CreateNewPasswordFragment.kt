package com.example.healthcareproject.present.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController


import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentCreateNewPasswordBinding
import com.example.healthcareproject.present.auth.viewmodel.CreateNewPasswordViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Fragment for resetting the user's password in the Forgot Password flow.
 */
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

        // Set email from navigation arguments
        val email = arguments?.getString("email")
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
        Timber.d("Email set to $email from navigation arguments")

        // Back button
        binding.btnBackCreateNewPassword.setOnClickListener {
            findNavController().navigateUp()
        }

        // Observe navigation to Login
        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
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
            binding.btnResetPassword.isEnabled = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}