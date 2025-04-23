package com.example.healthcareproject.present.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.CreateUserUseCase
import com.example.healthcareproject.domain.usecase.LoginUserUseCase
import com.example.healthcareproject.domain.usecase.VerifyCodeUseCase
import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val userFirebaseDataSource: UserFirebaseDataSource
) : ViewModel() {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isCodeVerified = MutableLiveData<Boolean>()
    val isCodeVerified: LiveData<Boolean> = _isCodeVerified

    private val _verificationCodeError = MutableLiveData<String?>()
    val verificationCodeError: LiveData<String?> = _verificationCodeError

    fun handleGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userFirebaseDataSource.googleSignIn(idToken)
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Google Sign-In failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyCode(email: String, verificationCode: String) {
        _verificationCodeError.value = null
        _error.value = null

        if (verificationCode.isBlank()) {
            _verificationCodeError.value = "Verification code is required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                verifyCodeUseCase(email, verificationCode)
                _isCodeVerified.value = true
            } catch (e: Exception) {
                _verificationCodeError.value = e.message ?: "Invalid verification code"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                loginUserUseCase(email, password)
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) {
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                createUserUseCase(
                    email = email,
                    password = password,
                    name = name,
                    address = address,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    bloodType = bloodType,
                    phone = phone
                )
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}