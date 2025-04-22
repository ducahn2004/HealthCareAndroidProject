package com.example.healthcareproject.present.pill

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentPillBinding
import com.example.healthcareproject.present.medicine.MedicalVisit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class PillFragment : Fragment() {

    private var _binding: FragmentPillBinding? = null
    private val binding get() = _binding!!

    private val medications = mutableListOf<Medication>()
    private lateinit var medicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load danh sách Medication từ SharedPreferences
        loadMedications()

        // Thiết lập RecyclerView cho medications
        medicationAdapter = MedicationAdapter { medication ->
            // Tìm MedicalVisit liên quan đến medication (dựa trên visitId)
            val medicalVisit = if (medication.visitId != null) {
                val sharedPrefs = requireActivity().getSharedPreferences("medical_visits", Context.MODE_PRIVATE)
                val visitsJson = sharedPrefs.getString("medical_visit_list", null)
                if (visitsJson != null) {
                    val type = object : TypeToken<List<MedicalVisit>>() {}.type
                    val medicalVisits: List<MedicalVisit> = Gson().fromJson(visitsJson, type)
                    medicalVisits.find { it.id == medication.visitId }
                } else {
                    null
                }
            } else {
                // Nếu không có visitId, tạo một MedicalVisit giả định
                MedicalVisit(
                    id = System.currentTimeMillis(),
                    condition = "Medication: ${medication.name}",
                    doctor = "Not specified",
                    facility = "Not specified",
                    timestamp = medication.startTimestamp,
                    location = null,
                    diagnosis = null,
                    doctorRemarks = medication.note
                )
            }

            // Điều hướng đến MedicalHistoryDetailFragment
            if (medicalVisit != null) {
                val bundle = Bundle().apply {
                    putParcelable("medicalVisit", medicalVisit)
                }
                findNavController().navigate(R.id.action_pillFragment_to_medicalHistoryDetailFragment, bundle)
            }
        }
        binding.rvCurrentMedications.layoutManager = LinearLayoutManager(context)
        binding.rvCurrentMedications.adapter = medicationAdapter
        medicationAdapter.submitList(medications.toList())

        // Xử lý nút thêm thuốc mới
        binding.fabAddMedication.setOnClickListener {
            findNavController().navigate(R.id.action_pillFragment_to_addMedicationFragment)
        }

        // Lắng nghe kết quả từ AddMedicationFragment
        setFragmentResultListener("medicationKey") { _, bundle ->
            Log.d("PillFragment", "Received bundle: $bundle")
            val newMedications = bundle.getParcelableArrayList<Medication>("newMedications")
            Log.d("PillFragment", "newMedications: $newMedications")
            if (newMedications != null) {
                medications.addAll(newMedications)
                Log.d("PillFragment", "Updated medications: $medications")
                saveMedications()
                medicationAdapter.submitList(medications.toList())
                Log.d("PillFragment", "Adapter updated with new list: $medications")
            }
        }
    }

    private fun loadMedications() {
        val sharedPrefs = requireActivity().getSharedPreferences("medications", Context.MODE_PRIVATE)
        val medicationsJson = sharedPrefs.getString("medication_list", null)
        if (medicationsJson != null) {
            val type = object : TypeToken<List<Medication>>() {}.type
            val loadedMedications: List<Medication> = Gson().fromJson(medicationsJson, type)
            medications.clear()
            medications.addAll(loadedMedications)
        } else {
            // Dữ liệu mẫu nếu chưa có
            val calendar = Calendar.getInstance()
            calendar.set(2025, Calendar.APRIL, 1)
            val startTimestamp = calendar.timeInMillis
            calendar.set(2025, Calendar.APRIL, 10)
            val endTimestamp = calendar.timeInMillis

            // Tạo MedicalVisit mẫu để liên kết với Medication
            val medicalVisit = MedicalVisit(
                id = System.currentTimeMillis(),
                condition = "Fever",
                doctor = "Dr. Tran Thi B",
                facility = "University Medical Center, HCMC",
                timestamp = startTimestamp,
                diagnosis = "Acute pharyngitis",
                doctorRemarks = "Take prescribed medications for 5 days."
            )

            // Lưu MedicalVisit mẫu vào SharedPreferences
            val medicalVisits = mutableListOf<MedicalVisit>()
            medicalVisits.add(medicalVisit)
            val sharedPrefsVisits = requireActivity().getSharedPreferences("medical_visits", Context.MODE_PRIVATE)
            val editor = sharedPrefsVisits.edit()
            val visitsJson = Gson().toJson(medicalVisits)
            editor.putString("medical_visit_list", visitsJson)
            editor.apply()

            // Gán visitId cho Medication mẫu
            medications.add(
                Medication(
                    name = "Paracetamol",
                    dosage = "500mg",
                    frequency = "Twice a day",
                    timeOfDay = "Morning, Evening",
                    startTimestamp = startTimestamp,
                    endTimestamp = endTimestamp,
                    note = "Take after meals",
                    visitId = medicalVisit.id
                )
            )
            saveMedications()
        }
    }
