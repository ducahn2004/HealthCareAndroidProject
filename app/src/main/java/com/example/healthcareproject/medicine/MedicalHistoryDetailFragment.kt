package com.example.healthcareproject.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R

class MedicalHistoryDetailFragment : Fragment() {

    private lateinit var tvFacility: TextView
    private lateinit var tvDoctor: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvDiagnosis: TextView
    private lateinit var tvRemarks: TextView
    private lateinit var ivBack: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medical_history_detail, container, false)

        // Khởi tạo các TextView
        tvFacility = view.findViewById(R.id.tv_facility_value)
        tvDoctor = view.findViewById(R.id.tv_doctor_value)
        tvDate = view.findViewById(R.id.tv_date_value)
        tvTime = view.findViewById(R.id.tv_time_value)
        tvDiagnosis = view.findViewById(R.id.tv_diagnosis_value)
        tvRemarks = view.findViewById(R.id.tv_remarks)
        ivBack = view.findViewById(R.id.iv_back)

        // Lấy MedicalVisit từ arguments
        val medicalVisit = arguments?.getParcelable<MedicalVisit>("medicalVisit")

        // Hiển thị dữ liệu
        medicalVisit?.let {
            tvFacility.text = it.facility
            tvDoctor.text = it.doctor
            tvDate.text = it.date
            tvTime.text = it.time
            tvDiagnosis.text = "Not available" // diagnosis không tồn tại trong MedicalVisit
            tvRemarks.text = "Not available"   // doctorRemarks không tồn tại trong MedicalVisit
        }

        // Sự kiện click nút Back
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }
}