package com.example.healthcareproject.present.ui.medication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentPastMedicationsBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.navigation.MainNavigator
import com.example.healthcareproject.present.viewmodel.medication.PillViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PastMedicationsFragment : Fragment() {
    private lateinit var binding: FragmentPastMedicationsBinding
    private val viewModel: PillViewModel by activityViewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var medicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("PastMedicationsFragment onCreateView")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_past_medications,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("PastMedicationsFragment onViewCreated")
        setupRecyclerView()
        setupFab()
        observeMedications()
        setupFragmentResultListener()
    }

    private fun setupRecyclerView() {
        Timber.d("Setting up past medications RecyclerView")
        medicationAdapter = MedicationAdapter(
            onEdit = { medication ->
                Timber.d("Editing medication: ${medication.name}")
                showEditMedicationDialog(medication)
            },
            onDelete = { medication ->
                Timber.d("Deleting medication: ${medication.name}")
                showDeleteConfirmationDialog(medication)
            },
            onItemClick = { medication ->
                medication.visitId?.takeIf { it.isNotEmpty() }?.let { visitId ->
                    Timber.d("Navigating to medical history detail: $visitId")
                    mainNavigator.navigatePillFragmentToMedicalHistoryDetail(visitId)
                }
            }
        )

        binding.rvPastMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = medicationAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddMedication.setOnClickListener {
            Timber.d("FAB Add Medication clicked")
            showAddMedicationDialog()
        }
    }

    private fun showAddMedicationDialog() {
        val dialog = AddMedicationDialogFragment.newInstance(
            medication = null,
            sourceFragment = AddMedicationDialogFragment.SOURCE_PILL_FRAGMENT
        )
        dialog.show(parentFragmentManager, "AddMedicationDialog")
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
                Timber.d("Confirmed deletion of medication: ${medication.name}")
                viewModel.deleteMedication(medication.medicationId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeMedications() {
        viewModel.pastMedications.observe(viewLifecycleOwner) { medications ->
            Timber.d("Past medications received: size=${medications?.size}")
            medicationAdapter.submitList(medications)
            binding.tvNoPastMedications.visibility = if (medications.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("medicationResult") { _, bundle ->
            Timber.d("Received medication result")
            val medication = bundle.getParcelable<Medication>("medication")
            medication?.let {
                Timber.d("Medication updated/added: ${it.name}")
                viewModel.loadMedications()
            }
        }
    }
}