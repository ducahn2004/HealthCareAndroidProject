package com.example.healthcareproject.present.medicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import com.example.healthcareproject.domain.model.MedicalVisit
import java.time.format.DateTimeFormatter

class MedicalVisitAdapter(
    private val onItemClick: (MedicalVisit) -> Unit
) : ListAdapter<MedicalVisit, MedicalVisitAdapter.MedicalVisitViewHolder>(MedicalVisitDiffCallback()) {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    class MedicalVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFacility: TextView = itemView.findViewById(R.id.tv_facility)
        val tvDoctor: TextView = itemView.findViewById(R.id.tv_doctor)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalVisitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medical_visit, parent, false)
        return MedicalVisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicalVisitViewHolder, position: Int) {
        val visit = getItem(position)
        holder.tvFacility.text = visit.clinicName
        holder.tvDoctor.text = visit.doctorName
        holder.tvDate.text = visit.visitDate.format(dateFormatter)
        holder.tvTime.text = visit.createdAt.format(timeFormatter)
        holder.itemView.setOnClickListener {
            onItemClick(visit)
        }
    }
}

class MedicalVisitDiffCallback : DiffUtil.ItemCallback<MedicalVisit>() {
    override fun areItemsTheSame(oldItem: MedicalVisit, newItem: MedicalVisit): Boolean {
        return oldItem.visitId == newItem.visitId
    }

    override fun areContentsTheSame(oldItem: MedicalVisit, newItem: MedicalVisit): Boolean {
        return oldItem == newItem
    }
}