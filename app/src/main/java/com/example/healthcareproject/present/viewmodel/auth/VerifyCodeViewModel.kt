package com.example.healthcareproject.present.viewmodel.auth

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.example.healthcareproject.domain.usecase.auth.SendVerificationCodeUseCase
import com.example.healthcareproject.domain.usecase.auth.VerifyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // Existing LiveData fields (email, authFlow, verificationCode, etc.)
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _authFlow = MutableLiveData<AuthFlow>()
    val authFlow: LiveData<AuthFlow> get() = _authFlow

    private val _verificationCode = MutableLiveData<String>()
    val verificationCode: LiveData<String> get() = _verificationCode

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

    private val _timerCount = MutableLiveData<Int>()
    val timerCount: LiveData<Int> get() = _timerCount

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> get() = _timerText

    private var timerJob: Job? = null

    fun setEmailAndAuthFlow(email: String, authFlow: AuthFlow) {
        _email.value = email
        _authFlow.value = authFlow
        sharedPreferences.edit().putString("auth_flow", authFlow.name).apply()
    }

    fun afterVerificationCodeChange(code: Editable) {
        _verificationCode.value = code.toString()
        _verificationCodeError.value = null
    }

    fun verifyCode() {
        val code = _verificationCode.value ?: ""
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
                _error.value = e.message ?: "Invalid verification code"
                _isVerified.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyEmailLink(emailLink: String) {
        val email = sharedPreferences.getString("pending_email", "") ?: run {
            _error.value = "Email is missing"
            return
        }

        if (auth.isSignInWithEmailLink(emailLink)) {
            _isLoading.value = true
            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("Verified with email link")
                        _isVerified.value = true
                        _error.value = null
                        sharedPreferences.edit().remove("pending_email").apply()
                        if (_authFlow.value == AuthFlow.FORGOT_PASSWORD) {
                            _navigateToCreateNewPassword.value = true
                        } else {
                            _navigateToLogin.value = true
                        }
                    } else {
                        Timber.e(task.exception, "Link verification failed")
                        _error.value = task.exception?.message ?: "Invalid email link"
                        _isVerified.value = false
                    }
                    _isLoading.value = false
                }
        } else {
            _error.value = "Invalid email link"
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
                _error.value = e.message ?: "Failed to send verification code"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startTimer(duration: Int = 60) {
        stopTimer()
        _timerCount.value = duration
        _timerText.value = "Resend in $duration s"
        timerJob = viewModelScope.launch {
            while (_timerCount.value!! > 0) {
                delay(1000)
                _timerCount.value = _timerCount.value!! - 1
                _timerText.value = if (_timerCount.value!! > 0) "Resend in ${_timerCount.value} s" else "Resend Code"
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerCount.value = 0
        _timerText.value = "Resend Code"
    }

    fun resetNavigationStates() {
        _navigateToCreateNewPassword.value = false
        _navigateToLogin.value = false
    }

    fun setError(error: String?) {
        _error.value = error
    }

    fun clearError() {
        _error.value = null
    }

    enum class AuthFlow {
        REGISTRATION, FORGOT_PASSWORD
    }
}