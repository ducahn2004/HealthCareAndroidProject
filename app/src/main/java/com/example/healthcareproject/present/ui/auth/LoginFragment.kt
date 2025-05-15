package com.example.healthcareproject.present.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentLoginBinding
import com.example.healthcareproject.present.navigation.AuthNavigator
import com.example.healthcareproject.present.ui.MainActivity
import com.example.healthcareproject.present.viewmodel.auth.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : androidx.fragment.app.Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigator: AuthNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe login result (UID)
        viewModel.loginResult.observe(viewLifecycleOwner) { uid: String? ->
            if (uid != null) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("user_uid", uid)
                startActivity(intent)
                requireActivity().finish()
                viewModel.resetNavigationStates()
            }
        }

        // Observe navigation to Google login
        viewModel.navigateToGoogleLogin.observe(viewLifecycleOwner) { navigate: Boolean ->
            if (navigate) {
                navigator.fromLoginToGoogleLogin()
                viewModel.resetNavigationStates()
            }
        }

        // Observe navigation to Forgot Password
        viewModel.navigateToForgotPassword.observe(viewLifecycleOwner) { navigate: Boolean ->
            if (navigate) {
                navigator.fromLoginToForgotPassword()
                viewModel.resetNavigationStates()
            }
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { error: String? ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in LoginFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observe email and password errors
        viewModel.emailError.observe(viewLifecycleOwner) { error: String? ->
            binding.etEmail.error = error
        }
        viewModel.passwordError.observe(viewLifecycleOwner) { error: String? ->
            binding.etPassword.error = error // Fixed from "Avocado"
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading: Boolean ->
            binding.btnLogin.isEnabled = !isLoading
            binding.googleLoginContainer.isEnabled = !isLoading
        }

        // Handle back button
        binding.btnBackLoginToLoginMethod.setOnClickListener {
            navigator.fromLoginToLoginMethod()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}