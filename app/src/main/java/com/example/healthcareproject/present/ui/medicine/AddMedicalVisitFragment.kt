package com.example.healthcareproject.present.ui.medicine

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.FragmentAddMedicalVisitBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.ui.medication.AddMedicationDialogFragment
import com.example.healthcareproject.present.viewmodel.medicine.AddMedicalVisitViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class AddMedicalVisitFragment : Fragment() {

    private var _binding: FragmentAddMedicalVisitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicalVisitViewModel by viewModels()
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
    }

    private fun setupRecyclerView() {
        medicationAdapter = MedicationAdapter(
            onEdit = { medication ->
                val dialog = AddMedicationDialogFragment.newInstance(medication)
                dialog.setTargetFragment(this, REQUEST_CODE_EDIT_MEDICATION)
                dialog.show(parentFragmentManager, "EditMedicationDialog")
            },
            onDelete = { medication ->
                viewModel.removeMedication(medication)
                medicationAdapter.submitList(viewModel.getMedications())
                Snackbar.make(binding.root, "Medication deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.addMedication(medication)
                        medicationAdapter.submitList(viewModel.getMedications())
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
            requireActivity().onBackPressed()
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
            val dialog = AddMedicationDialogFragment.newInstance()
            dialog.setTargetFragment(this, REQUEST_CODE_ADD_MEDICATION)
            dialog.show(parentFragmentManager, "AddMedicationDialog")
        }

        binding.btnSave.setOnClickListener {
            if (viewModel.getMedications().isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("No medications added. Save anyway?")
                    .setPositiveButton("Yes") { _, _ -> viewModel.saveMedicalVisit() }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                viewModel.saveMedicalVisit()
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
                requireActivity().onBackPressed()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val medication = data.getParcelableExtra<Medication>(EXTRA_MEDICATION)
            if (medication != null) {
                when (requestCode) {
                    REQUEST_CODE_ADD_MEDICATION -> viewModel.addMedication(medication)
                    REQUEST_CODE_EDIT_MEDICATION -> viewModel.updateMedication(medication)
                }
                medicationAdapter.submitList(viewModel.getMedications())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_ADD_MEDICATION = 1001
        private const val REQUEST_CODE_EDIT_MEDICATION = 1002
        const val EXTRA_MEDICATION = "extra_medication"
    }
}