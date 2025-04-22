package com.example.healthcareproject.present.pill

import android.os.Bundle
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import androidx.core.content.edit

class PillFragment : Fragment() {

    private var _binding: FragmentPillBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentMedicationAdapter: MedicationAdapter
    private lateinit var pastMedicationAdapter: MedicationAdapter
    private val medications = mutableListOf<Medication>()
    private val medicalVisits = mutableListOf<MedicalVisit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập RecyclerView cho Current Medications
        currentMedicationAdapter = MedicationAdapter { medication ->
            val bundle = Bundle().apply {
                putParcelable("medication", medication)
            }
            findNavController().navigate(R.id.action_pillFragment_to_medicalHistoryDetailFragment, bundle)
        }
        binding.rvCurrentMedications.layoutManager = LinearLayoutManager(context)
        binding.rvCurrentMedications.adapter = currentMedicationAdapter

        // Thiết lập RecyclerView cho Past Medications
        pastMedicationAdapter = MedicationAdapter { medication ->
            val bundle = Bundle().apply {
                putParcelable("medication", medication)
            }
            findNavController().navigate(R.id.action_pillFragment_to_medicalHistoryDetailFragment, bundle)
        }
        binding.rvPastMedications.layoutManager = LinearLayoutManager(context)
        binding.rvPastMedications.adapter = pastMedicationAdapter

        // Load dữ liệu từ SharedPreferences
        loadMedicalVisits()
        loadMedications()

        // Hiển thị danh sách
        updateMedicationList()

        // Xử lý nút Floating Action Button để thêm Medication
        binding.fabAddMedication.setOnClickListener {
            findNavController().navigate(R.id.action_pillFragment_to_addMedicationFragment)
        }

        // Lắng nghe kết quả từ AddMedicationFragment
        setFragmentResultListener("medicationKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            val newMedications = bundle.getParcelableArrayList<Medication>("newMedications")
            if (newVisit != null) {
                medicalVisits.add(newVisit)
                saveMedicalVisits()
                if (newMedications != null) {
                    medications.addAll(newMedications)
                    saveMedications()
                }
                updateMedicationList()
            }
        }
    }

    private fun loadMedicalVisits() {
        val sharedPrefs = requireActivity().getSharedPreferences("medical_visits", android.content.Context.MODE_PRIVATE)
        val medicalVisitsJson = sharedPrefs.getString("visit_list", null)
        if (medicalVisitsJson != null) {
            val type = object : TypeToken<List<MedicalVisit>>() {}.type
            val loadedVisits: List<MedicalVisit> = Gson().fromJson(medicalVisitsJson, type)
            medicalVisits.clear()
            medicalVisits.addAll(loadedVisits)
        }
    }

    private fun loadMedications() {
        val sharedPrefs = requireActivity().getSharedPreferences("medications", android.content.Context.MODE_PRIVATE)
        val medicationsJson = sharedPrefs.getString("medication_list", null)
        if (medicationsJson != null) {
            val type = object : TypeToken<List<Medication>>() {}.type
            val loadedMedications: List<Medication> = Gson().fromJson(medicationsJson, type)
            medications.clear()
            medications.addAll(loadedMedications)
        }
    }

    private fun saveMedicalVisits() {
        val sharedPrefs = requireActivity().getSharedPreferences("medical_visits", android.content.Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val medicalVisitsJson = Gson().toJson(medicalVisits)
        editor.putString("visit_list", medicalVisitsJson)
        editor.apply()
    }

    private fun saveMedications() {
        val sharedPrefs = requireActivity().getSharedPreferences("medications", android.content.Context.MODE_PRIVATE)
        sharedPrefs.edit() {
            val medicationsJson = Gson().toJson(medications)
            putString("medication_list", medicationsJson)
        }
    }

    private fun updateMedicationList() {
        // Sắp xếp danh sách theo ngày bắt đầu giảm dần
        val sortedMedications = medications.sortedByDescending { it.startDate }

        // Phân loại Current và Past Medications
        val today = LocalDate.now()
        val currentMedications = sortedMedications.filter { medication ->
            medication.startDate <= today && (medication.endDate >= today || medication.endDate == today)
        }

        val pastMedications = sortedMedications.filter { medication ->
            medication.endDate < today
        }

        // Cập nhật Current Medications
        currentMedicationAdapter.submitList(currentMedications)

        // Cập nhật Past Medications
        pastMedicationAdapter.submitList(pastMedications)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}