package com.example.healthcareproject.present.auth

import androidx.navigation.NavController
import com.example.healthcareproject.R

class AuthNavigator(private val navController: NavController) {

    fun fromLoginMethodToLogin() {
        navController.navigate(R.id.action_loginMethodFragment_to_loginFragment)
    }

    fun fromLoginMethodToGoogleLogin() {
        navController.navigate(R.id.action_loginMethodFragment_to_googleLoginFragment)
    }

    fun fromLoginMethodToRegister() {
        navController.navigate(R.id.action_loginMethodFragment_to_registerFragment)
    }

    fun fromLoginToForgotPassword() {
        navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    fun fromLoginToGoogleLogin() {
        navController.navigate(R.id.action_loginFragment_to_googleLoginFragment)
    }

    fun fromLoginToLoginMethod() {
        navController.navigate(R.id.action_loginFragment_to_loginMethodFragment)
    }

    fun fromRegisterToLoginMethod() {
        navController.navigate(R.id.action_registerFragment_to_loginMethodFragment)
    }

    fun fromGoogleLoginToLoginMethod() {
        navController.navigate(R.id.action_googleLoginFragment_to_loginMethodFragment)
    }

    fun fromGoogleLoginToLogin() {
        navController.navigate(R.id.action_googleLoginFragment_to_loginFragment)
    }

    fun fromGoogleLoginToRegister() {
        navController.navigate(R.id.action_googleLoginFragment_to_registerFragment)
    }

    fun fromForgotPasswordToLoginMethod() {
        navController.navigate(R.id.action_forgotPasswordFragment_to_loginMethodFragment)
    }

    fun fromVerifyCodeToLogin() {
        navController.navigate(R.id.action_verifyCodeFragment_to_loginFragment)
    }

    fun fromForgotPasswordToVerifyCode() {
        navController.navigate(R.id.action_forgotPasswordFragment_to_verifyCodeFragment)
    }

    fun fromRegisterToVerifyCode() {
        navController.navigate(R.id.action_registerFragment_to_verifyCodeFragment)
    }

    fun fromVerifyCodeToCreateNewPassword() {
        navController.navigate(R.id.action_verifyCodeFragment_to_createNewPasswordFragment)
    }

    fun fromCreateNewPasswordToLogin() {
        navController.navigate(R.id.action_createNewPasswordFragment_to_loginFragment)
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}