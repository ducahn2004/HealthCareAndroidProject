package com.example.healthcareproject.presentation.ui.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentMedicalVisitsBinding
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.presentation.navigation.MainNavigator
import com.example.healthcareproject.presentation.viewmodel.medicine.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MedicalVisitsFragment : Fragment() {
    private lateinit var binding: FragmentMedicalVisitsBinding
    private val viewModel: MedicineViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var medicalVisitAdapter: MedicalVisitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("MedicalVisitsFragment onCreateView")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_medical_visits,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("MedicalVisitsFragment onViewCreated")
        viewModel.loadMedicalVisits()
        setupRecyclerView()
        setupFab()
        observeMedicalVisits()
    }

    private fun setupRecyclerView() {
        Timber.d("Setting up medical visits RecyclerView")
        medicalVisitAdapter = MedicalVisitAdapter { visit ->
            Timber.d("Navigating with visitId: ${visit.visitId}")
            mainNavigator.navigateMedicineToMedicalHistoryDetail(visit.visitId)
        }
        binding.rvMedicalVisits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = medicalVisitAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddMedicalVisit.setOnClickListener {
            Timber.d("FAB Add Medical Visit clicked")
            viewModel.navigateToAddMedicalVisit()
        }
    }

    private fun observeMedicalVisits() {
        viewModel.medicalVisits.observe(viewLifecycleOwner) { visits ->
            Timber.d("MedicalVisitsFragment: Received ${visits?.size} visits")
            medicalVisitAdapter.submitList(visits)
            binding.tvNoMedicalVisits.visibility = if (visits.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    fun getCurrentMedicalVisits(): List<MedicalVisit> = medicalVisitAdapter.currentList

    fun updateMedicalVisits(visits: List<MedicalVisit>) {
        Timber.d("Updating medical visits: ${visits.size} items")
        medicalVisitAdapter.submitList(visits)
        binding.tvNoMedicalVisits.visibility = if (visits.isEmpty()) View.VISIBLE else View.GONE
    }
}