package com.example.healthcareproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.healthcareproject.databinding.DialogUpdateInformationBinding
import java.text.SimpleDateFormat
import java.util.*

class UpdateInformationDialogFragment(
    private val onSave: (Map<String, String>) -> Unit
) : DialogFragment() {

    private var _binding: DialogUpdateInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUpdateInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load thông tin hiện tại
        loadCurrentInformation()

        // Thiết lập Spinner cho Gender
        setupGenderSpinner()

        // Xử lý DatePicker cho Date of Birth
        binding.tvDob.setOnClickListener {
            showDatePicker()
        }

        // Xử lý nút Cancel
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // Xử lý nút Save
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveInformation()
            }
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
            window.setBackgroundDrawableResource(android.R.color.transparent) // Nền trong suốt
        }
    }

    private fun loadCurrentInformation() {
        val sharedPrefs = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        binding.etName.setText(sharedPrefs.getString("name", "John Doe"))
        binding.etAddress.setText(sharedPrefs.getString("address", "123 Main St, City"))
        binding.tvDob.text = sharedPrefs.getString("dob", "01/01/1990")
        binding.etBloodType.setText(sharedPrefs.getString("blood_type", "O+"))
        binding.etPhone.setText(sharedPrefs.getString("phone", "+1234567890"))

        // Load gender vào Spinner
        val currentGender = sharedPrefs.getString("gender", "Male") ?: "Male"
        val genderIndex = if (currentGender == "Male") 0 else 1
        binding.spinnerGender.setSelection(genderIndex)
    }

    private fun setupGenderSpinner() {
        val genders = arrayOf("Male", "Female")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDob = binding.tvDob.text.toString()
        if (currentDob.isNotEmpty() && isValidDateFormat(currentDob)) {
            val parts = currentDob.split("/")
            calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
        }

        val datePicker = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                binding.tvDob.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis() // Không cho chọn ngày tương lai
        datePicker.show()
    }

    private fun validateInput(): Boolean {
        // Kiểm tra Name
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.etName.error = "Name is required"
            return false
        }

        // Kiểm tra Date of Birth
        val dob = binding.tvDob.text.toString()
        if (!isValidDateFormat(dob) || !isValidDate(dob)) {
            Toast.makeText(requireContext(), "Invalid date of birth. Use dd/mm/yyyy and ensure it's a valid date.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isValidDateFormat(date: String): Boolean {
        return date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false // Không chấp nhận ngày không hợp lệ (ví dụ: 31/04)
            val parsedDate = sdf.parse(date)
            parsedDate != null && parsedDate.before(Date()) // Đảm bảo ngày không trong tương lai
        } catch (e: Exception) {
            false
        }
    }

    private fun saveInformation() {
        val sharedPrefs = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("name", binding.etName.text.toString())
            putString("address", binding.etAddress.text.toString())
            putString("dob", binding.tvDob.text.toString())
            putString("gender", binding.spinnerGender.selectedItem.toString())
            putString("blood_type", binding.etBloodType.text.toString())
            putString("phone", binding.etPhone.text.toString())
            apply()
        }

        // Gửi thông tin đã cập nhật về callback
        val updatedInfo = mapOf(
            "name" to binding.etName.text.toString(),
            "address" to binding.etAddress.text.toString(),
            "dob" to binding.tvDob.text.toString(),
            "gender" to binding.spinnerGender.selectedItem.toString(),
            "blood_type" to binding.etBloodType.text.toString(),
            "phone" to binding.etPhone.text.toString()
        )
        onSave(updatedInfo)

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}