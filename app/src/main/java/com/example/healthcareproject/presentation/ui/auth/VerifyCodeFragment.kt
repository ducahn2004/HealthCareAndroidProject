package com.example.healthcareproject.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentVerifyCodeBinding
import com.example.healthcareproject.presentation.navigation.AuthNavigator
import com.example.healthcareproject.presentation.viewmodel.auth.VerifyCodeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    private var _binding: FragmentVerifyCodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerifyCodeViewModel by viewModels()
    private lateinit var navigator: AuthNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel with email and authFlow
        val email = arguments?.getString("email") ?: ""
        val authFlowString = arguments?.getString("authFlow") ?: "REGISTRATION"
        viewModel.setEmailAndAuthFlow(
            email = email,
            authFlow = when (authFlowString) {
                "REGISTRATION" -> VerifyCodeViewModel.AuthFlow.REGISTRATION
                "FORGOT_PASSWORD" -> VerifyCodeViewModel.AuthFlow.FORGOT_PASSWORD
                else -> VerifyCodeViewModel.AuthFlow.REGISTRATION
            }
        )

        // Check for email link (fallback)
        activity?.intent?.data?.toString()?.let { emailLink ->
            viewModel.verifyEmailLink(emailLink)
        }

        // Back button
        binding.btnBackVerifyCode.setOnClickListener {
            navigator.fromVerifyCodeToLogin()
        }

        // Show Snackbar for email link prompt
        Snackbar.make(binding.root, "Check your email for the verification link", Snackbar.LENGTH_LONG).show()

        // Observe navigation (fallback)
        viewModel.navigateToCreateNewPassword.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navigator.fromVerifyCodeToCreateNewPassword()
                viewModel.resetNavigationStates()
            }
        }

        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navigator.fromVerifyCodeToLogin()
                viewModel.resetNavigationStates()
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                viewModel.setError(null)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopTimer()
        _binding = null
    }
}