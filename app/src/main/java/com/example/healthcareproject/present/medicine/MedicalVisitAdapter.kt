package com.example.healthcareproject.present.medicine

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

class MedicalVisitAdapter(
    private val onItemClick: (MedicalVisit) -> Unit
) : ListAdapter<MedicalVisit, MedicalVisitAdapter.MedicalVisitViewHolder>(MedicalVisitDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

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
        holder.tvFacility.text = visit.facility
        holder.tvDoctor.text = visit.doctor

        // Chuyển đổi timestamp thành định dạng ngày và giờ
        val dateTime = Date(visit.timestamp)
        holder.tvDate.text = dateFormat.format(dateTime)
        holder.tvTime.text = timeFormat.format(dateTime)

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