package com.example.healthcareproject.present.pill

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentAddMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddMedicationFragment : Fragment() {

    private var _binding: FragmentAddMedicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicationViewModel by viewModels()
    private lateinit var medicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicationAdapter = MedicationAdapter()
        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicationAdapter
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvDate.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    binding.tvDate.text = selectedDate.format(formatter)
                    viewModel.setVisitDate(Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    })
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            ).show()
        }

        binding.tvTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                    binding.tvTime.text = formatter.format(calendar.time)
                    viewModel.setVisitTime(calendar)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnSave.setOnClickListener {
            val diagnosis = binding.etCondition.text.toString()
            val doctorName = binding.etDoctor.text.toString()
            val clinicName = binding.etFacility.text.toString()
            val location = binding.etLocation.text.toString().takeIf { it.isNotBlank() }

            if (diagnosis.isBlank() || doctorName.isBlank() || clinicName.isBlank()) {
                Snackbar.make(binding.root, "Please fill all required fields", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.saveMedicalVisit(diagnosis, doctorName, clinicName, location)
        }

        binding.btnAddMedication.setOnClickListener {
            showAddMedicationDialog()
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->

            medicationAdapter.submitList(state.medications)

            binding.btnAddMedication.isEnabled = state.isVisitSaved && !state.isLoading

            binding.btnSave.isEnabled = !state.isLoading

            state.error?.let { error ->
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }

            // Handle finish
            if (state.isFinished) {
                viewModel.saveAllMedications()
                Snackbar.make(binding.root, "Saved successfully", Snackbar.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun showAddMedicationDialog() {
        val dialogView = LayoutInflater
            .from(requireContext())
            .inflate(R.layout.dialog_add_medication, null)
        val etMedicationName = dialogView.findViewById<EditText>(R.id.et_medication_name)
        val etDosageAmount = dialogView.findViewById<EditText>(R.id.et_dosage_amount)
        val spinnerDosageUnit = dialogView.findViewById<Spinner>(R.id.spinner_dosage_unit)
        val dosageUnits = DosageUnit.entries.map { it.toDisplayString() }
        spinnerDosageUnit.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, dosageUnits)
        val etFrequency = dialogView.findViewById<EditText>(R.id.et_frequency)
        val etTimeOfDay = dialogView.findViewById<EditText>(R.id.et_time_of_day)
        val spinnerMealRelation = dialogView.findViewById<Spinner>(R.id.spinner_meal_relation)
        val mealRelations = MealRelation.entries.map {
            it.name.replace("_", " ")
                .lowercase()
                .replaceFirstChar { it.uppercase() }
        }
        spinnerMealRelation.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, mealRelations)
        val tvStartDate = dialogView.findViewById<TextView>(R.id.tv_start_date)
        val tvEndDate = dialogView.findViewById<TextView>(R.id.tv_end_date)
        val etNote = dialogView.findViewById<EditText>(R.id.et_note)

        var startDate: LocalDate? = null
        tvStartDate.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    startDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    tvStartDate.text = startDate?.format(formatter)
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            ).show()
        }

        var endDate: LocalDate? = null
        tvEndDate.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    endDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    tvEndDate.text = endDate?.format(formatter)
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            ).show()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etMedicationName.text.toString()
                val dosageAmount = etDosageAmount.text.toString().toDoubleOrNull() ?: 0.0
                val dosageUnit = DosageUnit.entries[spinnerDosageUnit.selectedItemPosition]
                val frequency = etFrequency.text.toString().toIntOrNull() ?: 1
                val timeOfDay = etTimeOfDay.text.toString().split(",").map { it.trim() }
                val mealRelation = MealRelation.entries[spinnerMealRelation.selectedItemPosition]
                val note = etNote.text.toString()

                if (name.isBlank() || dosageAmount == 0.0 || frequency == 0 || timeOfDay.isEmpty() || startDate == null) {
                    Snackbar.make(binding.root, "Please fill all required fields", Snackbar.LENGTH_LONG).show()
                    return@setPositiveButton
                }

                if (endDate != null && endDate!!.isBefore(startDate)) {
                    Snackbar.make(binding.root, "End date cannot be before start date", Snackbar.LENGTH_LONG).show()
                    return@setPositiveButton
                }

                val medication = Medication(
                    medicationId = "",
                    userId = "",
                    visitId = "",
                    name = name,
                    dosageUnit = dosageUnit,
                    dosageAmount = dosageAmount.toFloat(),
                    frequency = frequency,
                    timeOfDay = timeOfDay,
                    mealRelation = mealRelation,
                    startDate = startDate!!,
                    endDate = endDate ?: startDate!!.plusMonths(1),
                    notes = note
                )
                viewModel.addMedicationToList(medication)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}