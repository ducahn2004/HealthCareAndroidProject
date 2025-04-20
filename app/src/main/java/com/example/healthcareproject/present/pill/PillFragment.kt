package com.example.healthcareproject.present.pill

import android.content.Context
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
            val bundle = Bundle().apply {
                putParcelable("medication", medication)
            }
            // Cập nhật ID điều hướng nếu cần
            findNavController().navigate(R.id.action_pillFragment_to_medicationDetailFragment, bundle)
        }
        binding.rvCurrentMedications.layoutManager = LinearLayoutManager(context)
        binding.rvPastMedications.adapter = medicationAdapter
        medicationAdapter.submitList(medications.toList())

        // Xử lý nút thêm thuốc mới
        binding.fabAddMedication.setOnClickListener {
            findNavController().navigate(R.id.action_pillFragment_to_addMedicationFragment)
        }

        // Lắng nghe kết quả từ AddMedicationFragment
        setFragmentResultListener("medicationKey") { _, bundle ->
            val newVisit = bundle.getParcelable<Medication>("newVisit")
            val newMedications = bundle.getParcelableArrayList<Medication>("newMedications")
            if (newMedications != null) {
                medications.addAll(newMedications)
                saveMedications()
                medicationAdapter.submitList(medications.toList())
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
            medications.add(
                Medication(
                    name = "Paracetamol",
                    dosage = "500mg",
                    frequency = "Twice a day",
                    timeOfDay = "Morning, Evening",
                    startTimestamp = startTimestamp,
                    endTimestamp = endTimestamp,
                    note = "Take after meals",
                    visitId = null
                )
            )
        }
    }

    private fun saveMedications() {
        val sharedPrefs = requireActivity().getSharedPreferences("medications", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val medicationsJson = Gson().toJson(medications)
        editor.putString("medication_list", medicationsJson)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}