package com.example.healthcareproject.presentation.viewmodel.auth

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)


    private val _isCodeSent = MutableLiveData<Boolean>()
    val isCodeSent: LiveData<Boolean> get() = _isCodeSent

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _navigateToVerifyCode = MutableLiveData<Boolean>()
    val navigateToVerifyCode: LiveData<Boolean> = _navigateToVerifyCode

    private val _resetRequestSuccess = MutableLiveData<String?>()
    val resetRequestSuccess: LiveData<String?> = _resetRequestSuccess

    fun afterEmailChange(value: Editable){
        _email.value = value.toString()
    }

    private fun getActionCodeSettings(): ActionCodeSettings {
        return ActionCodeSettings.newBuilder()
            .setUrl("https://heart-careproject.firebaseapp.com/resetPassword")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.healthcareproject", true, "1")
            .build()
    }

    fun onResetPasswordClicked() {
        val emailValue = email.value ?: ""
        if (emailValue.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = if (emailValue.isBlank()) "Email is required" else "Invalid email format"
            return
        }

        _isLoading.value = true
        auth.sendSignInLinkToEmail(emailValue, getActionCodeSettings())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Reset email link sent to $emailValue")
                    _resetRequestSuccess.value = "Check your email for the reset link!"
                    _navigateToVerifyCode.value = true
                    sharedPreferences.edit()
                        .putString("pending_email", emailValue)
                        .putString("auth_flow", "FORGOT_PASSWORD")
                        .apply()
                } else {
                    Timber.e(task.exception, "Failed to send reset link")
                    _error.value = task.exception?.message ?: "Failed to send reset link"
                }
                _isLoading.value = false
            }
    }

    fun resetNavigationStates() {
        _navigateToVerifyCode.value = false
        _resetRequestSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}