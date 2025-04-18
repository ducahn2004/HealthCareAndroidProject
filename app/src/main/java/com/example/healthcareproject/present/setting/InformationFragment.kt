package com.example.healthcareproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load thông tin từ SharedPreferences
        loadUserInformation()

        // Xử lý nút Update
        binding.btnUpdate.setOnClickListener {
            showUpdateDialog()
        }
        view.findViewById<View>(R.id.ic_back_information_to_settings).setOnClickListener {
            findNavController().navigate(R.id.action_informationFragment_to_settingsFragment)
        }
    }

    private fun loadUserInformation() {
        val sharedPrefs = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        binding.tvName.text = sharedPrefs.getString("name", "John Doe") ?: "John Doe"
        binding.tvAddress.text = sharedPrefs.getString("address", "123 Main St, City") ?: "123 Main St, City"
        binding.tvDob.text = sharedPrefs.getString("dob", "01/01/1990") ?: "01/01/1990"
        binding.tvGender.text = sharedPrefs.getString("gender", "Male") ?: "Male"
        binding.tvBloodType.text = sharedPrefs.getString("blood_type", "O+") ?: "O+"
        binding.tvPhone.text = sharedPrefs.getString("phone", "+1234567890") ?: "+1234567890"
    }

    private fun showUpdateDialog() {
        val dialog = UpdateInformationDialogFragment { updatedInfo ->
            // Cập nhật giao diện sau khi lưu
            binding.tvName.text = updatedInfo["name"]
            binding.tvAddress.text = updatedInfo["address"]
            binding.tvDob.text = updatedInfo["dob"]
            binding.tvGender.text = updatedInfo["gender"]
            binding.tvBloodType.text = updatedInfo["blood_type"]
            binding.tvPhone.text = updatedInfo["phone"]
        }
        dialog.show(parentFragmentManager, "UpdateInformationDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}