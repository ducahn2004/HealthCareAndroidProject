package com.example.healthcareproject.present.pill

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemMedicationBinding
import com.example.healthcareproject.domain.model.Medication
import java.time.format.DateTimeFormatter

class MedicationAdapter(
    private val onItemClick: (Medication) -> Unit
) : ListAdapter<Medication, MedicationAdapter.MedicationViewHolder>(MedicationDiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MedicationViewHolder(
        private val binding: ItemMedicationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(medication: Medication) {
            // Set data directly to views through data binding
            binding.tvMedicationName.text = medication.name
            binding.tvDosage.text = "${medication.dosageAmount} ${medication.dosageUnit}"
            binding.tvFrequency.text = medication.frequency.toString()
            binding.tvStartDate.text = medication.startDate.format(dateFormatter)
            binding.tvEndDate.text = medication.endDate?.format(dateFormatter) ?: "Ongoing"

            // Handle notes visibility
            if (medication.notes.isNullOrEmpty()) {
                binding.notesContainer.visibility = android.view.View.GONE
            } else {
                binding.notesContainer.visibility = android.view.View.VISIBLE
                binding.tvNotes.text = medication.notes
            }

            // Execute pending bindings to update the view immediately
            binding.executePendingBindings()
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
}