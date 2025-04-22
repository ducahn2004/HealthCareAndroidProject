package com.example.healthcareproject.present.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthcareproject.databinding.FragmentAlarmBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
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

        // Sample medication list (in a real app, this would come from the Medications table)
        val medications = listOf("Aspirin", "Ibuprofen", "Paracetamol")
        val medicationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, medications)
        binding.spinnerMedication.setAdapter(medicationAdapter)

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
            val selectedMedication = binding.spinnerMedication.selectedItem?.toString() ?: ""
            val repeatPattern = binding.spinnerRepeatPattern.selectedItem?.toString() ?: ""

            if (selectedMedication.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a medication", Toast.LENGTH_SHORT).show()
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

            // Simulate saving the alarm (in a real app, save to the Alerts table)
            val alertMessage = "Take $selectedMedication at $alertTime ($repeatPattern)"
            Toast.makeText(requireContext(), "Alarm set: $alertMessage", Toast.LENGTH_LONG).show()

            // You can add logic here to save to the database using the Alerts table schema:
            // - alert_id (auto-generated)
            // - user_id (from current user)
            // - title (e.g., "Medication Reminder: $selectedMedication")
            // - message (e.g., alertMessage)
            // - alert_time (calendar.time)
            // - repeat_pattern (e.g., repeatPattern)
            // - status (e.g., true for active)
            // - created_at (current timestamp)
        }

        // Set up cancel button
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}