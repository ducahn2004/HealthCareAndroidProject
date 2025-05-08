package com.example.healthcareproject.present.ui.medication

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.DialogAddMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import com.example.healthcareproject.present.navigation.MainNavigator
import com.example.healthcareproject.present.viewmodel.medication.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for handling the logic of adding a new medication in the dialog.
 */
@AndroidEntryPoint
class AddMedicationDialogFragment : DialogFragment() {
    private var _binding: DialogAddMedicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicationViewModel by viewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_medication, container, false)
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
    }

    private fun setupSpinners() {
        // Dosage Unit Spinner
        val dosageUnits = DosageUnit.values().map { it.toDisplayString() }
        val dosageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dosageUnits
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerDosageUnit.adapter = dosageAdapter
        binding.spinnerDosageUnit.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setDosageUnit(position)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        // Meal Relation Spinner
        val mealRelations = MealRelation.values().map { it.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() } }
        val mealAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mealRelations
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerMealRelation.adapter = mealAdapter
        binding.spinnerMealRelation.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setMealRelation(position)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
    }

    private fun setupDatePickers() {
        val today = LocalDate.now()

        binding.tvStartDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    viewModel.setStartDate(year, month, day)
                },
                today.year, today.monthValue - 1, today.dayOfMonth
            )
            datePicker.show()
        }

        binding.tvEndDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    viewModel.setEndDate(year, month, day)
                },
                today.year, today.monthValue - 1, today.dayOfMonth
            )
            datePicker.show()
        }
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnAdd.setOnClickListener {
            val visitId = arguments?.getString("visitId") ?: "" // Adjust based on your app's logic
            viewModel.addMedication(visitId)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAdd.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.dismissDialog.observe(viewLifecycleOwner) { shouldDismiss ->
            if (shouldDismiss) {
                // Notify PillFragment to refresh
                parentFragmentManager.setFragmentResult("medicationKey", Bundle().apply {
                    putBoolean("medicationAdded", true)
                })
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}