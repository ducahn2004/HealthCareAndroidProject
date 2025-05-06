package com.example.healthcareproject.present.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentMedicalHistoryDetailBinding
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
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
        _binding = FragmentMedicalHistoryDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@MedicalHistoryDetailFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
        loadVisitDetails()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            mainNavigator.navigateBackToMedicineFromMedicalHistoryDetail()
        }

        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MedicationAdapter(dateFormatter)
        }
    }

    private fun observeViewModel() {
        viewModel.medications.observe(viewLifecycleOwner) { medications ->
            (binding.rvMedications.adapter as MedicationAdapter).submitList(medications)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun loadVisitDetails() {
        val visitId = arguments?.getString("visitId")
        if (visitId != null) {
            viewModel.loadDetails(visitId)
        } else {
            Toast.makeText(context, "Invalid visit ID", Toast.LENGTH_SHORT).show()
            mainNavigator.navigateBackToMedicineFromMedicalHistoryDetail()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

