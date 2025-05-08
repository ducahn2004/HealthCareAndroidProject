package com.example.healthcareproject.present.ui.medication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentPillBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PillFragment : Fragment() {
    private lateinit var binding: FragmentPillBinding
    private val viewModel: PillViewModel by viewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var currentMedicationAdapter: MedicationAdapter
    private lateinit var pastMedicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pill,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupClickListeners()
        observeMedications()
        setupFragmentResultListener()
    }

    private fun setupRecyclerViews() {
        currentMedicationAdapter = MedicationAdapter(
            onEdit = { medication ->
                showEditMedicationDialog(medication)
            },
            onDelete = { medication ->
                showDeleteConfirmationDialog(medication)
            },
            onItemClick = { medication ->
                medication.visitId?.takeIf { it.isNotEmpty() }?.let { visitId ->
                    mainNavigator.navigateToMedicalHistoryDetail(visitId)
                } ?: run {
                    Toast.makeText(context, "No medical visit associated with this medication", Toast.LENGTH_SHORT).show()
                }
            }
        )
        pastMedicationAdapter = MedicationAdapter(
            onEdit = { medication ->
                showEditMedicationDialog(medication)
            },
            onDelete = { medication ->
                showDeleteConfirmationDialog(medication)
            },
            onItemClick = { medication ->
                medication.visitId?.takeIf { it.isNotEmpty() }?.let { visitId ->
                    mainNavigator.navigateToMedicalHistoryDetail(visitId)
                } ?: run {
                    Toast.makeText(context, "No medical visit associated with this medication", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.rvCurrentMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = currentMedicationAdapter
        }
        binding.rvPastMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pastMedicationAdapter
        }
    }

    private fun showEditMedicationDialog(medication: Medication) {
        val dialog = AddMedicationDialogFragment.newInstance(
            medication = medication,
            sourceFragment = AddMedicationDialogFragment.SOURCE_PILL_FRAGMENT
        )
        dialog.show(parentFragmentManager, "EditMedicationDialog")
    }

    private fun showDeleteConfirmationDialog(medication: Medication) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Medication")
            .setMessage("Are you sure you want to delete ${medication.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteMedication(medication.medicationId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupClickListeners() {
        binding.fabAddMedication.setOnClickListener {
            Timber.d("FAB clicked: Showing AddMedicationDialogFragment")
            try {
                // Use the new newInstance method with source fragment information
                val dialog = AddMedicationDialogFragment.newInstance(
                    sourceFragment = AddMedicationDialogFragment.SOURCE_PILL_FRAGMENT
                )
                dialog.show(parentFragmentManager, "AddMedicationDialog")
            } catch (e: Exception) {
                Timber.e(e, "Failed to show AddMedicationDialogFragment")
                Toast.makeText(context, "Failed to open Add Medication: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupFragmentResultListener() {
        // Listen to the specific result key for PillFragment
        setFragmentResultListener(AddMedicationDialogFragment.RESULT_KEY_PILL_FRAGMENT) { _, bundle ->
            if (bundle.getBoolean("medicationAdded", false)) {
                viewModel.loadMedications()
                Toast.makeText(context, "Medication added successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Also listen to the default key for backward compatibility
        setFragmentResultListener(AddMedicationDialogFragment.RESULT_KEY_DEFAULT) { _, bundle ->
            if (bundle.getBoolean("medicationAdded", false)) {
                // Check if this result was intended for this fragment
                val source = bundle.getString("sourceFragment")
                if (source == null || source == AddMedicationDialogFragment.SOURCE_PILL_FRAGMENT) {
                    viewModel.loadMedications()
                    Toast.makeText(context, "Medication added successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeMedications() {
        viewModel.currentMedications.observe(viewLifecycleOwner) { medications ->
            currentMedicationAdapter.submitList(medications)
        }
        viewModel.pastMedications.observe(viewLifecycleOwner) { medications ->
            pastMedicationAdapter.submitList(medications)
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMedications()
    }
}