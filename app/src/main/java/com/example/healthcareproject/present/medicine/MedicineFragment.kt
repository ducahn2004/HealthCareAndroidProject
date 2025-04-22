package com.example.healthcareproject.present.medicine

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentMedicineBinding
import com.example.healthcareproject.present.pill.Medication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class MedicineFragment : Fragment() {

    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!

    private lateinit var futureVisitsAdapter: MedicalVisitAdapter
    private lateinit var pastVisitsAdapter: MedicalVisitAdapter
    private val medicalVisits = mutableListOf<MedicalVisit>()
    private var filteredVisits = listOf<MedicalVisit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập RecyclerView cho Future Visits
        futureVisitsAdapter = MedicalVisitAdapter { medicalVisit ->
            val bundle = Bundle().apply {
                putParcelable("medicalVisit", medicalVisit)
            }
            findNavController().navigate(R.id.action_medicineFragment_to_medicalHistoryDetailFragment, bundle)
        }
        binding.recyclerViewAfter.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAfter.adapter = futureVisitsAdapter

        // Thiết lập RecyclerView cho Past Visits
        pastVisitsAdapter = MedicalVisitAdapter { medicalVisit ->
            val bundle = Bundle().apply {
                putParcelable("medicalVisit", medicalVisit)
            }
            findNavController().navigate(R.id.action_medicineFragment_to_medicalHistoryDetailFragment, bundle)
        }
        binding.recyclerViewBefore.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewBefore.adapter = pastVisitsAdapter

        // Load dữ liệu từ SharedPreferences
        loadMedicalVisits()

        // Hiển thị danh sách
        updateMedicalVisitList()

        // Thiết lập tìm kiếm
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterVisits(s.toString())
            }
        })

        // Xử lý nút Floating Action Button để thêm Appointment
        binding.fabAddVisit.setOnClickListener {
            findNavController().navigate(R.id.action_medicineFragment_to_addAppointmentFragment)
        }

        // Lắng nghe kết quả từ AddAppointmentFragment
        setFragmentResultListener("requestKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            if (newVisit != null) {
                medicalVisits.add(newVisit)
                saveMedicalVisits()
                updateMedicalVisitList()
            }
        }

        // Lắng nghe kết quả từ AddMedicationFragment
        setFragmentResultListener("medicationKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            val newMedications = bundle.getParcelableArrayList<Medication>("newMedications")
            if (newVisit != null) {
                medicalVisits.add(newVisit)
                if (newMedications != null) {
                    saveMedications(newMedications)
                }
                saveMedicalVisits()
                updateMedicalVisitList()
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

    private fun saveMedicalVisits() {
        val sharedPrefs = requireActivity().getSharedPreferences("medical_visits", android.content.Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val medicalVisitsJson = Gson().toJson(medicalVisits)
        editor.putString("visit_list", medicalVisitsJson)
        editor.apply()
    }

    private fun saveMedications(medications: List<Medication>) {
        val sharedPrefs = requireActivity().getSharedPreferences("medications", android.content.Context.MODE_PRIVATE)
        val currentMedicationsJson = sharedPrefs.getString("medication_list", null)
        val currentMedications: MutableList<Medication> = if (currentMedicationsJson != null) {
            val type = object : TypeToken<List<Medication>>() {}.type
            Gson().fromJson(currentMedicationsJson, type) as MutableList<Medication>
        } else {
            mutableListOf()
        }
        currentMedications.addAll(medications)
        val editor = sharedPrefs.edit()
        val medicationsJson = Gson().toJson(currentMedications)
        editor.putString("medication_list", medicationsJson)
        editor.apply()
    }

    private fun filterVisits(query: String) {
        filteredVisits = if (query.isEmpty()) {
            medicalVisits.toList()
        } else {
            medicalVisits.filter {
                it.diagnosis.contains(query, ignoreCase = true) ||
                        it.doctorName.contains(query, ignoreCase = true) ||
                        it.clinicName.contains(query, ignoreCase = true)
            }
        }
        updateMedicalVisitList()
    }

    private fun updateMedicalVisitList() {
        // Sắp xếp danh sách theo ngày giảm dần
        val sortedVisits = filteredVisits.sortedByDescending { it.visitDate }

        // Phân loại Future và Past Visits
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val futureVisits = sortedVisits.filter { visit ->
            val visitDateTime = visit.visitDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            visitDateTime >= today
        }

        val pastVisits = sortedVisits.filter { visit ->
            val visitDateTime = visit.visitDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            visitDateTime < today
        }

        // Cập nhật Future Visits
        if (futureVisits.isNotEmpty()) {
            binding.tvNoFutureVisits.visibility = View.GONE
            binding.recyclerViewAfter.visibility = View.VISIBLE
            futureVisitsAdapter.submitList(futureVisits)
        } else {
            binding.tvNoFutureVisits.visibility = View.VISIBLE
            binding.recyclerViewAfter.visibility = View.GONE
        }

        // Cập nhật Past Visits
        if (pastVisits.isNotEmpty()) {
            binding.tvNoPastVisits.visibility = View.GONE
            binding.recyclerViewBefore.visibility = View.VISIBLE
            pastVisitsAdapter.submitList(pastVisits)
        } else {
            binding.tvNoPastVisits.visibility = View.VISIBLE
            binding.recyclerViewBefore.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}