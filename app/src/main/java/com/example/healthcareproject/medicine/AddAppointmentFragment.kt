package com.example.healthcareproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.medicine.MedicalVisit
import com.example.healthcareproject.R

class AddAppointmentFragment : Fragment() {

    private lateinit var etCondition: EditText
    private lateinit var etDoctor: EditText
    private lateinit var etFacility: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etLocation: EditText
    private lateinit var btnSave: Button
    private lateinit var ivBack: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_appointment, container, false)

        // Khởi tạo các view
        etCondition = view.findViewById(R.id.et_condition)
        etDoctor = view.findViewById(R.id.et_doctor)
        etFacility = view.findViewById(R.id.et_facility)
        etDate = view.findViewById(R.id.et_date)
        etTime = view.findViewById(R.id.et_time)
        etLocation = view.findViewById(R.id.et_location)
        btnSave = view.findViewById(R.id.btn_save)
        ivBack = view.findViewById(R.id.iv_back)

        // Sự kiện click nút Back
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Sự kiện click nút Save
        btnSave.setOnClickListener {
            val condition = etCondition.text.toString().trim()
            val doctor = etDoctor.text.toString().trim()
            val facility = etFacility.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val location = etLocation.text.toString().trim()

            if (condition.isNotEmpty() && doctor.isNotEmpty() && facility.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                val newVisit = MedicalVisit(
                    condition = condition,
                    doctor = doctor,
                    facility = facility,
                    date = date,
                    time = time,
                    location = if (location.isNotEmpty()) location else null
                )

                // Truyền dữ liệu về MedicineFragment thông qua setFragmentResult
                setFragmentResult("requestKey", Bundle().apply {
                    putParcelable("newVisit", newVisit)
                })

                // Quay lại MedicineFragment
                findNavController().navigateUp()
            } else {
                // Hiển thị thông báo lỗi nếu các trường bắt buộc trống
                etCondition.error = if (condition.isEmpty()) "Required" else null
                etDoctor.error = if (doctor.isEmpty()) "Required" else null
                etFacility.error = if (facility.isEmpty()) "Required" else null
                etDate.error = if (date.isEmpty()) "Required" else null
                etTime.error = if (time.isEmpty()) "Required" else null
            }
        }

        return view
    }
}