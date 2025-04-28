package com.example.healthcareproject.present.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentCreateNewPasswordBinding
import com.example.healthcareproject.present.auth.viewmodel.CreateNewPasswordViewModel
import com.example.healthcareproject.present.auth.viewmodel.VerifyCodeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreateNewPasswordFragment : Fragment() {

    private var _binding: FragmentCreateNewPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateNewPasswordViewModel by viewModels()
    private val verifyCodeViewModel: VerifyCodeViewModel by viewModels()
    private lateinit var navigator: AuthNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNewPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("CreateNewPasswordFragment: onViewCreated")

        // Set email from VerifyCodeViewModel
        val email = verifyCodeViewModel.email.value
        if (email.isNullOrEmpty()) {
            Timber.e("Email is null or empty in CreateNewPasswordFragment")
            Snackbar.make(binding.root, "Email is required to reset password", Snackbar.LENGTH_LONG).show()
            navigator.fromCreateNewPasswordToLogin()
            return
        }
        viewModel.setEmail(email)
        Timber.d("Email set to $email from VerifyCodeViewModel")

        // Back button
        binding.btnBackCreateNewPassword.setOnClickListener {
            navigator.navigateUp()
        }

        // Observe navigation to Login
        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                Timber.d("Navigating to LoginFragment after successful password reset")
                Snackbar.make(binding.root, "Password reset successfully. Please log in.", Snackbar.LENGTH_SHORT).show()
                navigator.fromCreateNewPasswordToLogin()
                viewModel.resetNavigationStates()
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in CreateNewPasswordFragment: $error")
                if (error == "Password reset successfully") {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                }
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