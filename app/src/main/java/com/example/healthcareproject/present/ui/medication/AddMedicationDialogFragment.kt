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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.healthcareproject.databinding.DialogAddMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.viewmodel.medication.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class AddMedicationDialogFragment : DialogFragment() {

    private var _binding: DialogAddMedicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicationViewModel by viewModels()
    private var medicationToEdit: Medication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth)
        // Retrieve medication from arguments if editing
        medicationToEdit = arguments?.getParcelable(ARG_MEDICATION)
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
        setupSpinners()
        setupDatePickers()
        setupButtons()
        observeViewModel()
        // Prepopulate fields if editing
        medicationToEdit?.let { prepopulateFields(it) }
    }

    private fun prepopulateFields(medication: Medication) {
        viewModel.apply {
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
        // Dosage Unit Spinner
        val dosageUnits = DosageUnit.entries.map { it.name }
        val dosageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dosageUnits
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerDosageUnit.adapter = dosageAdapter
        binding.spinnerDosageUnit.setSelection(medicationToEdit?.dosageUnit?.ordinal ?: DosageUnit.None.ordinal)
        binding.spinnerDosageUnit.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setDosageUnit(DosageUnit.entries[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                viewModel.setDosageUnit(DosageUnit.None)
            }
        }

        // Meal Relation Spinner
        val mealRelations = MealRelation.entries.map { it.name }
        val mealAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mealRelations
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerMealRelation.adapter = mealAdapter
        binding.spinnerMealRelation.setSelection(medicationToEdit?.mealRelation?.ordinal ?: MealRelation.None.ordinal)
        binding.spinnerMealRelation.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setMealRelation(MealRelation.entries[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                viewModel.setMealRelation(MealRelation.None)
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
        binding.btnAdd.setOnClickListener {
            viewModel.addMedication()
            viewModel.saveAllMedications()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.isFinished.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished == true) {
                Toast.makeText(requireContext(), "Medication saved successfully", Toast.LENGTH_SHORT).show()
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

        fun newInstance(): AddMedicationDialogFragment {
            return AddMedicationDialogFragment()
        }

        fun newInstance(medication: Medication): AddMedicationDialogFragment {
            return AddMedicationDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_MEDICATION, medication)
                }
            }
        }
    }
}