package com.example.healthcareproject.present.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentAlarmBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlarmViewModel by viewModels()
    private lateinit var alarmAdapter: AlarmAdapter
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load data using ViewModel
        viewModel.loadAlarms(requireContext())
        viewModel.loadMedications(requireContext())

        // Set up RecyclerView for alarms
        alarmAdapter = AlarmAdapter()
        binding.rvAlarms.layoutManager = LinearLayoutManager(context)
        binding.rvAlarms.adapter = alarmAdapter

        // Observe alarms LiveData
        viewModel.alarms.observe(viewLifecycleOwner) { alarms ->
            alarmAdapter.submitList(alarms)
        }

        // Set up back icon navigation
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_back_alarmFragment_to_homeFragment)
        }

        // Set up FAB to show/hide add alarm form
        binding.fabAddAlarm.setOnClickListener {
            binding.llAddAlarmContainer.visibility = View.VISIBLE
            binding.fabAddAlarm.visibility = View.GONE
        }

        // Set up medication multi-selection
        viewModel.medications.observe(viewLifecycleOwner) { medications ->
            val medicationNames = medications.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, medicationNames)
            binding.multiSelectMedications.setAdapter(adapter)
            binding.multiSelectMedications.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        }

        // Repeat pattern options
        val repeatOptions = listOf("Daily", "Weekly", "Once")
        val repeatAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, repeatOptions)
        binding.spinnerRepeatPattern.setAdapter(repeatAdapter)

        // Set up time selection
        binding.tvSelectedTime.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(selectedHour)
                .setMinute(selectedMinute)
                .setTitleText("Select Alarm Time")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                selectedHour = timePicker.hour
                selectedMinute = timePicker.minute
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)
                }
                val sdf = SimpleDateFormat("HH:mm")
                binding.tvSelectedTime.text = sdf.format(calendar.time)
            }

            timePicker.show(childFragmentManager, "TIME_PICKER")
        }

        // Set up save button
        binding.btnSaveAlarm.setOnClickListener {
            val selectedMedications = binding.multiSelectMedications.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val repeatPattern = binding.spinnerRepeatPattern.selectedItem?.toString() ?: ""

            if (selectedMedications.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one medication", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.tvSelectedTime.text == "Select Time") {
                Toast.makeText(requireContext(), "Please select a time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Format the selected time
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }
            val sdf = SimpleDateFormat("HH:mm")
            val alertTime = sdf.format(calendar.time)

            // Create and save the alarm using ViewModel
            val newAlarm = Alarm(
                medications = selectedMedications,
                time = alertTime,
                repeatPattern = repeatPattern
            )
            viewModel.addAlarm(newAlarm, requireContext())

            // Hide the form and show FAB
            binding.llAddAlarmContainer.visibility = View.GONE
            binding.fabAddAlarm.visibility = View.VISIBLE

            Toast.makeText(requireContext(), "Alarm set for ${selectedMedications.joinToString()} at $alertTime", Toast.LENGTH_LONG).show()
        }

        // Set up cancel button
        binding.btnCancel.setOnClickListener {
            binding.llAddAlarmContainer.visibility = View.GONE
            binding.fabAddAlarm.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}