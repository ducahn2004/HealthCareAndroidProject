package com.example.healthcareproject.present.ui.medication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentAddMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.viewmodel.medication.AddMedicationViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
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

        // Setup RecyclerView with MedicationAdapter
        medicationAdapter = MedicationAdapter() // onItemClick mặc định là rỗng
        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicationAdapter
        }

        // Back button
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Date picker for visit date
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
                today.monthValue - 12,
                today.dayOfMonth
            ).show()
        }

        // Time picker for visit time
        binding.tvTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                    binding.tvTime.text = formatter.format(calendar.time)
                    viewModel.setVisitTime(calendar)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Save MedicalVisit and Medications
        binding.btnSave.setOnClickListener {
            val diagnosis = binding.etCondition.text.toString()
            val doctorName = binding.etDoctor.text.toString()
            val clinicName = binding.etFacility.text.toString()

            if (diagnosis.isBlank()) {
                binding.etCondition.error = "Condition is required"
                return@setOnClickListener
            }
            if (doctorName.isBlank()) {
                binding.etDoctor.error = "Doctor name is required"
                return@setOnClickListener
            }
            if (clinicName.isBlank()) {
                binding.etFacility.error = "Facility is required"
                return@setOnClickListener
            }

            Timber.d("Save button clicked: Saving MedicalVisit")
            viewModel.saveMedicalVisit(diagnosis, doctorName, clinicName)
        }

        // Add Medication
        binding.btnAddMedication.setOnClickListener {
            Timber.d("Add Medication button clicked: Showing dialog")
            showAddMedicationDialog()
        }

        // Observe UI state
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // Update RecyclerView
            medicationAdapter.submitList(state.medications)

            // Enable/disable Add Medication button
            binding.btnAddMedication.isEnabled = !state.isLoading

            // Handle loading
            binding.btnSave.isEnabled = !state.isLoading

            // Handle errors
            state.error?.let { error ->
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
            }

            // Handle finish
            if (state.isFinished) {
                Timber.d("Saving all medications")
                viewModel.saveAllMedications()
                setFragmentResult("medicationKey", Bundle().apply {
                    putString("visitId", state.visitId)
                    putBoolean("navigateToVisit", false)
                })
                Snackbar.make(binding.root, "Appointment and medications saved successfully", Snackbar.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun showAddMedicationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_medication, null)
        val etMedicationName = dialogView.findViewById<EditText>(R.id.et_medication_name)
        val etDosageAmount = dialogView.findViewById<EditText>(R.id.et_dosage_amount)
        val spinnerDosageUnit = dialogView.findViewById<Spinner>(R.id.spinner_dosage_unit)
        val dosageUnits = DosageUnit.values().map { it.toDisplayString() }
        spinnerDosageUnit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dosageUnits)
        val etFrequency = dialogView.findViewById<EditText>(R.id.et_frequency)
        val etTimeOfDay = dialogView.findViewById<EditText>(R.id.et_time_of_day)
        val spinnerMealRelation = dialogView.findViewById<Spinner>(R.id.spinner_meal_relation)
        val mealRelations = MealRelation.values().map { it.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() } }
        spinnerMealRelation.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mealRelations)
        val tvStartDate = dialogView.findViewById<TextView>(R.id.tv_start_date)
        val tvEndDate = dialogView.findViewById<TextView>(R.id.tv_end_date)
        val etNote = dialogView.findViewById<EditText>(R.id.et_note)
        val scrollView = dialogView.findViewById<ScrollView>(R.id.scroll_view)
        val btnAdd = dialogView.findViewById<Button>(R.id.btn_add)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        // Handle keyboard for EditText fields
        val editTexts = listOf(etMedicationName, etDosageAmount, etFrequency, etTimeOfDay, etNote)
        editTexts.forEach { editText ->
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Timber.d("IME_ACTION_DONE triggered for ${editText.id}")
                    editText.clearFocus()
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    true
                } else {
                    false
                }
            }
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Timber.d("Focus gained on ${editText.id}, scrolling to view")
                    scrollView.post {
                        scrollView.smoothScrollTo(0, editText.top)
                    }
                }
            }
        }

        // Clear focus of spinner after selection
        spinnerMealRelation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Timber.d("Meal Relation selected: ${mealRelations[position]}")
                spinnerMealRelation.clearFocus()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Date picker for start date
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

        // Date picker for end date
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

        // Create dialog before setting listeners
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // Handle Add button click
        btnAdd.setOnClickListener {
            Timber.d("Add button in dialog clicked")
            val name = etMedicationName.text.toString()
            val dosageAmount = etDosageAmount.text.toString().toDoubleOrNull()
            val dosageUnit = DosageUnit.values()[spinnerDosageUnit.selectedItemPosition]
            val frequency = etFrequency.text.toString().toIntOrNull()
            val timeOfDay = etTimeOfDay.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() }
            val mealRelation = MealRelation.values()[spinnerMealRelation.selectedItemPosition]
            val note = etNote.text.toString()

            // Validation with specific error messages
            if (name.isBlank()) {
                etMedicationName.error = "Medication name is required"
                return@setOnClickListener
            }
            if (dosageAmount == null || dosageAmount == 0.0) {
                etDosageAmount.error = "Valid dosage amount is required"
                return@setOnClickListener
            }
            if (frequency == null || frequency == 0) {
                etFrequency.error = "Valid frequency is required"
                return@setOnClickListener
            }
            if (timeOfDay.isEmpty()) {
                etTimeOfDay.error = "At least one time of day is required"
                return@setOnClickListener
            }
            if (startDate == null) {
                Snackbar.make(binding.root, "Start date is required", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (endDate != null && endDate.isBefore(startDate)) {
                Snackbar.make(binding.root, "End date cannot be before start date", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
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
                startDate = startDate,
                endDate = endDate ?: startDate.plusMonths(1),
                notes = note
            )
            Timber.d("Adding medication: $medication")
            viewModel.addMedicationToList(medication)
            Snackbar.make(binding.root, "Medication added", Snackbar.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // Handle Cancel button click from layout
        btnCancel.setOnClickListener {
            Timber.d("Cancel button in dialog clicked")
            dialog.dismiss()
        }

        // Configure windowSoftInputMode
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}