package com.example.healthcareproject.presentation.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginMethodViewModel @Inject constructor() : ViewModel() {

    private val _navigateToGoogleLogin = MutableLiveData<Boolean>()
    val navigateToGoogleLogin: LiveData<Boolean> = _navigateToGoogleLogin

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    private val _navigateToRegister = MutableLiveData<Boolean>()
    val navigateToRegister: LiveData<Boolean> = _navigateToRegister


    fun onGoogleLoginClicked() {
        _navigateToGoogleLogin.value = true
    }

    fun onLoginClicked() {
        _navigateToLogin.value = true
    }

    fun onRegisterClicked() {
        _navigateToRegister.value = true
    }

    fun resetNavigationStates() {
        _navigateToGoogleLogin.value = false
        _navigateToLogin.value = false
    }
}