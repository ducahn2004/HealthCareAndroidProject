package com.example.healthcareproject.present.ui.medicine

import android.os.Bundle
import android.util.Log
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

    private lateinit var medicalVisitAdapter: MedicalVisitAdapter
    private lateinit var appointmentAdapter: AppointmentAdapter

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
        viewModel.loadMedicalVisits()
    }

    private fun setupRecyclerViews() {
        medicalVisitAdapter = MedicalVisitAdapter { visit ->
            mainNavigator.navigateMedicineToMedicalHistoryDetail(visit.visitId)
        }
        appointmentAdapter = AppointmentAdapter { appointment ->
            mainNavigator.navigateMedicineToMedicalHistoryDetail(appointment.visitId ?: appointment.appointmentId)
        }

        binding.recyclerViewBefore.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = medicalVisitAdapter
        }

        binding.recyclerViewAfter.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.onSearchQueryChanged(text.toString())
        }
    }

    private fun setupObservers() {
        viewModel.medicalVisits.observe(viewLifecycleOwner) { visits ->
            medicalVisitAdapter.submitList(visits)
            binding.tvNoPastVisits.visibility = if (visits.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            appointmentAdapter.submitList(appointments)
            binding.tvNoFutureVisits.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Log.e("MedicineFragment", "Error: $it")
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.error.value = null
            }
        }

        viewModel.navigateToAddAppointmentEvent.observe(viewLifecycleOwner) {
            mainNavigator.navigateToAddAppointment()
        }

        viewModel.navigateToAddMedicalVisitEvent.observe(viewLifecycleOwner) {
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
        binding.btnAddFutureVisit.setOnClickListener {
            viewModel.navigateToAddAppointment()
        }

        binding.btnAddPastVisit.setOnClickListener {
            viewModel.navigateToAddMedicalVisit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}