package com.example.healthcareproject.presentation.ui.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.healthcareproject.databinding.FragmentMedicineBinding
import com.example.healthcareproject.domain.model.Appointment
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.presentation.navigation.MainNavigator
import com.example.healthcareproject.presentation.viewmodel.medicine.MedicineViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MedicineFragment : Fragment() {
    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicineViewModel by viewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("MedicineFragment onCreateView")
        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("MedicineFragment onViewCreated")
        setupViewPager()
        setupSearch()
        setupObservers()
        setupFragmentResultListener()
        if (savedInstanceState == null) {
            viewModel.loadMedicalVisits()
        }
    }

    private fun setupViewPager() {
        Timber.d("Setting up ViewPager")
        val pagerAdapter = MedicinePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Upcoming Visits"
                1 -> "Past Visits"
                else -> null
            }
        }.attach()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            Timber.d("Search input: $text")
            viewModel.onSearchQueryChanged(text.toString())
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Timber.d("Loading state: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Timber.e("Error: $it")
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.error.value = null
            }
        }

        viewModel.navigateToAddAppointmentEvent.observe(viewLifecycleOwner) {
            Timber.d("Navigating to add appointment")
            mainNavigator.navigateToAddAppointment()
        }

        viewModel.navigateToAddMedicalVisitEvent.observe(viewLifecycleOwner) {
            Timber.d("Navigating to add medical visit")
            mainNavigator.navigateToAddMedicalVisit()
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("requestKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            val newAppointment = bundle.getParcelable<Appointment>("newAppointment")
            if (newVisit != null) {
                Timber.d("New visit added: ${newVisit.diagnosis}")
                parentFragmentManager.fragments
                    .filterIsInstance<MedicalVisitsFragment>()
                    .firstOrNull()
                    ?.let { fragment ->
                        val currentList = fragment.getCurrentMedicalVisits().toMutableList()
                        currentList.add(newVisit)
                        fragment.updateMedicalVisits(currentList.sortedByDescending { it.visitDate })
                    }
            }
            if (newAppointment != null) {
                Timber.d("New appointment added: ${newAppointment.doctorName}")
                parentFragmentManager.fragments
                    .filterIsInstance<AppointmentsFragment>()
                    .firstOrNull()
                    ?.let { fragment ->
                        val currentList = fragment.getCurrentAppointments().toMutableList()
                        currentList.add(newAppointment)
                        fragment.updateAppointments(currentList.sortedBy { it.appointmentTime })
                    }
            }
            Timber.d("Updated UI without reloading from database")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("MedicineFragment onDestroyView")
        _binding = null
    }
}