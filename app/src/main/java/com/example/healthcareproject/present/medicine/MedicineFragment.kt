package com.example.healthcareproject.present.medicine

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
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MedicineFragment : Fragment() {
    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicineViewModel by viewModels()
    @Inject lateinit var mainNavigator: MainNavigator

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
        observeUiState()
        setupFragmentResultListener()
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

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            adapterBefore.submitList(state.visitsBefore)
            adapterAfter.submitList(state.visitsAfter)
            state.error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("requestKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            newVisit?.let {
                viewModel.loadMedicalVisits() // Refresh data
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}