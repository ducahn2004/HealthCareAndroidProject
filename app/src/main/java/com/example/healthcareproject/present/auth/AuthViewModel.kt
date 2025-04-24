package com.example.healthcareproject.present.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.CreateUserUseCase
import com.example.healthcareproject.domain.usecase.LoginUserUseCase
import com.example.healthcareproject.domain.usecase.SendPasswordResetEmailUseCase
import com.example.healthcareproject.domain.usecase.UpdatePasswordUseCase
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
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val userFirebaseDataSource: UserFirebaseDataSource
) : ViewModel() {

    enum class AuthFlow {
        REGISTRATION,
        LOGIN,
        FORGOT_PASSWORD
    }

    // Track current authentication flow
    private val _authFlow = MutableLiveData<AuthFlow>()
    val authFlow: LiveData<AuthFlow> = _authFlow

    // Set authentication flow
    fun setAuthFlow(flow: AuthFlow) {
        _authFlow.value = flow
    }

    private val _navigateToGoogleLogin = MutableLiveData<Boolean>()
    val navigateToGoogleLogin: LiveData<Boolean> = _navigateToGoogleLogin

    fun onGoogleLoginClicked() {
        _navigateToGoogleLogin.value = true
    }

    fun resetNavigationStates() {
        _navigateToGoogleLogin.value = false
    }

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setDateOfBirth(value: String) {
        _dateOfBirth.value = value
    }

    fun setConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    // Authentication state
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

    // Form fields
    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name
    fun onNameChanged(value: String) {
        _name.value = value
    }

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> = _dateOfBirth

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> = _gender

    private val _bloodType = MutableLiveData<String>()
    val bloodType: LiveData<String> = _bloodType

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    private val _address = MutableLiveData<String?>()
    val address: LiveData<String?> = _address

    private val _verificationCode = MutableLiveData<String>()
    val verificationCode: LiveData<String> = _verificationCode
    fun onVerificationCodeChanged(value: String) {
        _verificationCode.value = value
    }

    // Error fields
    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError

    private val _dateOfBirthError = MutableLiveData<String?>()
    val dateOfBirthError: LiveData<String?> = _dateOfBirthError

    private val _genderError = MutableLiveData<String?>()
    val genderError: LiveData<String?> = _genderError

    private val _bloodTypeError = MutableLiveData<String?>()
    val bloodTypeError: LiveData<String?> = _bloodTypeError

    fun onLoginClicked() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""

        // Reset errors
        _emailError.value = null
        _passwordError.value = null

        // Validate inputs
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

    fun onRegisterClicked() {
        val nameValue = name.value ?: ""
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""
        val confirmPasswordValue = confirmPassword.value ?: ""
        val dateOfBirthValue = dateOfBirth.value ?: ""
        val genderValue = gender.value ?: ""
        val bloodTypeValue = bloodType.value ?: ""
        val phoneValue = phone.value ?: ""
        val addressValue = address.value

        // Reset errors
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _dateOfBirthError.value = null
        _genderError.value = null
        _bloodTypeError.value = null

        // Validate inputs
        var isValid = true
        if (nameValue.isBlank()) {
            _nameError.value = "Name is required"
            isValid = false
        }
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
        } else if (passwordValue.length < 8) {
            _passwordError.value = "Password must be at least 8 characters"
            isValid = false
        }
        if (confirmPasswordValue != passwordValue) {
            _confirmPasswordError.value = "Passwords do not match"
            isValid = false
        }
        if (dateOfBirthValue.isBlank()) {
            _dateOfBirthError.value = "Date of birth is required"
            isValid = false
        }
        if (genderValue.isBlank()) {
            _genderError.value = "Gender is required"
            isValid = false
        }
        if (bloodTypeValue.isBlank()) {
            _bloodTypeError.value = "Blood type is required"
            isValid = false
        }

        if (!isValid) {
            return
        }

        register(
            email = emailValue,
            password = passwordValue,
            name = nameValue,
            address = addressValue,
            dateOfBirth = dateOfBirthValue,
            gender = genderValue,
            bloodType = bloodTypeValue,
            phone = phoneValue
        )
    }

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

    fun sendPasswordResetEmail(email: String) {
        _emailError.value = null
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                sendPasswordResetEmailUseCase(email)
                _email.value = email
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send reset email"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                updatePasswordUseCase(newPassword)
                _password.value = newPassword
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update password"
            } finally {
                _isLoading.value = false
            }
        }
    }
}