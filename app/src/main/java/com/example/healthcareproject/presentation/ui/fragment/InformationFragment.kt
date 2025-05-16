package com.example.healthcareproject.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentInformationBinding
import com.example.healthcareproject.presentation.viewmodel.setting.information.InformationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InformationViewModel by viewModels()

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

        // Check if user is logged in
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        // Load user info initially
        Timber.d("Loading user info for UID: $uid")
        viewModel.loadUserInfoByUid(uid)

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Timber.e("Error in InformationFragment: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Handle back button
        binding.icBackInformation.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle update button
        binding.btnUpdate.setOnClickListener {
            UpdateInformationDialogFragment { updatedInfo ->
                // Reload user info after update
                Timber.d("Reloading user info after update for UID: $uid")
                viewModel.loadUserInfoByUid(uid)
            }.show(parentFragmentManager, "UpdateInformationDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}