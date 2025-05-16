package com.example.healthcareproject.present.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentRegisterBinding
import com.example.healthcareproject.present.navigation.AuthNavigator
import com.example.healthcareproject.present.viewmodel.auth.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var navigator: AuthNavigator
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>

    // Flag to track if setup has been done
    private var isSetupComplete = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        setupGoogleSignIn()
        setupGoogleSignInLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSetupComplete) {
            Timber.d("Setting up observers and listeners for RegisterFragment")
            setupObservers()
            setupListeners()
            isSetupComplete = true
        } else {
            Timber.d("Setup already complete, skipping")
        }
    }

    private fun setupGoogleSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        }
    }

    private fun setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.linkGoogleAccount(idToken)
                } else {
                    Snackbar.make(binding.root, "ID Token is null", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                Snackbar.make(binding.root, "Google Sign-In failed: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.etDob.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                viewModel.setDateOfBirth(selectedDate.format(formatter))
            }, today.year, today.monthValue - 1, today.dayOfMonth).show()
        }

        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setGender(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.setGender("")
            }
        }

        binding.spinnerBloodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setBloodType(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.setBloodType("")
            }
        }

        // Set back button listener only once
        binding.btnBackRegisterToLoginMethod.setOnClickListener {
            Timber.d("Back button clicked in RegisterFragment")

            // Prevent multiple clicks
            binding.btnBackRegisterToLoginMethod.isEnabled = false

            // Use the ViewModel to handle navigation
            viewModel.onBackClicked()
        }

        Timber.d("Back button listener set for RegisterFragment")
    }

    private fun setupObservers() {
        viewModel.registrationSuccess.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                val email = viewModel.email.value
                if (!email.isNullOrBlank()) {
                    val bundle = Bundle().apply {
                        putString("email", email)
                        putString("authFlow", "REGISTRATION")
                    }
                    view?.postDelayed({
                        findNavController().navigate(R.id.action_registerFragment_to_verifyCodeFragment, bundle)
                    }, 100)
                    viewModel.resetNavigationStates()
                } else {
                    Snackbar.make(binding.root, "Email is required", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in RegisterFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { viewModel.onRegisterClicked() }
                    .show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnCreateAccount.isEnabled = !isLoading
            binding.btnBackRegisterToLoginMethod.isEnabled = !isLoading
        }

        viewModel.emailLinkSent.observe(viewLifecycleOwner) { sent ->
            if (sent) {
                Snackbar.make(binding.root, "Check your email for the sign-in link!", Snackbar.LENGTH_LONG).show()
            }
        }

        // Handle navigation through ViewModel
        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                Timber.d("Navigating back from RegisterFragment")
                findNavController().popBackStack(R.id.loginMethodFragment, false)
                viewModel.resetNavigateBack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}