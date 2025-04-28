package com.example.healthcareproject.present.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.SendVerificationCodeUseCase
import com.example.healthcareproject.domain.usecase.auth.VerifyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase
) : ViewModel() {

    // Lưu trữ email và luồng xác thực
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _authFlow = MutableLiveData<AuthFlow>()
    val authFlow: LiveData<AuthFlow> get() = _authFlow

    private val _isVerified = MutableLiveData<Boolean>()
    val isVerified: LiveData<Boolean> get() = _isVerified

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _verificationCodeError = MutableLiveData<String?>()
    val verificationCodeError: LiveData<String?> get() = _verificationCodeError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _navigateToCreateNewPassword = MutableLiveData<Boolean>()
    val navigateToCreateNewPassword: LiveData<Boolean> get() = _navigateToCreateNewPassword

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    // Timer để giới hạn thời gian gửi lại mã
    private val _timerCount = MutableLiveData<Int>()
    val timerCount: LiveData<Int> get() = _timerCount

    private var timerJob: Job? = null

    // Khởi tạo email và authFlow từ Fragment
    fun setEmailAndAuthFlow(email: String, authFlow: AuthFlow) {
        _email.value = email
        _authFlow.value = authFlow
    }

    fun verifyCode(code: String) {
        if (code.isEmpty()) {
            _verificationCodeError.value = "Verification code cannot be empty"
            return
        }

        val email = _email.value ?: run {
            _error.value = "Email is missing"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                verifyCodeUseCase(email, code)
                _isVerified.value = true
                _error.value = null
                _verificationCodeError.value = null
                if (_authFlow.value == AuthFlow.FORGOT_PASSWORD) {
                    _navigateToCreateNewPassword.value = true
                } else {
                    _navigateToLogin.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isVerified.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendVerificationCode() {
        val email = _email.value ?: run {
            _error.value = "Email is missing"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                sendVerificationCodeUseCase(email)
                _error.value = null
                startTimer()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startTimer(duration: Int = 60) {
        stopTimer()
        _timerCount.value = duration
        timerJob = viewModelScope.launch {
            while (_timerCount.value!! > 0) {
                delay(1000)
                _timerCount.value = _timerCount.value!! - 1
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerCount.value = 0
    }

    fun resetNavigationStates() {
        _navigateToCreateNewPassword.value = false
        _navigateToLogin.value = false
    }

    fun setError(error: String?) {
        _error.value = error
    }

    enum class AuthFlow {
        REGISTRATION, FORGOT_PASSWORD
    }
}