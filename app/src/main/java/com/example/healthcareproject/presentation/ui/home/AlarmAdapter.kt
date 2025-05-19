package com.example.healthcareproject.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemAlarmBinding
import com.example.healthcareproject.domain.model.Reminder
import java.time.format.DateTimeFormatter

class AlarmAdapter(
    private val onEditClick: (Reminder) -> Unit,
    private val onStatusChange: (String, Boolean) -> Unit
) : ListAdapter<Reminder, AlarmAdapter.AlarmViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.bind(reminder)
    }

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.tvTitle.text = reminder.title
            binding.tvTime.text = reminder.reminderTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            binding.tvRepeat.text = reminder.repeatPattern.name
            binding.switchStatus.isChecked = reminder.status

            binding.root.setOnClickListener {
                onEditClick(reminder)
            }

            binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
                onStatusChange(reminder.reminderId, isChecked)
            }
        }
    }
}

class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.reminderId == newItem.reminderId
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}