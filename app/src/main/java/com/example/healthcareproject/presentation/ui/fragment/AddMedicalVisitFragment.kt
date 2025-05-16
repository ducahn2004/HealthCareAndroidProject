package com.example.healthcareproject.presentation.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.FragmentAddMedicalVisitBinding
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.presentation.navigation.MainNavigator
import com.example.healthcareproject.presentation.ui.adapter.MedicationAdapter
import com.example.healthcareproject.presentation.viewmodel.medicine.AddMedicalVisitViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddMedicalVisitFragment : Fragment() {

    private var _binding: FragmentAddMedicalVisitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicalVisitViewModel by viewModels()

    @Inject
    lateinit var navigator: MainNavigator

    private lateinit var medicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicalVisitBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        setupFragmentResultListener()
    }

    private fun setupRecyclerView() {
        medicationAdapter = MedicationAdapter(
            onEdit = { medication ->
                // Use the updated newInstance with source fragment information
                val dialog = AddMedicationDialogFragment.newInstance(
                    medication = medication,
                    sourceFragment = AddMedicationDialogFragment.SOURCE_MEDICAL_VISIT_FRAGMENT,
                    visitId = viewModel.getVisitId()
                )
                dialog.show(parentFragmentManager, "EditMedicationDialog")
            },
            onDelete = { medication ->
                viewModel.removeMedication(medication)
                Snackbar.make(binding.root, "Medication deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.addMedication(medication)
                    }.show()
            }
        )
        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicationAdapter
        }

        // Enable drag-and-drop reordering
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                viewModel.reorderMedications(from, to)
                medicationAdapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvMedications)
    }

    private fun setupClickListeners() {
        val calendar = Calendar.getInstance()

        binding.ivBack.setOnClickListener {
            Timber.tag("AddMedicalVisitFragment").d("Back button clicked")
            navigator.navigateBackToMedicineFromAddMedicalVisit()
        }

        binding.tvDateTime.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val timePicker = TimePickerDialog(
                        requireContext(),
                        { _, hourOfDay, minute ->
                            val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                            viewModel.setVisitDateTime(selectedDateTime)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePicker.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.btnAddMedication.setOnClickListener {
            // Use the updated newInstance with source fragment information
            val dialog = AddMedicationDialogFragment.newInstance(
                sourceFragment = AddMedicationDialogFragment.SOURCE_MEDICAL_VISIT_FRAGMENT,
                visitId = viewModel.getVisitId()
            )
            Timber.d("Showing AddMedicationDialog with visitId: ${viewModel.getVisitId()}")
            dialog.show(parentFragmentManager, "AddMedicationDialog")
        }

        binding.btnSave.setOnClickListener {
            val diagnosis = binding.etCondition.text.toString()
            val doctorName = binding.etDoctor.text.toString()
            val clinicName = binding.etFacility.text.toString()

            if (diagnosis.isBlank()) {
                binding.etCondition.error = "Diagnosis is required"
                return@setOnClickListener
            }
            if (doctorName.isBlank()) {
                binding.etDoctor.error = "Doctor name is required"
                return@setOnClickListener
            }
            if (clinicName.isBlank()) {
                binding.etFacility.error = "Clinic name is required"
                return@setOnClickListener
            }

            viewModel.diagnosis.set(diagnosis)
            viewModel.doctorName.set(doctorName)
            viewModel.clinicName.set(clinicName)

            if (viewModel.getMedications().isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("No medications added. Save anyway?")
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.saveMedicalVisit()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                viewModel.saveMedicalVisit()
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(AddMedicationDialogFragment.RESULT_KEY_MEDICAL_VISIT_FRAGMENT) { _, bundle ->
            if (bundle.getBoolean("medicationAdded", false)) {
                val medication = bundle.getParcelable<Medication>("medication")
                if (medication != null) {
                    Timber.d("Received medication with visitId: ${medication.visitId}")

                    val updatedMedication = if (medication.visitId != viewModel.getVisitId()) {
                        medication.copy(visitId = viewModel.getVisitId())
                    } else {
                        medication
                    }

                    // Tạo ID nếu là medication mới
                    if (updatedMedication.medicationId.isBlank()) {
                        viewModel.addMedication(
                            updatedMedication.copy(
                                medicationId = UUID.randomUUID().toString()
                            )
                        )
                    } else {
                        viewModel.updateMedication(updatedMedication)
                    }

                    medicationAdapter.submitList(viewModel.getMedications())
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                binding.etCondition.error = if (it.contains("Diagnosis")) it else null
                binding.etDoctor.error = if (it.contains("Doctor")) it else null
                binding.etFacility.error = if (it.contains("Facility")) it else null
            }
        }

        viewModel.isFinished.observe(viewLifecycleOwner) { isFinished ->
            if (isFinished == true) {
                Toast.makeText(requireContext(), "Medical visit saved successfully", Toast.LENGTH_SHORT).show()
                val medicalVisit = MedicalVisit(
                    visitId = viewModel.getVisitId(),
                    userId = "",
                    visitDate = viewModel.visitDateTime.get()?.toLocalDate() ?: LocalDate.now(),
                    clinicName = viewModel.clinicName.get() ?: "",
                    doctorName = viewModel.doctorName.get() ?: "",
                    diagnosis = viewModel.diagnosis.get() ?: "",
                    treatment = "",
                    createdAt = viewModel.visitDateTime.get() ?: LocalDateTime.now()
                )
                setFragmentResult("requestKey", Bundle().apply {
                    putParcelable("newVisit", medicalVisit)
                })
                Timber.d("Navigating back after saving MedicalVisit")
                navigator.navigateBackToMedicineFromAddMedicalVisit()
            }
        }

        viewModel.isLoading.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val isLoading = viewModel.isLoading.get() ?: false
                binding.btnSave.isEnabled = !isLoading
                binding.btnAddMedication.isEnabled = !isLoading
            }
        })

        viewModel.medications.observe(viewLifecycleOwner) { medications ->
            medicationAdapter.submitList(medications)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}