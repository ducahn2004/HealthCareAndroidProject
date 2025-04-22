package com.example.healthcareproject.present.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R

class AlarmAdapter : ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm)
    }

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMedications: TextView = itemView.findViewById(R.id.tv_medications)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val tvRepeat: TextView = itemView.findViewById(R.id.tv_repeat)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_status)

        fun bind(alarm: Alarm) {
            tvMedications.text = alarm.medications.joinToString(", ")
            tvTime.text = alarm.time
            tvRepeat.text = alarm.repeatPattern
            tvStatus.text = if (alarm.isActive) "Active" else "Inactive"
        }
    }

    class AlarmDiffCallback : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem == newItem
        }
    }
}