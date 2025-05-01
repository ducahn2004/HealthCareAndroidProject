package com.example.healthcareproject.present.medicine

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentAddAppointmentBinding
import com.example.healthcareproject.domain.model.MedicalVisit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@AndroidEntryPoint
class AddAppointmentFragment : Fragment() {
    private var _binding: FragmentAddAppointmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddAppointmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAppointmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Date picker
        binding.etDate.setOnClickListener {
            showDatePicker { year, month, dayOfMonth ->
                viewModel.updateVisitDate(LocalDate.of(year, month + 1, dayOfMonth))
            }
        }

        // Time picker
        binding.etTime.setOnClickListener {
            showTimePicker { hour, minute ->
                viewModel.updateTime(LocalTime.of(hour, minute))
            }
        }

        // Save button
        binding.btnSave.setOnClickListener {
            viewModel.saveAppointment()
        }

        // Observe UI state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.isSuccess) {
                    // Create MedicalVisit for setFragmentResult
                    val medicalVisit = MedicalVisit(
                        visitId = "", // Assume repository sets
                        userId = "",  // Assume repository sets
                        visitDate = state.visitDate!!,
                        clinicName = state.clinicName,
                        doctorName = state.doctorName,
                        diagnosis = state.diagnosis,
                        treatment = state.treatment,
                        createdAt = LocalDateTime.of(state.visitDate, state.time!!)
                    )
                    setFragmentResult("requestKey", Bundle().apply {
                        putParcelable("newVisit", medicalVisit)
                    })
                    findNavController().navigateUp()
                }
                if (state.error != null) {
                    binding.tvError.text = state.error
                    binding.tvError.visibility = View.VISIBLE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }
        }

        // Clear error
        binding.tvError.setOnClickListener {
            viewModel.clearError()
        }
    }

    private fun showDatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                onDateSelected(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(onTimeSelected: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                onTimeSelected(hourOfDay, minute)
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