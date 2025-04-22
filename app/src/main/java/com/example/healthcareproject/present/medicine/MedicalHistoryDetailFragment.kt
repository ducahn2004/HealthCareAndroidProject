package com.example.healthcareproject.present.medicine

import android.annotation.SuppressLint
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
import java.time.format.DateTimeFormatter

class MedicalHistoryDetailFragment : Fragment() {

    private var _binding: FragmentMedicalHistoryDetailBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

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
            binding.tvCondition.text = medicalVisit.diagnosis
            binding.tvDoctor.text = medicalVisit.doctorName
            binding.tvFacility.text = medicalVisit.clinicName
            binding.tvDate.text = medicalVisit.visitDate.format(dateFormatter)
            binding.tvTime.text = medicalVisit.visitDate.atStartOfDay().format(timeFormatter)
            binding.tvDiagnosis.text = medicalVisit.diagnosis
            binding.tvDoctorRemarks.text = medicalVisit.treatment

            // Load và hiển thị danh sách Medication
            val medications = loadMedications(medicalVisit.visitId)
            displayMedications(medications)
        }
    }

    private fun loadMedications(visitId: String): List<Medication> {
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

    @SuppressLint("SetTextI18n")
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
                    text = "${medication.dosageAmount} ${medication.dosageUnit.name.lowercase()}"
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
                    text = "${medication.frequency} times a day"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                frequencyLayout.addView(frequencyLabel)
                frequencyLayout.addView(frequencyValue)

                // Time of Day
                val timeOfDayLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    orientation = LinearLayout.HORIZONTAL
                }
                val timeOfDayLabel = TextView(context).apply {
                    text = "TIME OF DAY:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val timeOfDayValue = TextView(context).apply {
                    text = medication.timeOfDay.joinToString(", ")
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                timeOfDayLayout.addView(timeOfDayLabel)
                timeOfDayLayout.addView(timeOfDayValue)

                // Meal Relation
                val mealRelationLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    orientation = LinearLayout.HORIZONTAL
                }
                val mealRelationLabel = TextView(context).apply {
                    text = "MEAL RELATION:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val mealRelationValue = TextView(context).apply {
                    text = medication.mealRelation.name.replace("_", " ").lowercase()
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                mealRelationLayout.addView(mealRelationLabel)
                mealRelationLayout.addView(mealRelationValue)

                // Start Date
                val startDateLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    orientation = LinearLayout.HORIZONTAL
                }
                val startDateLabel = TextView(context).apply {
                    text = "START DATE:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val startDateValue = TextView(context).apply {
                    text = medication.startDate.format(dateFormatter)
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                startDateLayout.addView(startDateLabel)
                startDateLayout.addView(startDateValue)

                // End Date
                val endDateLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    orientation = LinearLayout.HORIZONTAL
                }
                val endDateLabel = TextView(context).apply {
                    text = "END DATE:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val endDateValue = TextView(context).apply {
                    text = medication.endDate.format(dateFormatter)
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                endDateLayout.addView(endDateLabel)
                endDateLayout.addView(endDateValue)

                // Notes
                val notesLayout = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }
                    orientation = LinearLayout.HORIZONTAL
                }
                val notesLabel = TextView(context).apply {
                    text = "NOTES:"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    isAllCaps = true
                }
                val notesValue = TextView(context).apply {
                    text = medication.notes.takeIf { it.isNotEmpty() } ?: "Not specified"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                notesLayout.addView(notesLabel)
                notesLayout.addView(notesValue)

                linearLayout.addView(nameLayout)
                linearLayout.addView(dosageLayout)
                linearLayout.addView(frequencyLayout)
                linearLayout.addView(timeOfDayLayout)
                linearLayout.addView(mealRelationLayout)
                linearLayout.addView(startDateLayout)
                linearLayout.addView(endDateLayout)
                linearLayout.addView(notesLayout)
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