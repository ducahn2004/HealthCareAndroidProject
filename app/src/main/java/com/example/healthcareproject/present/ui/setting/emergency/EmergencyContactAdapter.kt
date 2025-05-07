package com.example.healthcareproject.present.ui.setting.emergency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemEmergencyContactBinding
import com.example.healthcareproject.domain.model.EmergencyInfo

class EmergencyContactAdapter(
    private val onEditClick: (EmergencyInfo) -> Unit,
    private val onDeleteClick: (EmergencyInfo) -> Unit
) : ListAdapter<EmergencyInfo, EmergencyContactAdapter.ViewHolder>(EmergencyInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmergencyContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEmergencyContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: EmergencyInfo) {
            binding.contact = contact
            binding.onEditClick = View.OnClickListener { onEditClick(contact) }
            binding.onDeleteClick = View.OnClickListener { onDeleteClick(contact) }
            binding.executePendingBindings()
        }
    }

    class EmergencyInfoDiffCallback : DiffUtil.ItemCallback<EmergencyInfo>() {
        override fun areItemsTheSame(oldItem: EmergencyInfo, newItem: EmergencyInfo): Boolean {
            return oldItem.emergencyId == newItem.emergencyId
        }

        override fun areContentsTheSame(oldItem: EmergencyInfo, newItem: EmergencyInfo): Boolean {
            return oldItem == newItem
        }
    }
}