package com.example.healthcareproject.present.ui.medicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemMedicalVisitBinding
import com.example.healthcareproject.domain.model.MedicalVisit
import java.time.format.DateTimeFormatter

class MedicalVisitAdapter(
    private val onItemClick: (MedicalVisit) -> Unit
) : ListAdapter<MedicalVisit, MedicalVisitAdapter.MedicalVisitViewHolder>(MedicalVisitDiffCallback()) {

    // Define formatters directly in the adapter
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    class MedicalVisitViewHolder(
        private val binding: ItemMedicalVisitBinding,
        private val dateFormatter: DateTimeFormatter,
        private val timeFormatter: DateTimeFormatter
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(visit: MedicalVisit, clickListener: (MedicalVisit) -> Unit) {
            binding.visit = visit
            binding.clickListener = View.OnClickListener { clickListener(visit) }

            // Format dates directly here
            binding.tvDate.text = visit.visitDate.format(dateFormatter)
            binding.tvTime.text = visit.createdAt.format(timeFormatter)

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalVisitViewHolder {
        val binding = ItemMedicalVisitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MedicalVisitViewHolder(binding, dateFormatter, timeFormatter)
    }

    override fun onBindViewHolder(holder: MedicalVisitViewHolder, position: Int) {
        val visit = getItem(position)
        holder.bind(visit, onItemClick)
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