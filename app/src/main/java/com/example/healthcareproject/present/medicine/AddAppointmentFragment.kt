package com.example.healthcareproject.present.medicine

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentAddAppointmentBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class AddAppointmentFragment : Fragment() {

    private var _binding: FragmentAddAppointmentBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private var selectedDate: Calendar? = null
    private var selectedTime: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etDate.setOnClickListener {
            showDatePicker { calendar ->
                selectedDate = calendar
                binding.etDate.setText(dateFormat.format(calendar.time))
            }
        }

        binding.etTime.setOnClickListener {
            showTimePicker { calendar ->
                selectedTime = calendar
                binding.etTime.setText(timeFormat.format(calendar.time))
            }
        }

        // Sự kiện click nút Save
        binding.btnSave.setOnClickListener {
            val diagnosis = binding.etCondition.text.toString().trim()
            val doctorName = binding.etDoctor.text.toString().trim()
            val clinicName = binding.etFacility.text.toString().trim()

            // Validate fields
            if (diagnosis.isEmpty()) {
                binding.etCondition.error = "Required"
                return@setOnClickListener
            }
            if (doctorName.isEmpty()) {
                binding.etDoctor.error = "Required"
                return@setOnClickListener
            }
            if (clinicName.isEmpty()) {
                binding.etFacility.error = "Required"
                return@setOnClickListener
            }
            if (selectedDate == null) {
                binding.etDate.error = "Required"
                return@setOnClickListener
            }
            if (selectedTime == null) {
                binding.etTime.error = "Required"
                return@setOnClickListener
            }

            // Kết hợp ngày và giờ
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate!!.time
            calendar.set(Calendar.HOUR_OF_DAY, selectedTime!!.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, selectedTime!!.get(Calendar.MINUTE))
            val visitDateTime = calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val visitDate = visitDateTime.toLocalDate()

            // Create MedicalVisit object
            val newVisit = MedicalVisit(
                visitId = UUID.randomUUID().toString(),
                userId = "user123", // Replace with actual user ID
                visitDate = visitDate,
                clinicName = clinicName,
                doctorName = doctorName,
                diagnosis = diagnosis,
                treatment = "", // Default to empty, can be updated later
                createdAt = LocalDateTime.now()
            )

            // Truyền dữ liệu về thông qua setFragmentResult
            setFragmentResult("requestKey", Bundle().apply {
                putParcelable("newVisit", newVisit)
            })

            // Quay lại MedicineFragment
            findNavController().navigateUp()
        }
    }

    private fun showDatePicker(onDateSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(onTimeSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected(calendar)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}