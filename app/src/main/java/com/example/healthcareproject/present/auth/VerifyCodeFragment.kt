package com.example.healthcareproject.present.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.healthcareproject.databinding.FragmentVerifyCodeBinding
import com.example.healthcareproject.present.MainActivity
import com.example.healthcareproject.present.auth.viewmodel.VerifyCodeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    private var _binding: FragmentVerifyCodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerifyCodeViewModel by viewModels()
    private lateinit var navigator: AuthNavigator
    private val args: VerifyCodeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy email và authFlow từ arguments
        val email = args.email
        val authFlow = when (args.authFlow) {
            "REGISTRATION" -> VerifyCodeViewModel.AuthFlow.REGISTRATION
            "FORGOT_PASSWORD" -> VerifyCodeViewModel.AuthFlow.FORGOT_PASSWORD
            else -> null
        }

        if (email.isNullOrEmpty() || authFlow == null) {
            Timber.e("Email or authFlow is missing: email=$email, authFlow=$authFlow")
            Snackbar.make(binding.root, "Invalid verification data", Snackbar.LENGTH_LONG).show()
            navigator.navigateUp()
            return
        }

        // Khởi tạo ViewModel với email và authFlow
        viewModel.setEmailAndAuthFlow(email, authFlow)
        Timber.d("VerifyCodeFragment: email=$email, authFlow=$authFlow")

        // Bắt đầu timer
        viewModel.startTimer()

        setupClickListeners()
        observeViewModelState()
    }

    private fun setupClickListeners() {
        // Xử lý nút back
        binding.btnBackVerifyCode.setOnClickListener {
            navigator.navigateUp()
        }

        // Xử lý gửi lại mã xác nhận
        binding.tvResend.setOnClickListener {
            if (viewModel.timerCount.value == 0) {
                viewModel.sendVerificationCode()
                Snackbar.make(binding.root, "Code resent to ${viewModel.email.value}", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Please wait ${viewModel.timerCount.value}s to resend", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModelState() {
        // Quan sát trạng thái xác minh
        viewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified) {
                Timber.d("Code verified, authFlow=${viewModel.authFlow.value}")
                when (viewModel.authFlow.value) {
                    VerifyCodeViewModel.AuthFlow.REGISTRATION -> {
                        navigator.fromVerifyCodeToLogin()
                    }
                    VerifyCodeViewModel.AuthFlow.FORGOT_PASSWORD -> {
                        navigator.fromVerifyCodeToCreateNewPassword()
                    }
                    null -> {
                        Timber.e("AuthFlow is null")
                        Snackbar.make(binding.root, "Invalid authentication flow", Snackbar.LENGTH_LONG).show()
                    }
                }
                viewModel.resetNavigationStates()
            }
        }

        // Quan sát lỗi
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Timber.e("Error in VerifyCodeFragment: $error")
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                viewModel.setError(null)
            }
        }

        // Quan sát lỗi mã xác nhận
        viewModel.verificationCodeError.observe(viewLifecycleOwner) { error ->
            binding.etVerificationCode.error = error
        }

        // Quan sát trạng thái tải
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnVerify.isEnabled = !isLoading
            binding.tvResend.isEnabled = !isLoading
        }

        // Quan sát timer
        viewModel.timerCount.observe(viewLifecycleOwner) { count ->
            binding.tvResend.isEnabled = count == 0
            binding.tvResend.text = if (count > 0) "Resend in ${count}s" else "Resend Code"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopTimer()
        _binding = null
    }
}