//package com.example.healthcareproject.present.medicine
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import com.example.healthcareproject.databinding.FragmentAddMedicalHistoryBinding
//import dagger.hilt.android.AndroidEntryPoint
//import java.time.LocalDate
//
//@AndroidEntryPoint
//class AddMedicalHistoryFragment : Fragment() {
//
//    private var _binding: FragmentAddMedicalHistoryBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: AddMedicalHistoryViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAddMedicalHistoryBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupUI()
//        observeViewModel()
//    }
//
//    private fun setupUI() {
//        binding.btnSave.setOnClickListener {
//            val patientName = binding.etPatientName.text.toString()
//            val visitReason = binding.etVisitReason.text.toString()
//            val doctorName = binding.etDoctorName.text.toString()
//            val diagnosis = binding.etDiagnosis.text.toString()
//            val visitDate = LocalDate.now() // Replace with actual date picker logic
//
//            if (patientName.isBlank() || visitReason.isBlank() || doctorName.isBlank()) {
//                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            viewModel.addMedicalVisit(
//                patientName = patientName,
//                visitReason = visitReason,
//                visitDate = visitDate,
//                doctorName = doctorName,
//                diagnosis = diagnosis
//            )
//        }
//    }
//
//    private fun observeViewModel() {
//        viewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
//            if (isSuccess) {
//                Toast.makeText(context, "Medical visit added successfully", Toast.LENGTH_SHORT).show()
//                requireActivity().onBackPressed()
//            }
//        }
//
//        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
//            errorMessage?.let {
//                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}