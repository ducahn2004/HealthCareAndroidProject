package com.example.healthcareproject.present.medicine

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.healthcareproject.databinding.FragmentAddAppointmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import com.example.healthcareproject.present.navigation.MainNavigator

@AndroidEntryPoint
class AddAppointmentFragment : Fragment() {
    private var _binding: FragmentAddAppointmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddAppointmentViewModel by viewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

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
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observe navigation
        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                mainNavigator.navigateBackToMedicineFromAddAppointment()
            }
        }

        // Observe date picker trigger
        viewModel.showDatePicker.observe(viewLifecycleOwner) { show ->
            if (show) {
                showDatePicker { year, month, dayOfMonth ->
                    viewModel.updateVisitDate(LocalDate.of(year, month + 1, dayOfMonth))
                }
                viewModel.resetDatePicker()
            }
        }

        // Observe time picker trigger
        viewModel.showTimePicker.observe(viewLifecycleOwner) { show ->
            if (show) {
                showTimePicker { hour, minute ->
                    viewModel.updateTime(LocalTime.of(hour, minute))
                }
                viewModel.resetTimePicker()
            }
        }

        // Observe success state with MedicalVisit
        viewModel.successWithVisit.observe(viewLifecycleOwner) { medicalVisit ->
            medicalVisit?.let {
                setFragmentResult("requestKey", Bundle().apply {
                    putParcelable("newVisit", it)
                })
                mainNavigator.navigateBackToMedicineFromAddAppointment()
            }
        }

        // Observe error message
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
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