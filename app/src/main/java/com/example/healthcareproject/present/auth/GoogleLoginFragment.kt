package com.example.healthcareproject.present.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentGoogleLoginBinding
import com.example.healthcareproject.present.MainActivity
import com.example.healthcareproject.present.auth.viewmodel.GoogleLoginViewModel
import com.example.healthcareproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GoogleLoginFragment : Fragment() {

    private var _binding: FragmentGoogleLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoogleLoginViewModel by viewModels()
    private lateinit var navigator: AuthNavigator
    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                Timber.d("Google Sign-In: Received ID token")
                viewModel.handleGoogleSignIn(idToken)
            } ?: run {
                Timber.e("Google Sign-In failed: No ID token")
                viewModel.setError("Google Sign-In failed: No ID token")
            }
        } catch (e: ApiException) {
            Timber.e(e, "Google Sign-In failed")
            viewModel.setError("Google Sign-In failed: ${e.message}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("GoogleLoginFragment: onViewCreated")

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Back button
        binding.btnBackGgLoginToLoginMethod.setOnClickListener {
            navigator.fromGoogleLoginToLoginMethod()
        }

        // Observe authentication state
        viewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuthenticated ->
            if (isAuthenticated) {
                Timber.d("Google Sign-In successful, navigating to MainActivity")
                saveLoginState(true)
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
                viewModel.resetNavigationStates()
            }
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in GoogleLoginFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                viewModel.setError(null)
            }
        }

        // Google login button click is handled via Data Binding
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        try {
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
            sharedPreferences.edit {
                putBoolean("is_logged_in", isLoggedIn)
            }
            Timber.d("Login state saved: $isLoggedIn")
        } catch (e: Exception) {
            Timber.e(e, "Error saving login state")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}