package com.example.healthcareproject.present.medicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import java.time.format.DateTimeFormatter

class MedicalVisitAdapter(
    private val onItemClick: (MedicalVisit) -> Unit
) : ListAdapter<MedicalVisit, MedicalVisitAdapter.MedicalVisitViewHolder>(MedicalVisitDiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    class MedicalVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDiagnosis: TextView = itemView.findViewById(R.id.tv_diagnosis)
        val tvVisitId: TextView = itemView.findViewById(R.id.tv_visit_id)
        val tvUserId: TextView = itemView.findViewById(R.id.tv_user_id)
        val tvDoctorName: TextView = itemView.findViewById(R.id.tv_doctor_name)
        val tvClinicName: TextView = itemView.findViewById(R.id.tv_clinic_name)
        val tvVisitDate: TextView = itemView.findViewById(R.id.tv_visit_date)
        val tvTreatment: TextView = itemView.findViewById(R.id.tv_treatment)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tv_created_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalVisitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medical_visit, parent, false)
        return MedicalVisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicalVisitViewHolder, position: Int) {
        val visit = getItem(position)
        holder.tvDiagnosis.text = visit.diagnosis
        holder.tvVisitId.text = visit.visitId
        holder.tvUserId.text = visit.userId
        holder.tvDoctorName.text = visit.doctorName
        holder.tvClinicName.text = visit.clinicName
        holder.tvVisitDate.text = visit.visitDate.format(dateFormatter)
        holder.tvTreatment.text = visit.treatment.takeIf { it.isNotEmpty() } ?: "Not specified"
        holder.tvCreatedAt.text = visit.createdAt.format(dateFormatter)

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