package com.example.healthcareproject.present.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.healthcareproject.databinding.FragmentMedicalHistoryDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class MedicalHistoryDetailFragment : Fragment() {

    private var _binding: FragmentMedicalHistoryDetailBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicalHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val medicalVisit = arguments?.getParcelable<MedicalVisit>("medicalVisit")
        if (medicalVisit != null) {
            binding.tvCondition.text = medicalVisit.condition
            binding.tvDoctor.text = medicalVisit.doctor
            binding.tvFacility.text = medicalVisit.facility
            val dateTime = Date(medicalVisit.timestamp)
            binding.tvDate.text = dateFormat.format(dateTime)
            binding.tvTime.text = timeFormat.format(dateTime)
            binding.tvLocation.text = medicalVisit.location ?: "Not specified"
            binding.tvDiagnosis.text = medicalVisit.diagnosis ?: "Not specified"
            binding.tvDoctorRemarks.text = medicalVisit.doctorRemarks ?: "Not specified"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}