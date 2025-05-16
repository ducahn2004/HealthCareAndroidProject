package com.example.healthcareproject.presentation.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.GoogleSignInUseCase
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
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
                Timber.d("Attempting Google Sign-In with ID token")
                googleSignInUseCase(idToken)
                _isAuthenticated.value = true
                _error.value = null
                Timber.d("Google Sign-In successful")
            } catch (e: FirebaseAuthInvalidUserException) {
                Timber.e(e, "Google Sign-In blocked: Account not registered")
                _error.value = "Account not registered. Please register first using email and password."
            } catch (e: FirebaseAuthUserCollisionException) {
                Timber.e(e, "Google Sign-In failed: Email linked with email/password")
                _error.value = "This email is linked with email/password. Please log in using email or link your Google account."
            } catch (e: Exception) {
                Timber.e(e, "Google Sign-In failed: Unknown error")
                _error.value = e.message ?: "Google Sign-In failed."
            } finally {
                _isLoading.value = false
                Timber.d("Google Sign-In attempt completed")
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