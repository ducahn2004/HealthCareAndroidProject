package com.example.healthcareproject.present.auth.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.LoginUserUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loginResult = MutableLiveData<String?>()
    val loginResult: LiveData<String?> = _loginResult

    private val _navigateToForgotPassword = MutableLiveData<Boolean>()
    val navigateToForgotPassword: LiveData<Boolean> = _navigateToForgotPassword

    private val _navigateToGoogleLogin = MutableLiveData<Boolean>()
    val navigateToGoogleLogin: LiveData<Boolean> = _navigateToGoogleLogin

    fun navigateToGoogleLogin() {
        _navigateToGoogleLogin.value = true
    }

    fun afterEmailChange(email: Editable) {
        _email.value = email.toString()
    }

    fun afterPasswordChange(password: Editable) {
        _password.value = password.toString()
    }

    fun resetNavigationStates() {
        _navigateToForgotPassword.value = false
        _navigateToGoogleLogin.value = false
    }

    fun onLoginClicked() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""

        _emailError.value = null
        _passwordError.value = null

        var isValid = true
        if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Invalid email format"
            isValid = false
        }
        if (passwordValue.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        }

        if (isValid) {
            login(emailValue, passwordValue)
        }
    }

    fun navigateToForgotPassword() {
        _navigateToForgotPassword.value = true
    }

    private fun login(email: String, password: String) {
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val uid = loginUserUseCase(email, password)

                val user = auth.currentUser
                val providerData = user?.providerData

                val isGoogleProviderLinked = providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true

                if (isGoogleProviderLinked) {
                    // Show error message in English
                    _error.value = "This account is linked with Google. Please log in using Google."
                    _loginResult.value = null // Reset login result
                } else {
                    _loginResult.value = uid
                    _error.value = null
                    _emailError.value = null
                    _passwordError.value = null
                }
            } catch (e: Exception) {
                // Handle error in case of failure
                _error.value = e.message ?: "Login failed"
                _loginResult.value = null
            } finally {
                // Ensure loading state is updated
                _isLoading.value = false
            }
        }
    }



}