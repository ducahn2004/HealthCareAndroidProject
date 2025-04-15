package com.example.healthcareproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.medicine.MedicalVisit
import com.example.healthcareproject.medicine.MedicalVisitAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MedicineFragment : Fragment() {

    private lateinit var recyclerViewBefore: RecyclerView
    private lateinit var recyclerViewAfter: RecyclerView
    private lateinit var adapterBefore: MedicalVisitAdapter
    private lateinit var adapterAfter: MedicalVisitAdapter
    private lateinit var medicalVisits: MutableList<MedicalVisit>
    private lateinit var fabAddVisit: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medicine, container, false)

        recyclerViewBefore = view.findViewById(R.id.recycler_view_before)
        recyclerViewAfter = view.findViewById(R.id.recycler_view_after)
        fabAddVisit = view.findViewById(R.id.fab_add_visit)

        medicalVisits = mutableListOf()
        adapterBefore = MedicalVisitAdapter { visit ->
            openMedicalHistoryDetail(visit)
        }
        adapterAfter = MedicalVisitAdapter { visit ->
            openMedicalHistoryDetail(visit)
        }

        recyclerViewBefore.layoutManager = LinearLayoutManager(context)
        recyclerViewAfter.layoutManager = LinearLayoutManager(context)
        recyclerViewBefore.adapter = adapterBefore
        recyclerViewAfter.adapter = adapterAfter

        loadSampleData()
        filterVisitsByDate()

        // Điều hướng đến AddAppointmentFragment khi nhấn FAB
        fabAddVisit.setOnClickListener {
            findNavController().navigate(R.id.action_medicineFragment_to_addAppointmentFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di chuyển setupSearch() vào onViewCreated
        setupSearch()

        // Nhận dữ liệu từ AddAppointmentFragment
        setFragmentResultListener("requestKey") { _, bundle ->
            val newVisit = bundle.getParcelable<MedicalVisit>("newVisit")
            newVisit?.let {
                medicalVisits.add(it)
                filterVisitsByDate()
            }
        }
    }

    private fun loadSampleData() {
        medicalVisits.addAll(
            listOf(
                // History (trước ngày 15/04/2025)
                MedicalVisit(
                    condition = "PNEUMONIA",
                    doctor = "BS. Tuana Satoth",
                    facility = "BV Bach Mai",
                    date = "10/04/2025",
                    time = "08:00",
                    location = "Room 301, Building A, 123 Nguyen Trai Street"
                ),
                MedicalVisit(
                    condition = "BRONCHITIS",
                    doctor = "Dr. Nguyen Van A",
                    facility = "Cho Ray Hospital",
                    date = "05/04/2025",
                    time = "14:30",
                    location = "Room 205, Building C, 789 Tran Hung Dao Street"
                ),
                MedicalVisit(
                    condition = "MIGRAINE",
                    doctor = "Dr. Le Thi C",
                    facility = "FV Hospital",
                    date = "01/04/2025",
                    time = "10:00",
                    location = "Room 102, Building D, 456 Le Dai Hanh Street"
                ),
                // Future (ngày 15/04/2025 hoặc sau đó)
                MedicalVisit(
                    condition = "FLU",
                    doctor = "Dr. John Doe",
                    facility = "City Hospital",
                    date = "15/04/2025",
                    time = "09:00",
                    location = "Room 402, Building B, 456 Le Loi Street"
                ),
                MedicalVisit(
                    condition = "CHECK-UP",
                    doctor = "Dr. Pham Thi B",
                    facility = "Vinmec Hospital",
                    date = "20/04/2025",
                    time = "11:00",
                    location = "Room 305, Building E, 123 Vinh Nghiem Street"
                ),
                MedicalVisit(
                    condition = "ALLERGY TEST",
                    doctor = "Dr. Tran Van D",
                    facility = "International Clinic",
                    date = "25/04/2025",
                    time = "15:00",
                    location = "Room 108, Building F, 789 Nguyen Thi Minh Khai Street"
                )
            )
        )
        filterVisitsByDate()
    }

    private fun filterVisitsByDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.parse(sdf.format(Date())) // Ngày hiện tại: 15/04/2025

        val visitsBefore = medicalVisits.filter { visit ->
            val visitDate = sdf.parse(visit.date)
            visitDate != null && visitDate.before(currentDate)
        }
        val visitsAfter = medicalVisits.filter { visit ->
            val visitDate = sdf.parse(visit.date)
            visitDate != null && (visitDate.after(currentDate) || visitDate == currentDate)
        }

        adapterBefore.submitList(visitsBefore.toMutableList())
        adapterAfter.submitList(visitsAfter.toMutableList())
    }

    private fun setupSearch() {
        val etSearch: EditText = requireView().findViewById(R.id.et_search)
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().trim().lowercase()
                if (query.isEmpty()) {
                    filterVisitsByDate()
                } else {
                    val filteredBefore = medicalVisits.filter { visit ->
                        visit.condition.lowercase().contains(query) ||
                                visit.doctor.lowercase().contains(query) ||
                                visit.facility.lowercase().contains(query)
                    }.filter { visit ->
                        val visitDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(visit.date)
                        visitDate != null && visitDate.before(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("15/04/2025"))
                    }
                    val filteredAfter = medicalVisits.filter { visit ->
                        visit.condition.lowercase().contains(query) ||
                                visit.doctor.lowercase().contains(query) ||
                                visit.facility.lowercase().contains(query)
                    }.filter { visit ->
                        val visitDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(visit.date)
                        visitDate != null && (visitDate.after(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("15/04/2025")) || visitDate == SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("15/04/2025"))
                    }
                    adapterBefore.submitList(filteredBefore.toMutableList())
                    adapterAfter.submitList(filteredAfter.toMutableList())
                }
            }
        })
    }

    private fun openMedicalHistoryDetail(visit: MedicalVisit) {
        val bundle = Bundle().apply {
            putParcelable("medicalVisit", visit)
        }
        findNavController().navigate(R.id.action_medicineFragment_to_medicalHistoryDetailFragment, bundle)
    }
}