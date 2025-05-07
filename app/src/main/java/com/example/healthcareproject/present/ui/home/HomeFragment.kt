package com.example.healthcareproject.present.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulate data from a database
        val heartRate = "84bpm"
        val oxygenLevel = "99"
        val ecgStatus = "GOOD"
        val weight = "65kg"

        // Update UI with simulated data
        binding.tvHeartRateValue.text = heartRate
        binding.tvOxygenLevelValue.text = oxygenLevel
        binding.tvEcgValue.text = ecgStatus
        binding.tvWeightValue.text = weight

        binding.tvHeartRateCurrent.text = getString(R.string.default_heart_rate_current, heartRate)
        binding.tvOxygenLevelCurrent.text = getString(R.string.default_oxygen_level_current, oxygenLevel)
        binding.tvEcgCurrent.text = getString(R.string.default_ecg_current, ecgStatus)
        binding.tvWeightCurrent.text = getString(R.string.default_weight_current, weight)

        // Set up click listeners for navigation
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
            findNavController().navigate(R.id.action_homeFragment_to_weightFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}