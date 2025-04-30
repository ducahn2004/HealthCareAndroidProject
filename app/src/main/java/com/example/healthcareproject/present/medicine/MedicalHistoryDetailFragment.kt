package com.example.healthcareproject.present.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentMedicalHistoryDetailBinding
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.present.navigation.MainNavigator
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MedicalHistoryDetailFragment : Fragment() {
    private var _binding: FragmentMedicalHistoryDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicalHistoryDetailViewModel by viewModels()
    @Inject lateinit var mainNavigator: MainNavigator

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

        binding.ivBack.setOnClickListener {
            mainNavigator.navigateBack()
        }

        val visitId = arguments?.getString("visitId")
        visitId?.let { viewModel.loadDetails(it) }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            state.medicalVisit?.let { visit ->
                binding.tvCondition.text = visit.diagnosis
                binding.tvDoctor.text = visit.doctorName
                binding.tvFacility.text = visit.clinicName
                binding.tvDate.text = visit.visitDate.format(dateFormatter)
                binding.tvTime.text = visit.createdAt.format(timeFormatter)
                binding.tvLocation.text = visit.clinicName
                binding.tvDiagnosis.text = visit.diagnosis
                binding.tvDoctorRemarks.text = visit.treatment
                displayMedications(state.medications)
            }
        }
    }

    private fun displayMedications(medications: List<Medication>) {
        val container = binding.llMedications
        container.removeAllViews()
        if (medications.isEmpty()) {
            val textView = TextView(context).apply {
                text = "No medications available"
                textSize = 16f
                setTextColor(resources.getColor(R.color.secondary_text_color, null))
            }
            container.addView(textView)
        } else {
            medications.forEach { medication ->
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
                    text = "${medication.dosageAmount} ${medication.dosageUnit}"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.secondary_text_color, null))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                }
                dosageLayout.addView(dosageLabel)
                dosageLayout.addView(dosageValue)

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
                    text = medication.frequency.toString()
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