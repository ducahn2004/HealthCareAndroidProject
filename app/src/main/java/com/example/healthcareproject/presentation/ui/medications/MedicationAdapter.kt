package com.example.healthcareproject.presentation.ui.medications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.databinding.ItemMedicationBinding
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.Medication
import timber.log.Timber
import java.time.format.DateTimeFormatter

class MedicationAdapter(
    private val onEdit: (Medication) -> Unit = {},
    private val onDelete: (Medication) -> Unit = {},
    private val onItemClick: (Medication) -> Unit = {},
    private val isHistoryView: Boolean = false // Thêm flag để điều chỉnh hành vi
) : ListAdapter<Medication, MedicationAdapter.MedicationViewHolder>(MedicationDiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val binding = ItemMedicationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicationViewHolder(binding, onEdit, onDelete, onItemClick, isHistoryView)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = getItem(position)
        Timber.d("Binding medication: ${medication.name} at position $position")
        holder.bind(getItem(position))
    }

    inner class MedicationViewHolder(
        private val binding: ItemMedicationBinding,
        private val onEdit: (Medication) -> Unit,
        private val onDelete: (Medication) -> Unit,
        private val onItemClick: (Medication) -> Unit,
        private val isHistoryView: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Xử lý click cho item (chỉ trong non-history view)
            if (!isHistoryView) {
                binding.root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClick(getItem(position))
                    }
                }
            }
            // Xử lý click cho edit/delete (ẩn trong history view)
            if (isHistoryView) {
                binding.iconEdit.visibility = View.GONE
                binding.iconDelete.visibility = View.GONE
            } else {
                binding.iconEdit.setOnClickListener {
                    onEdit(getItem(bindingAdapterPosition))
                }
                binding.iconDelete.setOnClickListener {
                    onDelete(getItem(bindingAdapterPosition))
                }
            }
        }

        fun bind(medication: Medication) {
            binding.medication = medication
            binding.dateFormatter = dateFormatter
            binding.tvDosage.text = "${medication.dosageAmount} ${medication.dosageUnit.toDisplayString()}"
            binding.tvFrequency.text = when (medication.frequency) {
                1 -> "Once daily"
                2 -> "Twice daily"
                else -> "${medication.frequency} times daily"
            }
            binding.tvStartDate.text = medication.startDate.format(dateFormatter)
            binding.tvEndDate.text = medication.endDate?.format(dateFormatter) ?: "Ongoing"
            binding.tvTimeOfDay.text = medication.timeOfDay.joinToString(", ")
            binding.tvMealRelation.text = medication.mealRelation.name
                .replace("_", " ")
                .lowercase()
                .replaceFirstChar { it.uppercase() }
            if (medication.notes.isEmpty()) {
                binding.notesContainer.visibility = View.GONE
            } else {
                binding.notesContainer.visibility = View.VISIBLE
                binding.tvNotes.text = medication.notes
            }
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

fun DosageUnit.toDisplayString(): String {
    return name.replace("PerDay", " per day").replaceFirstChar { it.uppercase()}
}