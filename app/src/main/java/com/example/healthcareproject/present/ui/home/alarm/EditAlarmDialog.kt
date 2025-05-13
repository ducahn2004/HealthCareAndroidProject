package com.example.healthcareproject.present.ui.home.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthcareproject.databinding.DialogEditAlarmBinding
import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.present.viewmodel.home.alarm.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class EditAlarmDialog : DialogFragment() {

    private var _binding: DialogEditAlarmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlarmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alert = arguments?.getParcelable("alert") as? Alert
        alert?.let { setupUI(it) }

        binding.btnSave.setOnClickListener { saveAlert() }
    }

    private fun setupUI(alert: Alert) {
        binding.etTitle.setText(alert.title)
        binding.etMessage.setText(alert.message)
        binding.timePicker.hour = alert.alertTime.hour
        binding.timePicker.minute = alert.alertTime.minute
        binding.spinnerRepeat.setSelection(RepeatPattern.values().indexOf(alert.repeatPattern))
    }

    private fun saveAlert() {
        val title = binding.etTitle.text.toString()
        val message = binding.etMessage.text.toString()
        val time = LocalTime.of(binding.timePicker.hour, binding.timePicker.minute)
        val repeatPattern = RepeatPattern.values()[binding.spinnerRepeat.selectedItemPosition]

        val alert = arguments?.getParcelable("alert") as? Alert
        alert?.let {
            viewModel.updateAlert(
                alertId = it.alertId,
                title = title,
                message = message,
                alertTime = time,
                repeatPattern = repeatPattern,
                status = it.status // Giữ nguyên trạng thái hiện tại
            )
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(alert: Alert): EditAlarmDialog {
            return EditAlarmDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("alert", alert)
                }
            }
        }
    }
}