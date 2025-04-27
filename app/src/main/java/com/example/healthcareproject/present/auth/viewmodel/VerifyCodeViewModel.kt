package com.example.healthcareproject.present.auth.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.domain.usecase.VerifyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val userFirebaseDataSource: UserFirebaseDataSource
) : ViewModel() {

    enum class AuthFlow {
        REGISTRATION, FORGOT_PASSWORD
    }

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _verificationCode = MutableLiveData<String>()
    val verificationCode: LiveData<String> = _verificationCode

    private val _isCodeVerified = MutableLiveData<Boolean>()
    val isCodeVerified: LiveData<Boolean> = _isCodeVerified

    private val _verificationCodeError = MutableLiveData<String?>()
    val verificationCodeError: LiveData<String?> = _verificationCodeError

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _timerText = MutableLiveData<String>("00:59")
    val timerText: LiveData<String> = _timerText

    private val _authFlow = MutableLiveData<AuthFlow>()
    val authFlow: LiveData<AuthFlow> = _authFlow

    private var countDownTimer: CountDownTimer? = null

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setVerificationCode(value: String) {
        _verificationCode.value = value
    }

    fun setAuthFlow(flow: AuthFlow) {
        _authFlow.value = flow
    }

    fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(59000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                _timerText.value = String.format("%02d:%02d", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                _timerText.value = "00:00"
            }
        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    fun setError(error: String?) {
        _error.value = error
    }

    fun verifyCode() {
        val emailValue = email.value ?: ""
        val codeValue = verificationCode.value ?: ""

        _verificationCodeError.value = null
        _error.value = null

        if (codeValue.isBlank()) {
            _verificationCodeError.value = "Verification code is required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                verifyCodeUseCase(emailValue, codeValue)
                _isCodeVerified.value = true
            } catch (e: Exception) {
                _verificationCodeError.value = e.message ?: "Invalid verification code"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendVerificationCode() {
        val emailValue = email.value ?: ""
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                userFirebaseDataSource.sendVerificationCode(emailValue)
                _error.value = "Verification code sent. Please check your email."
                startTimer()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send verification code"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetNavigationStates() {
        _isCodeVerified.value = false
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}