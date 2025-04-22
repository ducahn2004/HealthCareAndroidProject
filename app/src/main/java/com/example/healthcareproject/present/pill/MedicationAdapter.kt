package com.example.healthcareproject.present.pill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import java.text.SimpleDateFormat
import java.util.*

class MedicationAdapter(
    private val onItemClick: (Medication) -> Unit
) : ListAdapter<Medication, MedicationAdapter.MedicationViewHolder>(MedicationDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_medication_name)
        val tvDosage: TextView = itemView.findViewById(R.id.tv_dosage)
        val tvFrequency: TextView = itemView.findViewById(R.id.tv_frequency)
        val tvTimeOfDay: TextView = itemView.findViewById(R.id.tv_time_of_day)
        val tvStartDate: TextView = itemView.findViewById(R.id.tv_start_date)
        val tvEndDate: TextView = itemView.findViewById(R.id.tv_end_date)
        val tvNote: TextView = itemView.findViewById(R.id.tv_note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = getItem(position)
        holder.tvName.text = medication.name
        holder.tvDosage.text = medication.dosage
        holder.tvFrequency.text = medication.frequency
        holder.tvTimeOfDay.text = medication.timeOfDay
        holder.tvStartDate.text = dateFormat.format(Date(medication.startTimestamp))
        holder.tvEndDate.text = medication.endTimestamp?.let { dateFormat.format(Date(it)) } ?: "Not specified"
        holder.tvNote.text = medication.note

        holder.itemView.setOnClickListener {
            onItemClick(medication)
        }
    }
}

class MedicationDiffCallback : DiffUtil.ItemCallback<Medication>() {
    override fun areItemsTheSame(oldItem: Medication, newItem: Medication): Boolean {
        return oldItem.visitId == newItem.visitId
    }

    override fun areContentsTheSame(oldItem: Medication, newItem: Medication): Boolean {
        return oldItem == newItem
    }
}