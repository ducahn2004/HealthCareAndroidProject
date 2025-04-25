package com.example.healthcareproject.present.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentLoginBinding
import com.example.healthcareproject.present.MainActivity
import com.example.healthcareproject.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    // Initialize AuthViewModel using Hilt
    private val viewModel: AuthViewModel by viewModels()
    // View Binding property
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update ViewModel with user input
        binding.etEmail.addTextChangedListener { text ->
            viewModel.setEmail(text.toString().trim())
        }
        binding.etPassword.addTextChangedListener { text ->
            viewModel.setPassword(text.toString().trim())
        }

        // Observe authentication state
        viewModel.isAuthenticated.observe(viewLifecycleOwner, Observer { isAuthenticated ->
            if (isAuthenticated) {
                // Navigate to MainActivity and finish AuthActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
                viewModel.resetNavigationStates()
            }
        })

        // Observe Google login navigation
        viewModel.navigateToGoogleLogin.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_loginFragment_to_googleLoginFragment)
                viewModel.resetNavigationStates()
            }
        })

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
            } ?: run {
                binding.tvError.visibility = View.GONE
            }
        })

        // Observe email and password errors
        viewModel.emailError.observe(viewLifecycleOwner, Observer { error ->
            binding.etEmail.error = error
        })
        viewModel.passwordError.observe(viewLifecycleOwner, Observer { error ->
            binding.etPassword.error = error
        })

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.btnLogin.isEnabled = !isLoading
            binding.googleLoginContainer.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Set click listener for the login button
        binding.btnLogin.setOnClickListener {
            viewModel.onLoginClicked()
        }

        // Set click listener for the Google login container
        binding.googleLoginContainer.setOnClickListener {
            viewModel.onGoogleLoginClicked()
        }

        // Set click listener for the forgot password link
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        // Set click listener for the back button
        binding.btnBackLoginToLoginMethod.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_loginMethodFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear binding to prevent memory leaks
        _binding = null
    }
}