package com.example.healthcareproject.ui.auth

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var password: String = ""
    var verificationCode: String = ""
    var isLoginSuccessful: Boolean = false
}