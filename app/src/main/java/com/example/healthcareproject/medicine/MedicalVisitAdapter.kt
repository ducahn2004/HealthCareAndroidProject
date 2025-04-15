package com.example.healthcareproject.medicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R

class MedicalVisitAdapter(
    private val onItemClick: (MedicalVisit) -> Unit
) : ListAdapter<MedicalVisit, MedicalVisitAdapter.ViewHolder>(MedicalVisitDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCondition: TextView = itemView.findViewById(R.id.tv_condition)
        val tvDoctorValue: TextView = itemView.findViewById(R.id.tv_doctor_value)
        val tvFacilityValue: TextView = itemView.findViewById(R.id.tv_facility_value)
        val tvDateValue: TextView = itemView.findViewById(R.id.tv_date_value)
        val tvTimeValue: TextView = itemView.findViewById(R.id.tv_time_value)
        val tvLocationValue: TextView = itemView.findViewById(R.id.tv_location_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medical_visit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visit = getItem(position)

        holder.tvCondition.text = visit.condition
        holder.tvDoctorValue.text = visit.doctor
        holder.tvFacilityValue.text = visit.facility
        holder.tvDateValue.text = visit.date
        holder.tvTimeValue.text = visit.time
        holder.tvLocationValue.text = visit.location ?: "Not specified"

        // Sự kiện click vào item để mở chi tiết
        holder.itemView.setOnClickListener {
            onItemClick(visit)
        }
    }
}

class MedicalVisitDiffCallback : DiffUtil.ItemCallback<MedicalVisit>() {
    override fun areItemsTheSame(oldItem: MedicalVisit, newItem: MedicalVisit): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MedicalVisit, newItem: MedicalVisit): Boolean {
        return oldItem == newItem
    }
}