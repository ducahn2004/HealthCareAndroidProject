package com.example.healthcareproject.present.ui.home.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemAlarmBinding
import com.example.healthcareproject.domain.model.Alert
import java.time.format.DateTimeFormatter

class AlarmAdapter(
    private val onEditClick: (Alert) -> Unit,
    private val onStatusChange: (String, Boolean) -> Unit
) : ListAdapter<Alert, AlarmAdapter.AlarmViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert)
    }

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: Alert) {
            binding.tvTitle.text = alert.title
            binding.tvTime.text = alert.alertTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            binding.tvRepeat.text = alert.repeatPattern.name
            binding.switchStatus.isChecked = alert.status

            // Click vào item để mở EditAlarmDialog
            binding.root.setOnClickListener {
                onEditClick(alert)
            }

            // Xử lý thay đổi trạng thái qua Switch
            binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
                onStatusChange(alert.alertId, isChecked)
            }
        }
    }
}

class AlertDiffCallback : DiffUtil.ItemCallback<Alert>() {
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.alertId == newItem.alertId
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }
}