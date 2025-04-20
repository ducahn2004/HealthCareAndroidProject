package com.example.healthcareproject.present.pill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.healthcareproject.databinding.FragmentMedicationDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class MedicationDetailFragment : Fragment() {

    private var _binding: FragmentMedicationDetailBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val medication = arguments?.getParcelable<Medication>("medication")
        if (medication != null) {
            binding.tvName.text = medication.name
            binding.tvDosage.text = medication.dosage
            binding.tvFrequency.text = medication.frequency
            binding.tvTimeOfDay.text = medication.timeOfDay
            binding.tvStartDate.text = dateFormat.format(Date(medication.startTimestamp))
            binding.tvEndDate.text = medication.endTimestamp?.let { dateFormat.format(Date(it)) } ?: "Not specified"
            binding.tvNote.text = medication.note
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}