package com.example.healthcareproject.present.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.healthcareproject.databinding.FragmentVerifyCodeBinding
import com.example.healthcareproject.present.auth.viewmodel.VerifyCodeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    private var _binding: FragmentVerifyCodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerifyCodeViewModel by viewModels()
    private val args: VerifyCodeFragmentArgs by navArgs()
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

        // Initialize ViewModel with email and authFlow from navigation arguments
        viewModel.setEmailAndAuthFlow(
            email = args.email ?: "",
            authFlow = when (args.authFlow) {
                "REGISTRATION" -> VerifyCodeViewModel.AuthFlow.REGISTRATION
                "FORGOT_PASSWORD" -> VerifyCodeViewModel.AuthFlow.FORGOT_PASSWORD
                else -> VerifyCodeViewModel.AuthFlow.REGISTRATION
            }
        )

        // Back button
        binding.btnBackVerifyCode.setOnClickListener {
            navigator.fromVerifyCodeToLogin()
        }

        // Resend code
        binding.tvResend.setOnClickListener {
            if (viewModel.timerCount.value == 0) {
                viewModel.sendVerificationCode()
            }
        }

        // Observe navigation to CreateNewPassword
        viewModel.navigateToCreateNewPassword.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navigator.fromVerifyCodeToCreateNewPassword()
                viewModel.resetNavigationStates()
            }
        }

        // Observe navigation to Login
        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navigator.fromVerifyCodeToLogin()
                viewModel.resetNavigationStates()
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { viewModel.sendVerificationCode() }
                    .show()
                viewModel.setError(null)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnVerify.isEnabled = !isLoading
            binding.tvResend.isEnabled = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopTimer()
        _binding = null
    }
}