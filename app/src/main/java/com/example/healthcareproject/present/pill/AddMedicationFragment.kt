package com.example.healthcareproject.present.pill

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.DialogAddMedicationBinding
import com.example.healthcareproject.databinding.FragmentAddMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddMedicationFragment : Fragment() {
    private var _binding: FragmentAddMedicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicationViewModel by viewModels()
    @Inject lateinit var mainNavigator: MainNavigator

    private lateinit var medicationAdapter: MedicationAdapter
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        medicationAdapter = MedicationAdapter { med ->
            med.visitId?.let { visitId ->
                mainNavigator.navigateToMedicalHistoryDetail(visitId) // Pass visitId as String
            }
        }
        binding.rvMedications.layoutManager = LinearLayoutManager(context)
        binding.rvMedications.adapter = medicationAdapter
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvDate.setOnClickListener {
            showDatePicker { calendar ->
                viewModel.setVisitDate(calendar)
                binding.tvDate.text = calendar.time.toLocalDate().format(dateFormatter)
            }
        }

        binding.tvTime.setOnClickListener {
            showTimePicker { calendar ->
                viewModel.setVisitTime(calendar)
                binding.tvTime.text = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
            }
        }

        // Save MedicalVisit
        binding.btnSave.setOnClickListener {
            val condition = binding.etCondition.text.toString().trim()
            val doctor = binding.etDoctor.text.toString().trim()
            val facility = binding.etFacility.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()

            if (condition.isEmpty()) {
                binding.etCondition.error = "Required"
                return@setOnClickListener
            }
            if (doctor.isEmpty()) {
                binding.etDoctor.error = "Required"
                return@setOnClickListener
            }
            if (facility.isEmpty()) {
                binding.etFacility.error = "Required"
                return@setOnClickListener
            }
            if (viewModel.uiState.value?.visitDate == null) {
                binding.tvDate.error = "Required"
                return@setOnClickListener
            }
            if (viewModel.uiState.value?.visitTime == null) {
                binding.tvTime.error = "Required"
                return@setOnClickListener
            }

            viewModel.saveMedicalVisit(
                diagnosis = condition,
                doctorName = doctor,
                clinicName = facility,
                location = location.takeIf { it.isNotEmpty() }
            )
        }

        // Add Medication and Finalize
        binding.btnAddMedication.setOnClickListener {
            if (viewModel.uiState.value?.isVisitSaved == true) {
                showAddMedicationDialog()
            } else {
                Toast.makeText(context, "Please save the visit first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // Enable/disable buttons
            binding.btnAddMedication.isEnabled = state.isVisitSaved
            binding.btnSave.isEnabled = !state.isVisitSaved

            // Update RecyclerView
            state.medication?.let { medication ->
                medicationAdapter.submitList(listOf(medication))
            }

            // Handle completion
            if (state.isFinished && state.medication != null) {
                setFragmentResult("medicationKey", Bundle().apply {
                    putString("visitId", state.visitId)
                    putParcelable("medication", state.medication)
                })
                findNavController().navigateUp()
            }

            // Show errors
            state.error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddMedicationDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogAddMedicationBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val window = dialog.window
        if (window != null) {
            val width = (requireActivity().resources.displayMetrics.widthPixels * 0.9).toInt()
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        // Set default start date to today
        val today = LocalDate.now()
        dialogBinding.tvStartDate.text = today.format(dateFormatter)

        // Set default end date to 1 month later
        val defaultEndDate = today.plusMonths(1)
        dialogBinding.tvEndDate.text = defaultEndDate.format(dateFormatter)

        dialogBinding.tvStartDate.setOnClickListener {
            showDatePicker { calendar ->
                dialogBinding.tvStartDate.text = calendar.time.toLocalDate().format(dateFormatter)
            }
        }

        dialogBinding.tvEndDate.setOnClickListener {
            showDatePicker { calendar ->
                dialogBinding.tvEndDate.text = calendar.time.toLocalDate().format(dateFormatter)
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnAdd.setOnClickListener {
            val medicationName = dialogBinding.etMedicationName.text.toString().trim()
            val dosage = dialogBinding.etDosage.text.toString().trim()
            val frequency = dialogBinding.etFrequency.text.toString().trim()
            val timeOfDay = dialogBinding.etTimeOfDay.text.toString().trim()
            val note = dialogBinding.etNote.text.toString().trim()

            if (medicationName.isEmpty()) {
                dialogBinding.etMedicationName.error = "Required"
                return@setOnClickListener
            }
            if (dosage.isEmpty()) {
                dialogBinding.etDosage.error = "Required"
                return@setOnClickListener
            }
            if (frequency.isEmpty()) {
                dialogBinding.etFrequency.error = "Required"
                return@setOnClickListener
            }
            if (timeOfDay.isEmpty()) {
                dialogBinding.etTimeOfDay.error = "Required"
                return@setOnClickListener
            }

            // Parse dates
            val startDateStr = dialogBinding.tvStartDate.text.toString()
            val endDateStr = dialogBinding.tvEndDate.text.toString()

            val startDate = try {
                LocalDate.parse(startDateStr, dateFormatter)
            } catch (e: Exception) {
                LocalDate.now()
            }

            val endDate = try {
                if (endDateStr.isNotEmpty()) LocalDate.parse(endDateStr, dateFormatter) else null
            } catch (e: Exception) {
                null
            }

            if (endDate != null && endDate.isBefore(startDate)) {
                dialogBinding.tvEndDate.error = "End date must be after start date"
                return@setOnClickListener
            }

            // Parse dosage amount from string
            val dosageAmount = try {
                dosage.toFloatOrNull() ?: 1.0f
            } catch (e: Exception) {
                1.0f
            }

            // Default to PILL if dosage unit can't be determined
            val dosageUnit = try {
                DosageUnit.valueOf(dosage.uppercase())
            } catch (e: Exception) {
                DosageUnit.None
            }

            // Default to BEFORE_MEAL if meal relation can't be determined
            val mealRelation = MealRelation.None

            // Create medication object
            val newMedication = Medication(
                medicationId = "",  // Will be filled by ViewModel
                userId = "",        // Will be filled by ViewModel
                visitId = null,     // Will be filled by ViewModel
                name = medicationName,
                dosageUnit = dosageUnit,
                dosageAmount = dosageAmount.toFloat(),
                frequency = frequency.toIntOrNull() ?: 1,
                timeOfDay = timeOfDay.split(",").map { it.trim() },
                mealRelation = mealRelation,
                startDate = startDate,
                endDate = endDate ?: startDate.plusMonths(1),
                notes = note
            )

            viewModel.saveMedicationAndFinish(newMedication)
            dialog.dismiss()
        }

        dialog.show()
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
        android.app.TimePickerDialog(
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

    // Extension function to convert Date to LocalDate
    private fun Date.toLocalDate(): LocalDate {
        return toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
}