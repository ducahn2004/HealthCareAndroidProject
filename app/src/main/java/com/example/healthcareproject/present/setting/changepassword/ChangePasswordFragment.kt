package com.example.healthcareproject.present.setting.changepassword

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangePasswordViewModel by viewModels()

    // Track visibility states for each password field
    private var isCurrentPasswordVisible = false
    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

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

        // Handle password visibility toggles
        binding.toggleCurrentPassword.setOnClickListener {
            isCurrentPasswordVisible = !isCurrentPasswordVisible
            updatePasswordVisibility(
                binding.etCurrentPassword,
                binding.toggleCurrentPassword,
                isCurrentPasswordVisible
            )
        }

        binding.toggleNewPassword.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            updatePasswordVisibility(
                binding.etNewPassword,
                binding.toggleNewPassword,
                isNewPasswordVisible
            )
        }

        binding.toggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            updatePasswordVisibility(
                binding.etConfirmPassword,
                binding.toggleConfirmPassword,
                isConfirmPasswordVisible
            )
        }
    }

    private fun updatePasswordVisibility(
        editText: EditText,
        toggleIcon: ImageView,
        isVisible: Boolean
    ) {
        if (isVisible) {
            // Show password
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleIcon.setImageResource(R.drawable.ic_visibility)
            toggleIcon.contentDescription = "Hide password"
        } else {
            // Hide password
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleIcon.setImageResource(R.drawable.ic_visibility_off)
            toggleIcon.contentDescription = "Show password"
        }
        // Move cursor to the end of the text
        editText.setSelection(editText.text?.length ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}