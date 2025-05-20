package com.example.healthcareproject.presentation.ui.medications

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentCurrentMedicationsBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.presentation.navigation.MainNavigator
import com.example.healthcareproject.presentation.viewmodel.medication.PillViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CurrentMedicationsFragment : Fragment() {
    private lateinit var binding: FragmentCurrentMedicationsBinding
    private val viewModel: PillViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var medicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("CurrentMedicationsFragment onCreateView")
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
        Timber.d("PastMedicationsFragment onViewCreated")
        setupRecyclerView()
        observeMedications()
        setupFragmentResultListener()
        observeSearchEvents()
    }

    private fun setupRecyclerView() {
        Timber.d("Setting up current medications RecyclerView")
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
            },
            isHistoryView = false
        )
        binding.rvCurrentMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = medicationAdapter
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
        AlertDialog.Builder(requireContext())
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
        viewModel.currentMedications.observe(viewLifecycleOwner) { medications ->
            Timber.d("CurrentMedicationsFragment: Received ${medications?.size ?: 0} medications: ${medications?.map { it.name }?.joinToString()}")
            medicationAdapter.submitList(medications?.toList()) {
                Timber.d("CurrentMedicationsFragment: Adapter updated with ${medicationAdapter.itemCount} items")
                binding.tvNoCurrentMedications.visibility = if (medications.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun observeSearchEvents() {
        // Observe search events from ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchEvent.collectLatest { query ->
                Timber.d("Fragment received search event: $query")
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(AddMedicationDialogFragment.RESULT_KEY_PILL_FRAGMENT) { _, bundle ->
            Timber.d("Received medication result")
            val medication = bundle.getParcelable<Medication>("medication")
            medication?.let {
                Timber.d("Medication updated/added: ${it.name}, id=${it.medicationId}")
                // Check if medication belongs to past tab
                val today = LocalDate.now()
                val endDate = it.endDate ?: LocalDate.now().plusMonths(1)
                val isCurrent = today in it.startDate..endDate
                if (!isCurrent) {
                    // If it's a past medication, ensure PastMedicationsFragment refreshes
                    parentFragmentManager.fragments
                        .filterIsInstance<PastMedicationsFragment>()
                        .firstOrNull()
                        ?.let { pastFragment ->
                            Timber.d("Triggering loadMedications for PastMedicationsFragment")
                            pastFragment.triggerMedicationRefresh()
                        }
                }
                // Refresh current medications
                viewModel.loadMedications()
            }
        }
    }

    // Allow PastMedicationsFragment to trigger a refresh
    fun triggerMedicationRefresh() {
        Timber.d("Triggering loadMedications for CurrentMedicationsFragment")
        viewModel.loadMedications()
    }
}