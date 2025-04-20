package com.example.healthcareproject.present.medicine

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentMedicalHistoryDetailBinding
import com.example.healthcareproject.present.pill.Medication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

        // Xử lý nút Back
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Lấy MedicalVisit từ Bundle
        val medicalVisit = arguments?.getParcelable<MedicalVisit>("medicalVisit")
        if (medicalVisit != null) {
            // Hiển thị thông tin MedicalVisit
            binding.tvCondition.text = medicalVisit.condition
            binding.tvDoctor.text = medicalVisit.doctor
            binding.tvFacility.text = medicalVisit.facility
            binding.tvDate.text = dateFormat.format(Date(medicalVisit.timestamp))
            binding.tvTime.text = timeFormat.format(Date(medicalVisit.timestamp))
            binding.tvLocation.text = medicalVisit.location ?: "Not specified"
            binding.tvDiagnosis.text = medicalVisit.diagnosis ?: "Not specified"
            binding.tvDoctorRemarks.text = medicalVisit.doctorRemarks ?: "Not specified"

            // Load và hiển thị danh sách Medication
            val medications = loadMedications(medicalVisit.id)
            displayMedications(medications)
        }
    }

    private fun loadMedications(visitId: Long): List<Medication> {
        val sharedPrefs = requireActivity().getSharedPreferences("medications", Context.MODE_PRIVATE)
        val medicationsJson = sharedPrefs.getString("medication_list", null)
        return if (medicationsJson != null) {
            val type = object : TypeToken<List<Medication>>() {}.type
            val allMedications: List<Medication> = Gson().fromJson(medicationsJson, type)
            allMedications.filter { it.visitId == visitId }
        } else {
            emptyList()
        }
    }

    private fun displayMedications(medications: List<Medication>) {
        val container = binding.llMedications
        if (medications.isEmpty()) {
            val textView = TextView(context).apply {
                text = "No medications available"
                textSize = 16f
                setTextColor(resources.getColor(R.color.secondary_text_color, null))
            }
            container.addView(textView)
        } else {
            medications.forEach { medication ->
                // Tạo CardView cho từng Medication
                val cardView = androidx.cardview.widget.CardView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 8)
                    }
                    radius = 8f
                    cardElevation = 2f
                    setCardBackgroundColor(resources.getColor(R.color.surface_alternate, null))
                }

                val linearLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.VERTICAL
                    setPadding(12, 12, 12, 12)
                }

                // Name
                val nameLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                }
                val nameLabel = TextView(context).apply {
                    text = "NAME:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val nameValue = TextView(context).apply {
                    text = medication.name
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                nameLayout.addView(nameLabel)
                nameLayout.addView(nameValue)

                // Dosage
                val dosageLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    orientation = LinearLayout.HORIZONTAL
                }
                val dosageLabel = TextView(context).apply {
                    text = "DOSAGE:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val dosageValue = TextView(context).apply {
                    text = medication.dosage
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                dosageLayout.addView(dosageLabel)
                dosageLayout.addView(dosageValue)

                // Frequency
                val frequencyLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    orientation = LinearLayout.HORIZONTAL
                }
                val frequencyLabel = TextView(context).apply {
                    text = "FREQUENCY:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val frequencyValue = TextView(context).apply {
                    text = medication.frequency
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                frequencyLayout.addView(frequencyLabel)
                frequencyLayout.addView(frequencyValue)

                linearLayout.addView(nameLayout)
                linearLayout.addView(dosageLayout)
                linearLayout.addView(frequencyLayout)
                cardView.addView(linearLayout)
                container.addView(cardView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}