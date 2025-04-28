package com.example.healthcareproject.present.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentForgotPasswordBinding
import com.example.healthcareproject.present.auth.viewmodel.ForgotPasswordViewModel
import com.example.healthcareproject.present.auth.viewmodel.VerifyCodeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
    private val verifyCodeViewModel: VerifyCodeViewModel by viewModels()
    private lateinit var navigator: AuthNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = forgotPasswordViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("ForgotPasswordFragment: onViewCreated")

        // Back button
        binding.btnBackForgotPasswordToLogin.setOnClickListener {
            navigator.fromForgotPasswordToLoginMethod()
        }

        // Observe navigation to VerifyCode
        forgotPasswordViewModel.navigateToVerifyCode.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                Timber.d("Navigating to VerifyCodeFragment with email=${forgotPasswordViewModel.email.value}")
                verifyCodeViewModel.setEmail(forgotPasswordViewModel.email.value ?: "")
                verifyCodeViewModel.setAuthFlow(VerifyCodeViewModel.AuthFlow.FORGOT_PASSWORD)
                navigator.fromForgotPasswordToVerifyCode()
                forgotPasswordViewModel.resetNavigationStates()
            }
        }

        // Observe errors
        forgotPasswordViewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in ForgotPasswordFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observe loading state
        forgotPasswordViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnResetPassword.isEnabled = !isLoading
        }

        // Observe email errors
        forgotPasswordViewModel.emailError.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                binding.etEmail.error = error
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}