package com.example.healthcareproject.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentGoogleLoginBinding
import com.example.healthcareproject.presentation.navigation.AuthNavigator
import com.example.healthcareproject.presentation.ui.activity.MainActivity
import com.example.healthcareproject.presentation.viewmodel.auth.GoogleLoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.net.UnknownHostException
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context

@AndroidEntryPoint
class GoogleLoginFragment : Fragment() {

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
                Timber.Forest.d("Google Sign-In: Received ID token")
                viewModel.handleGoogleSignIn(idToken)
            } else {
                Timber.Forest.e("Google Sign-In failed: No ID token")
                viewModel.setError(getString(R.string.error_no_id_token))
            }
        } catch (e: ApiException) {
            Timber.Forest.e(e, "Google Sign-In failed with status code: ${e.statusCode}")
            val errorMessage = when (e.statusCode) {
                12501 -> getString(R.string.error_sign_in_cancelled)
                10 -> getString(R.string.error_developer_config)
                7 -> getString(R.string.error_network) // Handle NETWORK_ERROR
                else -> getString(R.string.error_sign_in_failed, e.message)
            }
            viewModel.setError(errorMessage)
        } catch (e: UnknownHostException) {
            Timber.Forest.e(e, "Network error during Google Sign-In")
            viewModel.setError(getString(R.string.error_network))
        } catch (e: Exception) {
            Timber.Forest.e(e, "Unexpected error during Google Sign-In")
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
            Timber.Forest.d("User already signed in, navigating to MainActivity")
            navigateToMainActivity()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check Google Play Services availability
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(requireActivity(), resultCode, 9000)?.show()
            } else {
                viewModel.setError(getString(R.string.error_google_play_services))
            }
            return
        }

        Timber.Forest.d("GoogleLoginFragment: onViewCreated")

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
            if (isNetworkAvailable()) {
                Timber.Forest.d("Launching Google Sign-In intent")
                googleSignInClient.signOut().addOnCompleteListener {
                    signInLauncher.launch(googleSignInClient.signInIntent)
                }
            } else {
                Timber.Forest.e("No network available for Google Sign-In")
                viewModel.setError(getString(R.string.error_network))
            }
        }

        // Observe authentication state
        viewModel.isAuthenticated.observe(viewLifecycleOwner) { isAuthenticated ->
            if (isAuthenticated) {
                Timber.Forest.d("Google Sign-In successful, navigating to MainActivity")
                saveLoginState(true)
                navigateToMainActivity()
            }
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.Forest.e("Error in GoogleLoginFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                viewModel.setError(null)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
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
            Timber.Forest.d("Login state saved: $isLoggedIn")
        } catch (e: Exception) {
            Timber.Forest.e(e, "Error saving login state")
            viewModel.setError(getString(R.string.error_saving_state))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}