package com.example.healthcareproject.present.pill

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import java.time.format.DateTimeFormatter

class MedicationAdapter(
    private val onItemClick: (Medication) -> Unit
) : ListAdapter<Medication, MedicationAdapter.MedicationViewHolder>(MedicationDiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMedicationId: TextView = itemView.findViewById(R.id.tv_medication_id)
        val tvUserId: TextView = itemView.findViewById(R.id.tv_user_id)
        val tvVisitId: TextView = itemView.findViewById(R.id.tv_visit_id)
        val tvName: TextView = itemView.findViewById(R.id.tv_medication_name)
        val tvDosage: TextView = itemView.findViewById(R.id.tv_dosage)
        val tvFrequency: TextView = itemView.findViewById(R.id.tv_frequency)
        val tvTimeOfDay: TextView = itemView.findViewById(R.id.tv_time_of_day)
        val tvMealRelation: TextView = itemView.findViewById(R.id.tv_meal_relation)
        val tvStartDate: TextView = itemView.findViewById(R.id.tv_start_date)
        val tvEndDate: TextView = itemView.findViewById(R.id.tv_end_date)
        val tvNotes: TextView = itemView.findViewById(R.id.tv_notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = getItem(position)
        holder.tvMedicationId.text = medication.medicationId
        holder.tvUserId.text = medication.userId
        holder.tvVisitId.text = medication.visitId ?: "Not specified"
        holder.tvName.text = medication.name
        holder.tvDosage.text = "${medication.dosageAmount} + ${medication.dosageUnit.name.lowercase()}"
        holder.tvFrequency.text = "${medication.frequency} times a day"
        holder.tvTimeOfDay.text = medication.timeOfDay.joinToString(", ")
        holder.tvMealRelation.text = medication.mealRelation.name.replace("_", " ").lowercase()
        holder.tvStartDate.text = medication.startDate.format(dateFormatter)
        holder.tvEndDate.text = medication.endDate.format(dateFormatter)
        holder.tvNotes.text = medication.notes

        holder.itemView.setOnClickListener {
            onItemClick(medication)
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