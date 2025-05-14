package com.example.healthcareproject.present.ui.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentAppointmentsBinding
import com.example.healthcareproject.domain.model.Appointment
import com.example.healthcareproject.present.navigation.MainNavigator
import com.example.healthcareproject.present.viewmodel.medicine.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppointmentsFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicineViewModel by viewModels({ requireParentFragment() })

    private lateinit var appointmentAdapter: AppointmentAdapter

    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointments, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeAppointments()
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter()
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddAppointment.setOnClickListener {
            viewModel.navigateToAddAppointment()
        }
    }

    private fun observeAppointments() {
        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            Timber.d("AppointmentsFragment: Received ${appointments.size} appointments")
            appointmentAdapter.submitList(appointments)
            binding.tvNoAppointments.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    fun getCurrentAppointments(): List<Appointment> = appointmentAdapter.currentList

    fun updateAppointments(appointments: List<Appointment>) {
        appointmentAdapter.submitList(appointments)
        binding.tvNoAppointments.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}