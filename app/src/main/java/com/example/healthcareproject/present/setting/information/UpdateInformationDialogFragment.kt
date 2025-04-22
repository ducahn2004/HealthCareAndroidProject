package com.example.healthcareproject.present.setting.information

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthcareproject.databinding.DialogUpdateInformationBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.healthcareproject.R
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

        // Thiết lập Spinner cho Gender
        setupGenderSpinner()

        // Thiết lập Spinner cho Blood Type
        setupBloodTypeSpinner()

        // Xử lý DatePicker cho Date of Birth
        binding.tvDob.setOnClickListener {
            showDatePicker()
        }

        // Observe ViewModel states
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isSaved.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                // Gửi thông tin đã cập nhật về callback
                val updatedInfo = viewModel.getUpdatedUserInfo()
                onSave(updatedInfo)
                dismiss()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Optionally show a progress bar
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Load thông tin hiện tại
        viewModel.loadUserInfo("user123") // Replace with actual userId

        // Xử lý nút Cancel
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // Xử lý nút Save
        binding.btnSave.setOnClickListener {
            viewModel.saveUserInfo(
                userId = "user123", // Replace with actual userId
                password = "password123" // Replace with actual password
            )
        }
    }

    override fun onStart() {
        super.onStart()
        // Thiết lập chiều rộng dialog
        dialog?.window?.let { window ->
            val params = window.attributes
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt() // 90% chiều rộng màn hình
            params.width = width
            window.attributes = params

        }
    }

    private fun setupGenderSpinner() {
        val genders = arrayOf("Male", "Female")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter

        // Set initial selection based on ViewModel's gender
        viewModel.gender.observe(viewLifecycleOwner) { gender ->
            val index = genders.indexOf(gender)
            if (index >= 0) {
                binding.spinnerGender.setSelection(index)
            }
        }

        // Update ViewModel when selection changes
        binding.spinnerGender.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setGender(genders[position])
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
        viewModel.bloodType.observe(viewLifecycleOwner) { bloodType ->
            val index = bloodTypes.indexOf(bloodType)
            if (index >= 0) {
                binding.spinnerBloodType.setSelection(index)
            }
        }

        // Update ViewModel when selection changes
        binding.spinnerBloodType.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setBloodType(bloodTypes[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // No action needed
            }
        })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDob = viewModel.getDateOfBirth() ?: ""
        if (currentDob.isNotEmpty() && currentDob.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            val parts = currentDob.split("/")
            calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
        }

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                viewModel.setDateOfBirth(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}