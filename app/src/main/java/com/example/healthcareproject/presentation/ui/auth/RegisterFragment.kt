package com.example.healthcareproject.presentation.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentRegisterBinding
import com.example.healthcareproject.presentation.navigation.AuthNavigator
import com.example.healthcareproject.presentation.viewmodel.auth.RegisterViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

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

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // from Firebase Console
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Date picker
        binding.etDob.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                viewModel.setDateOfBirth(selectedDate.format(formatter))
            }, today.year, today.monthValue - 1, today.dayOfMonth).show()
        }

        // Gender spinner
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setGender(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.setGender("")
            }
        }

        // Blood type spinner
        binding.spinnerBloodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setBloodType(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.setBloodType("")
            }
        }

        // Observe registration success notification
        viewModel.registrationSuccess.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }

        // Observe registration result
        viewModel.registerResult.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                val email = viewModel.email.value
                if (!email.isNullOrBlank()) {

                    // Step 2: Navigate to code verification screen
                    val bundle = Bundle().apply {
                        putString("email", email)
                        putString("authFlow", "REGISTRATION")
                    }
                    findNavController().navigate(R.id.action_registerFragment_to_verifyCodeFragment, bundle)
                    viewModel.resetNavigationStates()
                } else {
                    Snackbar.make(binding.root, "Email is required", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        // Error observer
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in RegisterFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { viewModel.onRegisterClicked() }
                    .show()
            }
        }

        // Loading observer
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnCreateAccount.isEnabled = !isLoading
        }

        // Back button
        binding.btnBackRegisterToLoginMethod.setOnClickListener {
            navigator.fromRegisterToLoginMethod()
        }

        viewModel.emailLinkSent.observe(viewLifecycleOwner) { sent ->
            if (sent) {
                Snackbar.make(binding.root, "Check your email for the sign-in link!", Snackbar.LENGTH_LONG).show()
                // Optionally, navigate to a waiting screen or stay on the current screen
            }
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                // Wait for email link verification; no immediate navigation
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
