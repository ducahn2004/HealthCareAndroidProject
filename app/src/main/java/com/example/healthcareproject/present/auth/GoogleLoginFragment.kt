package com.example.healthcareproject.present.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentGoogleLoginBinding
import com.example.healthcareproject.present.MainActivity
import com.example.healthcareproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoogleLoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private var _binding: FragmentGoogleLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient

    // Activity result launcher for Google Sign-In
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                viewModel.handleGoogleSignIn(idToken)
            } ?: run {
                viewModel.setError("Google Sign-In failed: No ID token")
            }
        } catch (e: ApiException) {
            viewModel.setError("Google Sign-In failed: ${e.message}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ensure resource exists
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

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

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.btnGoogleLogin.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Set click listener for the Google login button
        binding.btnGoogleLogin.setOnClickListener {
            viewModel.onGoogleLoginClicked()
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }

        // Set click listener for the back button
        binding.btnBackGgLoginToLoginMethod.setOnClickListener {
            findNavController().navigate(R.id.action_googleLoginFragment_to_loginMethodFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}