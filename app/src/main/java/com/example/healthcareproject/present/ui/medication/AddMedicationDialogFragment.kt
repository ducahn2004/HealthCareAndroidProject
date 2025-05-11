package com.example.healthcareproject.present.ui.medication

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.Observable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.healthcareproject.data.source.network.datasource.MedicationDataSource
import com.example.healthcareproject.databinding.DialogAddMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.viewmodel.medication.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddMedicationDialogFragment : DialogFragment() {

    private var _binding: DialogAddMedicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicationViewModel by viewModels()
    private var medicationToEdit: Medication? = null
    private var sourceFragment: String? = null // To track which fragment opened this dialog
    @Inject lateinit var medicationDataSource: MedicationDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth)
        medicationToEdit = arguments?.getParcelable(ARG_MEDICATION)
        sourceFragment = arguments?.getString(ARG_SOURCE_FRAGMENT)
        // Attache visitId to ViewModel
        val visitId = arguments?.getString(ARG_VISIT_ID)
        viewModel.setVisitId(visitId)
        Timber.d("AddMedicationDialogFragment onCreate with visitId: $visitId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddMedicationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val visitId = arguments?.getString(ARG_VISIT_ID)
        Timber.d("AddMedicationDialogFragment initialized with visitId: $visitId")
        viewModel.setVisitId(visitId)
        setupSpinners()
        setupDatePickers()
        setupButtons()
        observeViewModel()
        medicationToEdit?.let { prepopulateFields(it) }
    }

    private fun prepopulateFields(medication: Medication) {
        if (medication.medicationId.isBlank()) {
            Toast.makeText(requireContext(), "Invalid medication ID", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }
        viewModel.apply {
            setMedicationId(medication.medicationId)
            medicationName.set(medication.name)
            dosageAmount.set(medication.dosageAmount.toString())
            dosageUnit.set(medication.dosageUnit)
            frequency.set(medication.frequency.toString())
            timeOfDay.set(medication.timeOfDay.joinToString(","))
            mealRelation.set(medication.mealRelation)
            startDate.set(medication.startDate)
            endDate.set(medication.endDate)
            notes.set(medication.notes)
        }
    }


    private fun setupSpinners() {
        val dosageUnits = DosageUnit.entries.map { it.name }
        val dosageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dosageUnits
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerDosageUnit.adapter = dosageAdapter
        binding.spinnerDosageUnit.setSelection(medicationToEdit?.dosageUnit?.ordinal ?: DosageUnit.Cup.ordinal)
        binding.spinnerDosageUnit.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setDosageUnit(DosageUnit.entries[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                viewModel.setDosageUnit(DosageUnit.Cup)
            }
        }

        val mealRelations = MealRelation.entries.map { it.name }
        val mealAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mealRelations
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerMealRelation.adapter = mealAdapter
        binding.spinnerMealRelation.setSelection(medicationToEdit?.mealRelation?.ordinal ?: MealRelation.AfterMeal.ordinal)
        binding.spinnerMealRelation.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setMealRelation(MealRelation.entries[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                viewModel.setMealRelation(MealRelation.AfterMeal)
            }
        }
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        binding.tvStartDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    viewModel.setStartDate(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.tvEndDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    viewModel.setEndDate(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun setupButtons() {
        if (medicationToEdit != null) {
            binding.btnAdd.text = "Update"
            binding.btnAdd.setOnClickListener {
                Timber.d("Update button clicked")
                if (validateInputs()) {
                    Timber.d("Inputs validated, calling updateMedication()")
                    viewModel.updateMedication()
                }
            }
        } else {
            binding.btnAdd.setOnClickListener {
                Timber.d("Add button clicked")
                if (validateInputs()) {
                    Timber.d("Inputs validated, calling addMedication()")
                    viewModel.addMedication(syncToNetwork = sourceFragment == SOURCE_PILL_FRAGMENT)
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        if (viewModel.medicationName.get().isNullOrBlank()) {
            Toast.makeText(requireContext(), "Medication name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (viewModel.dosageAmount.get().isNullOrBlank()) {
            Toast.makeText(requireContext(), "Dosage amount is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (viewModel.frequency.get()?.toIntOrNull()?.let { it <= 0 } != false) {
            Toast.makeText(requireContext(), "Valid frequency is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (viewModel.timeOfDay.get()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.isEmpty() != false) {
            Toast.makeText(requireContext(), "At least one time of day is required", Toast.LENGTH_SHORT).show()
            return false
        }
        // Check if startDate is before or equal to endDate (if endDate is set)
        val startDate = viewModel.startDate.get()
        val endDate = viewModel.endDate.get()
        if (startDate == null) {
            Toast.makeText(requireContext(), "Start date is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (endDate != null && startDate.isAfter(endDate)) {
            Toast.makeText(requireContext(), "Start date must be before or equal to end date", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.isFinished.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished == true && !isStateSaved) {
                val medication = Medication(
                    medicationId = medicationToEdit?.medicationId ?: "",
                    userId = "",
                    visitId = viewModel.getVisitId(), // Sử dụng visitId từ ViewModel
                    name = viewModel.medicationName.get() ?: "",
                    dosageUnit = viewModel.dosageUnit.get() ?: DosageUnit.None,
                    dosageAmount = viewModel.dosageAmount.get()?.toFloatOrNull() ?: 0f,
                    frequency = viewModel.frequency.get()?.toIntOrNull() ?: 0,
                    timeOfDay = viewModel.timeOfDay.get()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
                    mealRelation = viewModel.mealRelation.get() ?: MealRelation.None,
                    startDate = viewModel.startDate.get() ?: LocalDate.now(),
                    endDate = viewModel.endDate.get() ?: LocalDate.now().plusMonths(1),
                    notes = viewModel.notes.get() ?: ""
                )
                Timber.d("Creating Medication object with visitId: ${viewModel.getVisitId()}")
                Timber.d("Returning medication with visitId: ${medication.visitId}")
                val resultKey = when (sourceFragment) {
                    SOURCE_PILL_FRAGMENT -> RESULT_KEY_PILL_FRAGMENT
                    SOURCE_MEDICAL_VISIT_FRAGMENT -> RESULT_KEY_MEDICAL_VISIT_FRAGMENT
                    else -> RESULT_KEY_DEFAULT
                }

                setFragmentResult(resultKey, Bundle().apply {
                    putBoolean("medicationAdded", true)
                    putParcelable("medication", medication)
                    putString("sourceFragment", sourceFragment)
                })

                dismiss()
            }
        })

        viewModel.isLoading.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val isLoading = viewModel.isLoading.get() ?: false
                binding.btnAdd.isEnabled = !isLoading
                binding.btnCancel.isEnabled = !isLoading
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MEDICATION = "arg_medication"
        private const val ARG_SOURCE_FRAGMENT = "arg_source_fragment"
        private const val ARG_VISIT_ID = "arg_visit_id"

        const val SOURCE_PILL_FRAGMENT = "pill_fragment"
        const val SOURCE_MEDICAL_VISIT_FRAGMENT = "medical_visit_fragment"

        const val RESULT_KEY_DEFAULT = "medicationKey"
        const val RESULT_KEY_PILL_FRAGMENT = "medicationKeyPill"
        const val RESULT_KEY_MEDICAL_VISIT_FRAGMENT = "medicationKeyMedicalVisit"

        fun newInstance(): AddMedicationDialogFragment {
            return AddMedicationDialogFragment()
        }

        fun newInstance(medication: Medication? = null): AddMedicationDialogFragment {
            return AddMedicationDialogFragment().apply {
                arguments = Bundle().apply {
                    medication?.let { putParcelable(ARG_MEDICATION, it) }
                }
            }
        }

        fun newInstance(medication: Medication? = null, sourceFragment: String? = null, visitId: String? = null): AddMedicationDialogFragment {
            return AddMedicationDialogFragment().apply {
                arguments = Bundle().apply {
                    medication?.let { putParcelable(ARG_MEDICATION, it) }
                    sourceFragment?.let { putString(ARG_SOURCE_FRAGMENT, it) }
                    visitId?.let { putString(ARG_VISIT_ID, it) }
                }
            }
        }
    }
}