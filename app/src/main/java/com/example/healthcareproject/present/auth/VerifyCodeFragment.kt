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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startTimer()
        // Resend code
        binding.tvResend.setOnClickListener {
            val email = viewModel.email.value
            if (!email.isNullOrBlank()) {
                viewModel.sendPasswordResetEmail(email)
                viewModel.startTimer() // Restart timer after resending
                Snackbar.make(binding.root, "Code resent to $email", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Email not found", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Observe code verification success
        viewModel.isCodeVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified) {
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
                        Snackbar.make(binding.root, "Invalid authentication flow", Snackbar.LENGTH_LONG).show()
                    }
                }
                viewModel.resetNavigationStates()
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnVerify.isEnabled = !isLoading
        }
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
        sharedPreferences.edit() {
            putBoolean("is_logged_in", isLoggedIn)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopTimer()
        _binding = null
    }
}