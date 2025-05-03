package com.example.healthcareproject.present.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentRegisterBinding
import com.example.healthcareproject.present.auth.viewmodel.RegisterViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button
        binding.btnBackRegisterToLoginMethod.setOnClickListener {
            Timber.d("Back button clicked, current destination: ${findNavController().currentDestination?.id}")
            try {
                navigator.fromRegisterToLoginMethod()
            } catch (e: Exception) {
                Timber.e("Navigation failed: ${e.message}")
                Snackbar.make(binding.root, "Navigation failed: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }

        // Date picker for date of birth
        binding.etDob.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    viewModel.setDateOfBirth(selectedDate.format(formatter))
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            ).show()
        }

        // Gender spinner
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedGender = parent.getItemAtPosition(position).toString()
                viewModel.setGender(selectedGender)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.setGender("")
            }
        }

        // Blood type spinner
        binding.spinnerBloodType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedBloodType = parent.getItemAtPosition(position).toString()
                    viewModel.setBloodType(selectedBloodType)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    viewModel.setBloodType("")
                }
            }

        // Observe registration result
        viewModel.registerResult.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                val email = viewModel.email.value
                if (!email.isNullOrBlank()) {
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

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
                    .setAction("Retry") { viewModel.onRegisterClicked() }
                    .show()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnCreateAccount.isEnabled = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}