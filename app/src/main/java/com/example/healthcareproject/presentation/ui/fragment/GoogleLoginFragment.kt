package com.example.healthcareproject.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentGoogleLoginBinding
import com.example.healthcareproject.presentation.ui.activity.MainActivity
import com.example.healthcareproject.presentation.viewmodel.auth.GoogleLoginViewModel
import com.example.healthcareproject.R
import com.example.healthcareproject.presentation.navigation.AuthNavigator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.net.UnknownHostException

@AndroidEntryPoint
class GoogleLoginFragment : androidx.fragment.app.Fragment() {

    private var _binding: FragmentGoogleLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoogleLoginViewModel by viewModels()
    private lateinit var navigator: AuthNavigator
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                Timber.d("Google Sign-In: Received ID token")
                viewModel.handleGoogleSignIn(idToken)
            } else {
                Timber.e("Google Sign-In failed: No ID token")
                viewModel.setError(getString(R.string.error_no_id_token))
            }
        } catch (e: ApiException) {
            Timber.e(e, "Google Sign-In failed with status code: ${e.statusCode}")
            val errorMessage = when (e.statusCode) {
                12501 -> getString(R.string.error_sign_in_cancelled)
                10 -> getString(R.string.error_developer_config)
                else -> getString(R.string.error_sign_in_failed, e.message)
            }
            viewModel.setError(errorMessage)
        } catch (e: UnknownHostException) {
            Timber.e(e, "Network error during Google Sign-In")
            viewModel.setError(getString(R.string.error_network))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during Google Sign-In")
            viewModel.setError(getString(R.string.error_unexpected))
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

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Timber.d("User already signed in, navigating to MainActivity")
            navigateToMainActivity()
        }
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

        // Observe ViewModel's Google Sign-In trigger
        viewModel.googleSignInTrigger.observe(viewLifecycleOwner) {
            Timber.d("Launching Google Sign-In intent")
            googleSignInClient.signOut().addOnCompleteListener {
                signInLauncher.launch(googleSignInClient.signInIntent)
            }
        }

        // Observe authentication state
        viewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuthenticated ->
            if (isAuthenticated) {
                Timber.d("Google Sign-In successful, navigating to MainActivity")
                saveLoginState(true)
                navigateToMainActivity()
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
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
        viewModel.resetNavigationStates()
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
            viewModel.setError(getString(R.string.error_saving_state))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}