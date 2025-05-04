package com.example.healthcareproject.present.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listeners
        binding.cvHeartRate.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_heartRateFragment)
        }
        binding.cvOxygenLevel.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_oxygenFragment)
        }
        binding.cvEcg.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_ecgFragment)
        }
        binding.cvWeight.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_alarmFragment)
        }
        binding.btnRefresh.setOnClickListener {
            viewModel.refreshMeasurements()
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.llSummary.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.cvHeartRate.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.cvOxygenLevel.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.cvEcg.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.cvWeight.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.btnRefresh.isEnabled = !isLoading
        })

        // Observe heart rate
        viewModel.heartRate.observe(viewLifecycleOwner, Observer { heartRate ->
            binding.tvHeartRateValue.text = heartRate
            binding.tvHeartRateCurrent.text = getString(R.string.default_heart_rate_current, heartRate)
        })

        // Observe oxygen level
        viewModel.oxygenLevel.observe(viewLifecycleOwner, Observer { oxygenLevel ->
            binding.tvOxygenLevelValue.text = oxygenLevel
            binding.tvOxygenLevelCurrent.text = getString(R.string.default_oxygen_level_current, oxygenLevel)
        })

        // Observe ECG status
        viewModel.ecgStatus.observe(viewLifecycleOwner, Observer { ecgStatus ->
            binding.tvEcgValue.text = ecgStatus
            binding.tvEcgCurrent.text = getString(R.string.default_ecg_current, ecgStatus)
        })

        // Observe weight
        viewModel.weight.observe(viewLifecycleOwner, Observer { weight ->
            binding.tvWeightCurrent.text = getString(R.string.default_weight_current, weight)
        })

        // Observe heart rate alert
        viewModel.heartRateAlert.observe(viewLifecycleOwner, Observer { alert ->
            binding.tvHeartRateAlert.text = alert ?: ""
            binding.tvHeartRateAlert.visibility = if (alert != null) View.VISIBLE else View.GONE
        })

        // Observe oxygen level alert
        viewModel.oxygenLevelAlert.observe(viewLifecycleOwner, Observer { alert ->
            binding.tvOxygenLevelAlert.text = alert ?: ""
            binding.tvOxygenLevelAlert.visibility = if (alert != null) View.VISIBLE else View.GONE
        })

        // Observe error
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}