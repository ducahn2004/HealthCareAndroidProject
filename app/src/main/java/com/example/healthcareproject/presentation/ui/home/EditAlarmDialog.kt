package com.example.healthcareproject.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthcareproject.databinding.DialogEditAlarmBinding
import com.example.healthcareproject.domain.model.Reminder
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.presentation.viewmodel.home.ReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class EditAlarmDialog : DialogFragment() {

    private var _binding: DialogEditAlarmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReminderViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, // Set width to match parent
            ViewGroup.LayoutParams.WRAP_CONTENT // Keep height as wrap content
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reminder = arguments?.getParcelable("reminder") as? Reminder
        reminder?.let { setupUI(it) }

        binding.btnSave.setOnClickListener { saveReminder() }
    }

    private fun setupUI(reminder: Reminder) {
        binding.etTitle.setText(reminder.title)
        binding.etMessage.setText(reminder.message)
        binding.timePicker.hour = reminder.reminderTime.hour
        binding.timePicker.minute = reminder.reminderTime.minute
        binding.spinnerRepeat.setSelection(RepeatPattern.values().indexOf(reminder.repeatPattern))
    }

    private fun saveReminder() {
        val title = binding.etTitle.text.toString()
        val message = binding.etMessage.text.toString()
        val time = LocalTime.of(binding.timePicker.hour, binding.timePicker.minute)
        val repeatPattern = RepeatPattern.entries[binding.spinnerRepeat.selectedItemPosition]

        val reminder = arguments?.getParcelable("reminder") as? Reminder
        reminder?.let {
            viewModel.updateReminder(
                reminderId = it.reminderId,
                title = title,
                message = message,
                reminderTime = time,
                repeatPattern = repeatPattern,
                startDate = it.startDate,
                endDate = it.endDate,
                status = it.status // Keep the current status
            )
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(reminder: Reminder): EditAlarmDialog {
            return EditAlarmDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("reminder", reminder)
                }
            }
        }
    }
}