package com.example.healthcareproject.present.ui.setting.emergency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemEmergencyContactBinding
import com.example.healthcareproject.domain.model.EmergencyInfo

class EmergencyInfoAdapter(
    private val onEditClick: (EmergencyInfo) -> Unit,
    private val onDeleteClick: (EmergencyInfo) -> Unit
) : ListAdapter<EmergencyInfo, EmergencyInfoAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemEmergencyContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
    }

    inner class ContactViewHolder(
        private val binding: ItemEmergencyContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: EmergencyInfo) {
            // Better approach: Pass the contact directly to the layout
            binding.contact = contact

            // Set click listeners
            binding.btnEdit.setOnClickListener { onEditClick(contact) }
            binding.btnDelete.setOnClickListener { onDeleteClick(contact) }

            // Set lifecycle owner for LiveData observation
            binding.lifecycleOwner = binding.root.context as? LifecycleOwner
            binding.executePendingBindings()
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<EmergencyInfo>() {
        override fun areItemsTheSame(oldItem: EmergencyInfo, newItem: EmergencyInfo): Boolean {
            return oldItem.emergencyId == newItem.emergencyId
        }

        override fun areContentsTheSame(oldItem: EmergencyInfo, newItem: EmergencyInfo): Boolean {
            return oldItem == newItem
        }
    }
}