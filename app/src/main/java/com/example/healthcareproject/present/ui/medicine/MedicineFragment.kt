package com.example.healthcareproject.present.ui.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentMedicineBinding
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MedicineFragment : Fragment() {
    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicineViewModel by viewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var adapterBefore: MedicalVisitAdapter
    private lateinit var adapterAfter: MedicalVisitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearch()
        setupObservers()
        setupFragmentResultListener()
        setupButtons()
    }

    private fun setupRecyclerViews() {
        adapterBefore = MedicalVisitAdapter { visit ->
            mainNavigator.navigateToMedicalHistoryDetail(visit.visitId)
        }
        adapterAfter = MedicalVisitAdapter { visit ->
            mainNavigator.navigateToMedicalHistoryDetail(visit.visitId)
        }

        binding.recyclerViewBefore.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterBefore
        }

        binding.recyclerViewAfter.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterAfter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.onSearchQueryChanged(text.toString())
        }
    }

    private fun setupObservers() {
        // Observe visitsBefore LiveData
        viewModel.visitsBefore.observe(viewLifecycleOwner) { visits ->
            adapterBefore.submitList(visits)
            binding.tvNoPastVisits.visibility = if (visits.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe visitsAfter LiveData
        viewModel.visitsAfter.observe(viewLifecycleOwner) { visits ->
            adapterAfter.submitList(visits)
            binding.tvNoFutureVisits.visibility = if (visits.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error LiveData
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.error.value = null // Reset after showing
            }
        }

        // Observe navigateToAddAppointment event
        viewModel.navigateToAddAppointmentEvent.observe(viewLifecycleOwner) { _ ->
            mainNavigator.navigateToAddAppointment()
        }

        // Observe navigateToAddMedicalVisit event (new)
        viewModel.navigateToAddMedicalVisitEvent.observe(viewLifecycleOwner) { _ ->
            mainNavigator.navigateToAddMedicalVisit()
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("requestKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            newVisit?.let {
                viewModel.loadMedicalVisits()
            }
        }
    }

    private fun setupButtons() {
        // Upcoming Visits Add Button
        binding.btnAddFutureVisit.setOnClickListener {
            viewModel.navigateToAddAppointment()
        }

        // Past Visits Add Button
        binding.btnAddPastVisit.setOnClickListener {
            viewModel.navigateToAddMedicalVisit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}