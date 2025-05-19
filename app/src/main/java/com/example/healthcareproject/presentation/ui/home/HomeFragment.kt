package com.example.healthcareproject.presentation.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentHomeBinding
import com.example.healthcareproject.presentation.viewmodel.home.HomeViewModel
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

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeHeartRate()
        observeSpO2()

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("StringFormatInvalid")
    private fun observeHeartRate() {
        viewModel.heartRate.observe(viewLifecycleOwner) { heartRate ->
            val heartRateText = "${heartRate.toInt()} bpm"
            binding.tvHeartRateValue.text = heartRateText
            binding.tvHeartRateCurrent.text =
                getString(R.string.default_heart_rate_current, heartRateText)
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun observeSpO2() {
        viewModel.spO2.observe(viewLifecycleOwner) { spO2 ->
            val spO2Text = "${spO2.toInt()}%"
            binding.tvOxygenLevelValue.text = spO2Text
            binding.tvOxygenLevelCurrent.text =
                getString(R.string.default_oxygen_level_current, spO2Text)
        }
    }
}