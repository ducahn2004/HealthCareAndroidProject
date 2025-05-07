package com.example.healthcareproject.present.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.GoogleSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _googleSignInTrigger = MutableLiveData<Unit>()
    val googleSignInTrigger: LiveData<Unit> = _googleSignInTrigger

    fun onGoogleLoginClicked() {
        _error.value = null
        _googleSignInTrigger.value = Unit
    }

    fun handleGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                googleSignInUseCase(idToken)
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Google Sign-In failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setError(error: String?) {
        _error.value = error
    }

    fun resetNavigationStates() {
        _isAuthenticated.value = false
    }
}