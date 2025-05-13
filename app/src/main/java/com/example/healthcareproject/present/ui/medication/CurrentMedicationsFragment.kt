package com.example.healthcareproject.present.ui.medication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentCurrentMedicationsBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.navigation.MainNavigator
import com.example.healthcareproject.present.viewmodel.medication.PillViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CurrentMedicationsFragment : Fragment() {
    private lateinit var binding: FragmentCurrentMedicationsBinding
    private val viewModel: PillViewModel by activityViewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var medicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_current_medications,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeMedications()
    }

    private fun setupRecyclerView() {
        medicationAdapter = MedicationAdapter(
            onEdit = { medication ->
                showEditMedicationDialog(medication)
            },
            onDelete = { medication ->
                showDeleteConfirmationDialog(medication)
            },
            onItemClick = { medication ->
                medication.visitId?.takeIf { it.isNotEmpty() }?.let { visitId ->
                    mainNavigator.navigatePillFragmentToMedicalHistoryDetail(visitId)
                }
            }
        )

        binding.rvCurrentMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = medicationAdapter
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
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Medication")
            .setMessage("Are you sure you want to delete ${medication.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteMedication(medication.medicationId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeMedications() {
        viewModel.currentMedications.observe(viewLifecycleOwner) { medications ->
            medicationAdapter.submitList(medications)
        }
    }
}