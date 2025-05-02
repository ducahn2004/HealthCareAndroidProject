package com.example.healthcareproject.present.pill

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
        // Use DataBindingUtil for inflation to properly set up data binding
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pill,
            container,
            false
        )

        // Set up data binding variables
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
        currentMedicationAdapter = MedicationAdapter { medication ->
            medication.visitId?.let { visitId ->
                mainNavigator.navigateToMedicalHistoryDetail(visitId)
            }
        }
        pastMedicationAdapter = MedicationAdapter { medication ->
            medication.visitId?.let { visitId ->
                mainNavigator.navigateToMedicalHistoryDetail(visitId)
            }
        }

        binding.rvCurrentMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = currentMedicationAdapter
        }
        binding.rvPastMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pastMedicationAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddMedication.setOnClickListener {
            mainNavigator.navigateToAddMedication()
        }
    }

    private fun observeMedications() {
        // Observe current medications
        viewModel.currentMedications.observe(viewLifecycleOwner) { medications ->
            currentMedicationAdapter.submitList(medications)
        }

        // Observe past medications
        viewModel.pastMedications.observe(viewLifecycleOwner) { medications ->
            pastMedicationAdapter.submitList(medications)
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("medicationKey") { _, bundle ->
            // Refresh the medication list
            viewModel.loadMedications()

            // Optionally navigate to the medical visit detail if requested
            val visitId = bundle.getString("visitId")
            if (visitId != null && bundle.getBoolean("navigateToVisit", false)) {
                mainNavigator.navigateToMedicalHistoryDetail(visitId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh medications when returning to this fragment
        viewModel.loadMedications()
    }
}