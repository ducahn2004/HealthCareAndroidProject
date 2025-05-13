package com.example.healthcareproject.present.ui.medicine

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.example.healthcareproject.present.navigation.MainNavigator
import com.example.healthcareproject.present.ui.medication.AddMedicationDialogFragment
import com.example.healthcareproject.present.ui.medication.MedicationAdapter
import com.example.healthcareproject.present.viewmodel.medicine.MedicalHistoryDetailViewModel
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
            onEdit = { /* Editing not supported in history view */ },
            onDelete = { /* Deleting not supported in history view */ }
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
        val dialog = AddMedicationDialogFragment.newInstance(
            medication = medication,
            sourceFragment = AddMedicationDialogFragment.SOURCE_MEDICAL_HISTORY_DETAIL_FRAGMENT
        )
        dialog.show(parentFragmentManager, "EditMedicationDialog")
    }
    private fun setupFragmentResultListener() {
        setFragmentResultListener(AddMedicationDialogFragment.RESULT_KEY_DEFAULT) { _, bundle ->
            if (bundle.getBoolean("medicationAdded", false)) {
                val source = bundle.getString("sourceFragment")
                if (source == AddMedicationDialogFragment.SOURCE_MEDICAL_HISTORY_DETAIL_FRAGMENT) {
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