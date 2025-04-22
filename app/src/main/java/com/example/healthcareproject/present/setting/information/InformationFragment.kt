package com.example.healthcareproject.present.setting.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.present.setting.information.UpdateInformationDialogFragment
import com.example.healthcareproject.present.setting.information.UpdateInformationViewModel
import com.example.healthcareproject.databinding.FragmentInformationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user info
        viewModel.loadUserInfo("user123") // Replace with actual userId

        // Observe ViewModel states
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Optionally show a loading indicator
            // For example, you can add a ProgressBar to the layout and toggle its visibility
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Handle back button
        binding.icBackInformationToSettings.setOnClickListener {
            findNavController().navigateUp() // Navigate back to previous fragment
        }

        // Handle update button
        binding.btnUpdate.setOnClickListener {
            val dialog = UpdateInformationDialogFragment { updatedInfo ->
                // Optionally handle the updated info callback
                Toast.makeText(requireContext(), "Information updated", Toast.LENGTH_SHORT).show()
            }
            dialog.show(parentFragmentManager, "UpdateInformationDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}