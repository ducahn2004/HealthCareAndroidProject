package com.example.healthcareproject.present.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentForgotPasswordBinding
import com.example.healthcareproject.present.navigation.AuthNavigator
import com.example.healthcareproject.present.viewmodel.auth.ForgotPasswordViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ForgotPasswordFragment : androidx.fragment.app.Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var navigator: AuthNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("ForgotPasswordFragment: onViewCreated")

        // Observe password reset success notification
        viewModel.resetRequestSuccess.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observe navigation to VerifyCode
        // Observe navigation to VerifyCode
        viewModel.navigateToVerifyCode.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val email = viewModel.email.value
                if (!email.isNullOrBlank()) {
                    val bundle = Bundle().apply {
                        putString("email", email)
                        putString("authFlow", "FORGOT_PASSWORD")
                    }
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_verifyCodeFragment, bundle)
                    viewModel.resetNavigationStates()
                }
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in ForgotPasswordFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { viewModel.onResetPasswordClicked() }
                    .show()
                viewModel.clearError()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnResetPassword.isEnabled = !isLoading
        }

        // Observe email errors
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.etEmail.error = error
        }

        // Back button
        binding.btnBackForgotPasswordToLogin.setOnClickListener {
            navigator.fromForgotPasswordToLoginMethod()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}