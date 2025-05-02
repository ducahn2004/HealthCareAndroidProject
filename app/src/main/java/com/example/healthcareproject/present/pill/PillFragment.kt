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
import androidx.navigation.fragment.findNavController
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

        // Set NavController for MainNavigator
        mainNavigator.setNavController(findNavController())

        setupRecyclerViews()
        setupClickListeners()
        observeMedications()
        setupFragmentResultListener()
    }

    private fun setupRecyclerViews() {
        currentMedicationAdapter = MedicationAdapter { medication ->
            medication.visitId?.let { visitId ->
                mainNavigator.navigateToMedicalHistoryDetail(visitId)
            } ?: Toast.makeText(context, "No visit ID available", Toast.LENGTH_SHORT).show()
        }
        pastMedicationAdapter = MedicationAdapter { medication ->
            medication.visitId?.let { visitId ->
                mainNavigator.navigateToMedicalHistoryDetail(visitId)
            } ?: Toast.makeText(context, "No visit ID available", Toast.LENGTH_SHORT).show()
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

    private fun setupFragmentResultListener() {
        setFragmentResultListener("medicationKey") { _, bundle ->
            viewModel.loadMedications()
            val visitId = bundle.getString("visitId")
            if (visitId != null && bundle.getBoolean("navigateToVisit", false)) {
                mainNavigator.navigateToMedicalHistoryDetail(visitId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMedications()
    }
}