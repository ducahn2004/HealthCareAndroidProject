package com.example.healthcareproject.presentation.ui.medicine

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentMedicalHistoryDetailBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.presentation.navigation.MainNavigator
import com.example.healthcareproject.presentation.ui.medications.MedicationAdapter
import com.example.healthcareproject.presentation.ui.medications.AddMedicationDialogFragment
import com.example.healthcareproject.presentation.viewmodel.medicine.MedicalHistoryDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MedicalHistoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentMedicalHistoryDetailBinding
    private val viewModel: MedicalHistoryDetailViewModel by viewModels()
    private lateinit var medicationAdapter: MedicationAdapter

    @Inject
    lateinit var mainNavigator: MainNavigator // Inject MainNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_medical_history_detail,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val visitId = arguments?.getString("visitId")
        val sourceFragment = arguments?.getString("sourceFragment")

        if (visitId == null) {
            binding.tvError.text = "No visit ID provided"
            binding.tvError.visibility = View.VISIBLE
            Timber.tag("MedicalHistoryDetail").e("No visitId received")
        } else {
            Timber.tag("MedicalHistoryDetail").d("Loading visitId: $visitId")
            viewModel.loadDetails(visitId)
        }

        // Handle back button click
        binding.ivBack.setOnClickListener {
            when (sourceFragment) {
                "PillFragment" -> mainNavigator.navigateBackPillFragmentFromMedicalHistoryDetail()
                "MedicineFragment" -> mainNavigator.navigateBackToMedicineFromMedicalHistoryDetail()
                else -> findNavController().navigateUp() // Fallback
            }
        }

        setupRecyclerView()
        setupFragmentResultListener()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        medicationAdapter = MedicationAdapter(
            onEdit = { medication ->
                showEditMedicationDialog(medication)
            },
            onDelete = { medication ->
                showDeleteConfirmationDialog(medication)
            },
            isHistoryView = false // Hiển thị icon edit/delete
        )
        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicationAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.medications.observe(viewLifecycleOwner) { medications ->
            Timber.tag("MedicalHistoryDetail").d("Medications: $medications")
            if (medications.isEmpty()) {
                binding.tvError.text = "No medications found for this visit"
                binding.tvError.visibility = View.VISIBLE
                binding.rvMedications.visibility = View.GONE
            } else {
                binding.tvError.visibility = View.GONE
                binding.rvMedications.visibility = View.VISIBLE
                medicationAdapter.submitList(medications)
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showEditMedicationDialog(medication: Medication) {
        val dialog = AddMedicationDialogFragment.Companion.newInstance(
            medication = medication,
            sourceFragment = AddMedicationDialogFragment.Companion.SOURCE_MEDICAL_HISTORY_DETAIL_FRAGMENT
        )
        dialog.show(parentFragmentManager, "EditMedicationDialog")
    }
    private fun setupFragmentResultListener() {
        setFragmentResultListener(AddMedicationDialogFragment.Companion.RESULT_KEY_DEFAULT) { _, bundle ->
            if (bundle.getBoolean("medicationAdded", false)) {
                val source = bundle.getString("sourceFragment")
                if (source == AddMedicationDialogFragment.Companion.SOURCE_MEDICAL_HISTORY_DETAIL_FRAGMENT) {
                    val updatedMedication = bundle.getParcelable<Medication>("updatedMedication")
                    if (updatedMedication != null) {
                        viewModel.updateMedication(updatedMedication)
                        Toast.makeText(context, "Medication updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        val visitId = arguments?.getString("visitId")
                        if (visitId != null) {
                            viewModel.loadDetails(visitId)
                            Toast.makeText(context, "Medication updated successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
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

}