package com.example.healthcareproject.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentChangePasswordBinding
import com.example.healthcareproject.presentation.viewmodel.setting.ChangePasswordViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is logged in
        val userId = FirebaseAuth.getInstance().currentUser?.email
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        // Observe ViewModel states
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isPasswordChanged.observe(viewLifecycleOwner) { isChanged ->
            if (isChanged) {
                Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        // Handle back button
        binding.icBackChangePassword.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle change password button
        binding.btnChangePassword.setOnClickListener {
            viewModel.changePassword(userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}