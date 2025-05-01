package com.example.healthcareproject.present.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentMedicalHistoryDetailBinding
import com.example.healthcareproject.databinding.ItemMedicationBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MedicalHistoryDetailFragment : Fragment() {
    private var _binding: FragmentMedicalHistoryDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicalHistoryDetailViewModel by viewModels()

    @Inject lateinit var mainNavigator: MainNavigator

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicalHistoryDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup back button
        binding.ivBack.setOnClickListener {
            mainNavigator.navigateBackToMedicineFromMedicalHistoryDetail()
        }

        // Setup RecyclerView
        val adapter = MedicationAdapter(dateFormatter)
        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        // Collect UI state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.medications)
                state.error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Load data
        val visitId = arguments?.getString("visitId")
        visitId?.let { viewModel.loadDetails(it) } ?: run {
            Toast.makeText(context, "Invalid visit ID", Toast.LENGTH_SHORT).show()
            mainNavigator.navigateBackToMedicineFromMedicalHistoryDetail()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// RecyclerView Adapter for Medications
class MedicationAdapter(
    private val dateFormatter: DateTimeFormatter
) : androidx.recyclerview.widget.ListAdapter<Medication, MedicationAdapter.MedicationViewHolder>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Medication>() {
        override fun areItemsTheSame(oldItem: Medication, newItem: Medication): Boolean =
            oldItem.medicationId == newItem.medicationId
        override fun areContentsTheSame(oldItem: Medication, newItem: Medication): Boolean =
            oldItem == newItem
    }
) {
    class MedicationViewHolder(val binding: ItemMedicationBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = getItem(position)
        holder.binding.medication = medication
        holder.binding.dateFormatter = dateFormatter
        holder.binding.executePendingBindings()
    }
}