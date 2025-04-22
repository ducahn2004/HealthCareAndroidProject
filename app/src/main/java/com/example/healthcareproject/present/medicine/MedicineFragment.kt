package com.example.healthcareproject

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentMedicineBinding
import com.example.healthcareproject.present.medicine.MedicalVisit
import com.example.healthcareproject.present.medicine.MedicalVisitAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MedicineFragment : Fragment() {

    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!

    private val medicalVisits = mutableListOf<MedicalVisit>()
    private val futureVisits = mutableListOf<MedicalVisit>()
    private val pastVisits = mutableListOf<MedicalVisit>()
    private lateinit var futureVisitAdapter: MedicalVisitAdapter
    private lateinit var pastVisitAdapter: MedicalVisitAdapter
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val SEARCH_DEBOUNCE_DELAY = 300L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load danh sách MedicalVisit từ SharedPreferences
        loadMedicalVisits()

        // Phân loại visits thành future và past
        categorizeVisits()

        // Thiết lập RecyclerView cho future visits
        futureVisitAdapter = MedicalVisitAdapter { visit ->
            val bundle = Bundle().apply {
                putParcelable("medicalVisit", visit)
            }
            findNavController().navigate(R.id.action_medicineFragment_to_medicalHistoryDetailFragment, bundle)
        }
        binding.recyclerViewAfter.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAfter.adapter = futureVisitAdapter
        updateFutureVisitsVisibility()

        // Thiết lập RecyclerView cho past visits
        pastVisitAdapter = MedicalVisitAdapter { visit ->
            val bundle = Bundle().apply {
                putParcelable("medicalVisit", visit)
            }
            findNavController().navigate(R.id.action_medicineFragment_to_medicalHistoryDetailFragment, bundle)
        }
        binding.recyclerViewBefore.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewBefore.adapter = pastVisitAdapter
        updatePastVisitsVisibility()

        // Xử lý nút thêm cuộc hẹn mới
        binding.fabAddVisit.setOnClickListener {
            findNavController().navigate(R.id.action_medicineFragment_to_addAppointmentFragment)
        }

        // Lắng nghe kết quả từ AddAppointmentFragment
        setFragmentResultListener("requestKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            if (newVisit != null) {
                medicalVisits.add(newVisit)
                saveMedicalVisits()
                categorizeVisits()
                updateAdapters()
            }
        }

        // Thiết lập tìm kiếm với debounce
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    filterVisits(s.toString())
                }
                searchHandler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
            }
        })

        // Xử lý nút xóa văn bản tìm kiếm
        binding.etSearch.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etSearch.right - binding.etSearch.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    binding.etSearch.setText("")
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun loadMedicalVisits() {
        val sharedPrefs = requireActivity().getSharedPreferences("medical_visits", Context.MODE_PRIVATE)
        val visitsJson = sharedPrefs.getString("medical_visit_list", null)
        if (visitsJson != null) {
            val type = object : TypeToken<List<MedicalVisit>>() {}.type
            val loadedVisits: List<MedicalVisit> = Gson().fromJson(visitsJson, type)
            medicalVisits.clear()
            medicalVisits.addAll(loadedVisits)
        } else {
            // Dữ liệu mẫu nếu chưa có
            val calendar = Calendar.getInstance()
            calendar.set(2025, Calendar.APRIL, 11, 11, 0)
            medicalVisits.add(
                MedicalVisit(
                    id = System.currentTimeMillis(),
                    condition = "Fever",
                    doctor = "Dr. Tran Thi B",
                    facility = "University Medical Center, HCMC",
                    timestamp = calendar.timeInMillis,
                    diagnosis = "Acute pharyngitis",
                    doctorRemarks = "Patient shows symptoms of fever, sore throat, and wheezing. Advised to rest and take prescribed medications for 5 days."
                )
            )
            saveMedicalVisits()
        }
    }

    private fun saveMedicalVisits() {
        val sharedPrefs = requireActivity().getSharedPreferences("medical_visits", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val visitsJson = Gson().toJson(medicalVisits)
        editor.putString("medical_visit_list", visitsJson)
        editor.apply()
    }

    private fun categorizeVisits() {
        futureVisits.clear()
        pastVisits.clear()

        val today = Calendar.getInstance().timeInMillis

        medicalVisits.forEach { visit ->
            if (visit.timestamp >= today) {
                futureVisits.add(visit)
            } else {
                pastVisits.add(visit)
            }
        }

        // Sắp xếp future visits theo ngày tăng dần
        futureVisits.sortBy { visit: MedicalVisit -> visit.timestamp }
        // Sắp xếp past visits theo ngày giảm dần
        pastVisits.sortByDescending { visit: MedicalVisit -> visit.timestamp }
    }

    private fun updateAdapters() {
        futureVisitAdapter.submitList(futureVisits.toList())
        pastVisitAdapter.submitList(pastVisits.toList())
        updateFutureVisitsVisibility()
        updatePastVisitsVisibility()
    }

    private fun updateFutureVisitsVisibility() {
        if (futureVisits.isEmpty()) {
            binding.recyclerViewAfter.visibility = View.GONE
            binding.tvNoFutureVisits.visibility = View.VISIBLE
        } else {
            binding.recyclerViewAfter.visibility = View.VISIBLE
            binding.tvNoFutureVisits.visibility = View.GONE
        }
    }

    private fun updatePastVisitsVisibility() {
        if (pastVisits.isEmpty()) {
            binding.recyclerViewBefore.visibility = View.GONE
            binding.tvNoPastVisits.visibility = View.VISIBLE
        } else {
            binding.recyclerViewBefore.visibility = View.VISIBLE
            binding.tvNoPastVisits.visibility = View.GONE
        }
    }

    private fun filterVisits(query: String) {
        val filteredFutureVisits = if (query.isEmpty()) {
            futureVisits.toList()
        } else {
            futureVisits.filter {
                it.condition.contains(query, ignoreCase = true) ||
                        it.doctor.contains(query, ignoreCase = true) ||
                        it.facility.contains(query, ignoreCase = true)
            }
        }

        val filteredPastVisits = if (query.isEmpty()) {
            pastVisits.toList()
        } else {
            pastVisits.filter {
                it.condition.contains(query, ignoreCase = true) ||
                        it.doctor.contains(query, ignoreCase = true) ||
                        it.facility.contains(query, ignoreCase = true)
            }
        }

        futureVisitAdapter.submitList(filteredFutureVisits)
        pastVisitAdapter.submitList(filteredPastVisits)
        updateFutureVisitsVisibility()
        updatePastVisitsVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        _binding = null
    }
}