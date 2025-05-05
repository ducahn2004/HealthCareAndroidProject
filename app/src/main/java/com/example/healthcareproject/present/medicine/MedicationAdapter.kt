package com.example.healthcareproject.present.medicine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.healthcareproject.databinding.ItemMedicationBinding
import com.example.healthcareproject.domain.model.Medication
import java.time.format.DateTimeFormatter

class MedicationAdapter(
    private val dateFormatter: DateTimeFormatter
) : androidx.recyclerview.widget.ListAdapter<Medication, MedicationAdapter.MedicationViewHolder>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Medication>() {
        override fun areItemsTheSame(oldItem: Medication, newItem: Medication): Boolean =
            oldItem.medicationId == newItem.medicationId

        override fun areContentsTheSame(oldItem: Medication, newItem: Medication): Boolean =
            oldItem == newItem
    }
) {
    class MedicationViewHolder(val binding: ItemMedicationBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.binding.apply {
            medication = getItem(position)
            dateFormatter = this@MedicationAdapter.dateFormatter
            executePendingBindings()
        }
    }
}