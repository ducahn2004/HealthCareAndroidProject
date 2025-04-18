package com.example.healthcareproject.present.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R

class EmergencyContactAdapter(
    private val onEditClick: (EmergencyContact) -> Unit,
    private val onDeleteClick: (EmergencyContact) -> Unit
) : ListAdapter<EmergencyContact, EmergencyContactAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emergency_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvPhoneNumber: TextView = itemView.findViewById(R.id.tv_phone_number)
        private val tvRelationship: TextView = itemView.findViewById(R.id.tv_relationship)
        private val tvPriority: TextView = itemView.findViewById(R.id.tv_priority)
        private val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: Button = itemView.findViewById(R.id.btn_delete)

        fun bind(contact: EmergencyContact) {
            tvName.text = contact.name
            tvPhoneNumber.text = contact.phoneNumber
            tvRelationship.text = contact.relationship
            tvPriority.text = "Priority: ${contact.priority}"
            btnEdit.setOnClickListener { onEditClick(contact) }
            btnDelete.setOnClickListener { onDeleteClick(contact) }
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<EmergencyContact>() {
        override fun areItemsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
            return oldItem == newItem
        }
    }
}