package com.example.healthcareproject

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.DialogAddMedicationBinding
import com.example.healthcareproject.databinding.FragmentAddMedicationBinding
import com.example.healthcareproject.present.medicine.MedicalVisit
import com.example.healthcareproject.present.pill.Medication
import com.example.healthcareproject.present.pill.MedicationAdapter
import java.text.SimpleDateFormat
import java.util.*

class AddMedicationFragment : Fragment() {

    private var _binding: FragmentAddMedicationBinding? = null
    private val binding get() = _binding!!

    private val medications = mutableListOf<Medication>()
    private lateinit var medicationAdapter: MedicationAdapter
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private var selectedDate: Calendar? = null
    private var selectedTime: Calendar? = null
    private var selectedStartDate: Calendar? = null
    private var selectedEndDate: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập RecyclerView cho danh sách thuốc
        medicationAdapter = MedicationAdapter { medication ->
            val bundle = Bundle().apply {
                putParcelable("medication", medication)
            }
            findNavController().navigate(R.id.action_pillFragment_to_medicationDetailFragment, bundle)
        }
        binding.rvMedications.layoutManager = LinearLayoutManager(context)
        binding.rvMedications.adapter = medicationAdapter

        // Sự kiện click nút Back
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Sự kiện click chọn ngày
        binding.tvDate.setOnClickListener {
            showDatePicker { calendar ->
                selectedDate = calendar
                binding.tvDate.text = dateFormat.format(calendar.time)
            }
        }

        // Sự kiện click chọn giờ
        binding.tvTime.setOnClickListener {
            showTimePicker { calendar ->
                selectedTime = calendar
                binding.tvTime.text = timeFormat.format(calendar.time)
            }
        }

        // Sự kiện click nút Add Medication
        binding.btnAddMedication.setOnClickListener {
            showAddMedicationDialog()
        }

        // Sự kiện click nút Save
        binding.btnSave.setOnClickListener {
            val condition = binding.etCondition.text.toString().trim()
            val doctor = binding.etDoctor.text.toString().trim()
            val facility = binding.etFacility.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()

            // Validate Appointment Fields
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
            if (selectedDate == null) {
                binding.tvDate.error = "Required"
                return@setOnClickListener
            }
            if (selectedTime == null) {
                binding.tvTime.error = "Required"
                return@setOnClickListener
            }
            if (medications.isEmpty()) {
                binding.btnAddMedication.error = "Please add at least one medication"
                return@setOnClickListener
            }

            // Kết hợp ngày và giờ thành timestamp
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate!!.time
            calendar.set(Calendar.HOUR_OF_DAY, selectedTime!!.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, selectedTime!!.get(Calendar.MINUTE))
            val timestamp = calendar.timeInMillis

            // Create MedicalVisit object
            val newVisit = MedicalVisit(
                condition = condition,
                doctor = doctor,
                facility = facility,
                timestamp = timestamp,
                location = if (location.isNotEmpty()) location else null,
                diagnosis = null,
                doctorRemarks = null
            )

            // Gán visitId cho các Medication
            val updatedMedications = medications.map { it.copy(visitId = newVisit.id) }

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

        // Điều chỉnh kích thước dialog
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

        // Sự kiện click chọn ngày bắt đầu
        dialogBinding.tvStartDate.setOnClickListener {
            showDatePicker { calendar ->
                selectedStartDate = calendar
                dialogBinding.tvStartDate.text = dateFormat.format(calendar.time)
            }
        }

        // Sự kiện click chọn ngày kết thúc
        dialogBinding.tvEndDate.setOnClickListener {
            showDatePicker { calendar ->
                selectedEndDate = calendar
                dialogBinding.tvEndDate.text = dateFormat.format(calendar.time)
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

            // Validate Medication Fields
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
            if (selectedStartDate == null) {
                dialogBinding.tvStartDate.error = "Required"
                return@setOnClickListener
            }
            if (selectedEndDate != null && selectedStartDate != null) {
                if (selectedEndDate!!.before(selectedStartDate)) {
                    dialogBinding.tvEndDate.error = "End date must be after start date"
                    return@setOnClickListener
                }
            }

            // Create Medication object
            val newMedication = Medication(
                name = medicationName,
                dosage = dosage,
                frequency = frequency,
                timeOfDay = timeOfDay,
                startTimestamp = selectedStartDate!!.timeInMillis,
                endTimestamp = selectedEndDate?.timeInMillis,
                note = note
            )

            // Thêm vào danh sách và cập nhật RecyclerView
            medications.add(newMedication)
            medicationAdapter.notifyDataSetChanged()

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