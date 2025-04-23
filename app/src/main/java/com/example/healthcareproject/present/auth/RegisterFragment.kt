package com.example.healthcareproject.present.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentRegisterBinding
import java.util.Calendar
import java.util.Locale

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button
        binding.btnBackRegisterToLoginMethod.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginMethodFragment)
        }

        // Set up DatePicker for Date of Birth
        binding.tvDob.setOnClickListener {
            showDatePicker()
        }

        // Set up Gender Spinner
        setupGenderSpinner()

        // Set up Blood Type Spinner
        setupBloodTypeSpinner()

        // Observe registration state
        viewModel.isRegistered.observe(viewLifecycleOwner) { isRegistered ->
            if (isRegistered) {
                findNavController().navigate(R.id.action_registerFragment_to_verifyCodeFragment)
                viewModel.isRegistered.value = false // Reset state
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe loading state (optional)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Optionally show a loading indicator
            // For example, binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDob = viewModel.dateOfBirthLiveData.value ?: ""
        if (currentDob.isNotEmpty() && currentDob.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            val parts = currentDob.split("/")
            calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
        }

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                viewModel.dateOfBirth = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun setupGenderSpinner() {
        val genders = arrayOf("Male", "Female", "None")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter

        // Set initial selection based on ViewModel's gender
        viewModel.genderLiveData.observe(viewLifecycleOwner) { gender ->
            val index = genders.indexOf(gender)
            if (index >= 0) {
                binding.spinnerGender.setSelection(index)
            }
        }

        // Update ViewModel when selection changes
        binding.spinnerGender.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.gender = genders[position]
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // No action needed
            }
        })
    }

    private fun setupBloodTypeSpinner() {
        val bloodTypes = arrayOf("A", "B", "AB", "O", "None")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bloodTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBloodType.adapter = adapter

        // Set initial selection based on ViewModel's bloodType
        viewModel.bloodTypeLiveData.observe(viewLifecycleOwner) { bloodType ->
            val index = bloodTypes.indexOf(bloodType)
            if (index >= 0) {
                binding.spinnerBloodType.setSelection(index)
            }
        }

        // Update ViewModel when selection changes
        binding.spinnerBloodType.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.bloodType = bloodTypes[position]
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // No action needed
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}