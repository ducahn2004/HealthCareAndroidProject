package com.example.healthcareproject.present.pill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentPillBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PillFragment : Fragment() {
    private var _binding: FragmentPillBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PillViewModel by viewModels()
    @Inject lateinit var mainNavigator: MainNavigator

    private lateinit var currentMedicationAdapter: MedicationAdapter
    private lateinit var pastMedicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupClickListeners()
        observeUiState()
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

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Update UI state
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                // Handle empty states
                binding.tvNoCurrentMedications.visibility =
                    if (state.currentMedications.isEmpty() && !state.isLoading) View.VISIBLE else View.GONE
                binding.tvNoPastMedications.visibility =
                    if (state.pastMedications.isEmpty() && !state.isLoading) View.VISIBLE else View.GONE

                // Update adapters
                currentMedicationAdapter.submitList(state.currentMedications)
                pastMedicationAdapter.submitList(state.pastMedications)

                // Show errors if any
                state.error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("medicationKey") { _, bundle ->
            // Get the returned medication from AddMedicationFragment
            val medication = bundle.getParcelable<Medication>("medication")
            medication?.let {
                viewModel.addMedication(it)
            }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}