package com.example.healthcareproject.present.ui.medicine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemMedicationBinding
import com.example.healthcareproject.domain.model.Medication
import java.time.format.DateTimeFormatter

class MedicationAdapter(
    private val onEdit: (Medication) -> Unit,
    private val onDelete: (Medication) -> Unit
) : ListAdapter<Medication, MedicationAdapter.ViewHolder>(MedicationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemMedicationBinding,
        private val onEdit: (Medication) -> Unit,
        private val onDelete: (Medication) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medication: Medication) {
            binding.medication = medication
            binding.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            binding.iconEdit.setOnClickListener { onEdit(medication) }
            binding.iconDelete.setOnClickListener { onDelete(medication) }
            binding.executePendingBindings()
        }
    }
}

class MedicationDiffCallback : DiffUtil.ItemCallback<Medication>() {
    override fun areItemsTheSame(oldItem: Medication, newItem: Medication): Boolean {
        return oldItem.medicationId == newItem.medicationId
    }

    override fun areContentsTheSame(oldItem: Medication, newItem: Medication): Boolean {
        return oldItem == newItem
    }
}