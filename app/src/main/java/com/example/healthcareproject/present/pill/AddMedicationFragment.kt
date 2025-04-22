package com.example.healthcareproject.present.pill

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.DialogAddMedicationBinding
import com.example.healthcareproject.databinding.FragmentAddMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.present.medicine.MedicalVisit
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class AddMedicationFragment : Fragment() {

    private var _binding: FragmentAddMedicationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddMedicationViewModel by viewModels()
    private lateinit var medicationAdapter: MedicationAdapter
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicationAdapter = MedicationAdapter { medication ->
            val bundle = Bundle().apply {
                putParcelable("medication", medication)
            }
            findNavController().navigate(R.id.action_pillFragment_to_medicalHistoryDetailFragment, bundle)
        }
        binding.rvMedications.layoutManager = LinearLayoutManager(context)
        binding.rvMedications.adapter = medicationAdapter

        // Observe medications from ViewModel
        viewModel.medications.observe(viewLifecycleOwner) { medications ->
            medicationAdapter.submitList(medications)
        }

        // Observe selected date and time
        viewModel.selectedDate.observe(viewLifecycleOwner) { calendar ->
            if (calendar != null) {
                binding.tvDate.text = dateFormat.format(calendar.time)
            }
        }

        viewModel.selectedTime.observe(viewLifecycleOwner) { calendar ->
            if (calendar != null) {
                binding.tvTime.text = timeFormat.format(calendar.time)
            }
        }

        // Sự kiện click nút Back
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvDate.setOnClickListener {
            showDatePicker { calendar ->
                viewModel.setSelectedDate(calendar)
            }
        }

        binding.tvTime.setOnClickListener {
            showTimePicker { calendar ->
                viewModel.setSelectedTime(calendar)
            }
        }

        binding.btnAddMedication.setOnClickListener {
            showAddMedicationDialog()
        }

        binding.btnSave.setOnClickListener {
            val diagnosis = binding.etCondition.text.toString().trim()
            val doctorName = binding.etDoctor.text.toString().trim()
            val clinicName = binding.etFacility.text.toString().trim()

            // Validate Appointment Fields
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
            if (viewModel.selectedDate.value == null) {
                binding.tvDate.error = "Required"
                return@setOnClickListener
            }
            if (viewModel.selectedTime.value == null) {
                binding.tvTime.error = "Required"
                return@setOnClickListener
            }
            if (viewModel.medications.value?.isEmpty() != false) {
                binding.btnAddMedication.error = "Please add at least one medication"
                return@setOnClickListener
            }

            // Kết hợp ngày và giờ thành LocalDateTime
            val calendar = Calendar.getInstance()
            calendar.time = viewModel.selectedDate.value!!.time
            calendar.set(Calendar.HOUR_OF_DAY, viewModel.selectedTime.value!!.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, viewModel.selectedTime.value!!.get(Calendar.MINUTE))
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

            // Gán visitId cho các Medication
            val updatedMedications = viewModel.medications.value!!.map { it.copy(visitId = newVisit.visitId) }

            // Truyền dữ liệu về thông qua setFragmentResult
            setFragmentResult("medicationKey", Bundle().apply {
                putParcelable("newVisit", newVisit)
                putParcelableArrayList("newMedications", ArrayList(updatedMedications))
            })

            // Quay lại PillFragment
            findNavController().navigateUp()
        }
    }

    private fun showAddMedicationDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogAddMedicationBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val window = dialog.window
        if (window != null) {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = (displayMetrics.widthPixels * 0.9).toInt()
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = layoutParams
        }

        // Set up Dosage Unit Spinner
        val dosageUnits = DosageUnit.entries.map { it.name.lowercase() }
        val dosageUnitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dosageUnits)
        dialogBinding.spinnerDosageUnit.adapter = dosageUnitAdapter

        // Set up Meal Relation Spinner
        val mealRelations = MealRelation.entries.map { it.name.replace("_", " ").lowercase() }
        val mealRelationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mealRelations)
        dialogBinding.spinnerMealRelation.adapter = mealRelationAdapter

        // Auto-generate Medication ID
        dialogBinding.etMedicationId.setText(UUID.randomUUID().toString())

        // Sự kiện click chọn ngày bắt đầu
        dialogBinding.tvStartDate.setOnClickListener {
            showDatePicker { calendar ->
                val date = calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                viewModel.setSelectedStartDate(date)
                dialogBinding.tvStartDate.text = dateFormat.format(calendar.time)
            }
        }

        // Sự kiện click chọn ngày kết thúc
        dialogBinding.tvEndDate.setOnClickListener {
            showDatePicker { calendar ->
                val date = calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                viewModel.setSelectedEndDate(date)
                dialogBinding.tvEndDate.text = dateFormat.format(calendar.time)
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnAdd.setOnClickListener {
            val medicationId = dialogBinding.etMedicationId.text.toString().trim()
            val userId = dialogBinding.etUserId.text.toString().trim()
            val medicationName = dialogBinding.etMedicationName.text.toString().trim()
            val dosageAmount = dialogBinding.etDosageAmount.text.toString().trim()
            val dosageUnit = dialogBinding.spinnerDosageUnit.selectedItem.toString()
            val frequency = dialogBinding.etFrequency.text.toString().trim()
            val timeOfDay = dialogBinding.etTimeOfDay.text.toString().trim()
            val mealRelation = dialogBinding.spinnerMealRelation.selectedItem.toString()
            val notes = dialogBinding.etNotes.text.toString().trim()

            // Validate Medication Fields
            if (medicationId.isEmpty()) {
                dialogBinding.etMedicationId.error = "Required"
                return@setOnClickListener
            }
            if (userId.isEmpty()) {
                dialogBinding.etUserId.error = "Required"
                return@setOnClickListener
            }
            if (medicationName.isEmpty()) {
                dialogBinding.etMedicationName.error = "Required"
                return@setOnClickListener
            }
            if (dosageAmount.isEmpty()) {
                dialogBinding.etDosageAmount.error = "Required"
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
            if (viewModel.selectedStartDate.value == null) {
                dialogBinding.tvStartDate.error = "Required"
                return@setOnClickListener
            }
            if (viewModel.selectedEndDate.value != null && viewModel.selectedStartDate.value != null) {
                if (viewModel.selectedEndDate.value!!.isBefore(viewModel.selectedStartDate.value)) {
                    dialogBinding.tvEndDate.error = "End date must be after start date"
                    return@setOnClickListener
                }
            }

            // Create Medication object
            val newMedication = Medication(
                medicationId = medicationId,
                userId = userId,
                visitId = null,
                name = medicationName,
                dosageUnit = DosageUnit.valueOf(dosageUnit.uppercase()),
                dosageAmount = dosageAmount.toFloat(),
                frequency = frequency.toInt(),
                timeOfDay = timeOfDay.split(",").map { it.trim() },
                mealRelation = MealRelation.valueOf(mealRelation.replace(" ", "_").uppercase()),
                startDate = viewModel.selectedStartDate.value!!,
                endDate = viewModel.selectedEndDate.value!!,
                notes = notes
            )

            // Add to ViewModel
            viewModel.addMedication(newMedication)

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