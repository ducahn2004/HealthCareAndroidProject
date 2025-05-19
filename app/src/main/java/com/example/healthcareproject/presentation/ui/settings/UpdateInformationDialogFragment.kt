package com.example.healthcareproject.presentation.ui.settings

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.DialogUpdateInformationBinding
import com.example.healthcareproject.presentation.viewmodel.setting.information.UpdateInformationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UpdateInformationDialogFragment(
    private val onSave: (Map<String, String>) -> Unit
) : DialogFragment() {

    private var _binding: DialogUpdateInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateInformationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUpdateInformationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
        loadUserInfo()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun loadUserInfo() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModel.loadUserInfoByUid(uid)
        } else {
            showToast(getString(R.string.user_not_logged_in))
            dismiss()
        }
    }

    private fun setupObservers() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }

        viewModel.isSaved.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                onSave(viewModel.getUpdatedUserInfo())
                dismiss()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show progress bar if needed
        }
    }

    private fun setupListeners() {
        // Date of Birth Picker
        binding.etDob.setOnClickListener { showDatePicker() }

        // Gender Spinner
        setupSpinner(
            binding.spinnerGender,
            arrayOf("Male", "Female"),
            viewModel.genderLiveData
        ) { viewModel.setGender(it) }

        // Blood Type Spinner
        setupSpinner(
            binding.spinnerBloodType,
            arrayOf("A", "B", "AB", "O", "None"),
            viewModel.bloodTypeLiveData
        ) { viewModel.setBloodType(it) }

        // Buttons
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
                viewModel.saveUserInfoByUid(uid)
            } ?: run {
                showToast(getString(R.string.user_not_logged_in))
                dismiss()
            }
        }
    }

    private fun setupSpinner(
        spinner: Spinner,
        items: Array<String>,
        liveData: LiveData<String>,
        onItemSelected: (String) -> Unit
    ) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        liveData.observe(viewLifecycleOwner) { value ->
            val index = items.indexOf(value)
            if (index >= 0) spinner.setSelection(index)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                onItemSelected(items[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        viewModel.dateOfBirth.value?.takeIf { it.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) }?.let { dob ->
            try {
                val parts = dob.split("/")
                calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
            } catch (e: Exception) {
                // Use current date as fallback
            }
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = String.format(Locale.US, "%02d/%02d/%04d", day, month + 1, year)
                viewModel.setDateOfBirth(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}