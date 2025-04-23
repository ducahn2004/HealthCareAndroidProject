package com.example.healthcareproject.present.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.usecase.CreateUserUseCase
import com.example.healthcareproject.domain.usecase.GoogleSignInUseCase
import com.example.healthcareproject.domain.usecase.LoginUserUseCase
import com.example.healthcareproject.domain.usecase.VerifyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase // New use case for Google Sign-In
) : ViewModel() {

    // Form fields for registration, login, and verification (unchanged)
    var name: String = ""
        set(value) {
            field = value
            _name.value = value
            _nameError.value = null
        }

    var email: String = ""
        set(value) {
            field = value
            _email.value = value
            _emailError.value = null
        }

    var password: String = ""
        set(value) {
            field = value
            _password.value = value
            _passwordError.value = null
        }

    var confirmPassword: String = ""
        set(value) {
            field = value
            _confirmPassword.value = value
            _confirmPasswordError.value = null
        }

    var dateOfBirth: String = ""
        set(value) {
            field = value
            _dateOfBirth.value = value
            _dateOfBirthError.value = null
        }

    var gender: String = ""
        set(value) {
            field = value
            _gender.value = value
            _genderError.value = null
        }

    var bloodType: String = ""
        set(value) {
            field = value
            _bloodType.value = value
            _bloodTypeError.value = null
        }

    var verificationCode: String = ""
        set(value) {
            field = value
            _verificationCode.value = value
            _verificationCodeError.value = null
        }

    // LiveData for observing changes (unchanged)
    private val _name = MutableLiveData<String>("")
    val nameLiveData: LiveData<String> get() = _name

    private val _email = MutableLiveData<String>("")
    val emailLiveData: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>("")
    val passwordLiveData: LiveData<String> get() = _password

    private val _confirmPassword = MutableLiveData<String>("")
    val confirmPasswordLiveData: LiveData<String> get() = _confirmPassword

    private val _dateOfBirth = MutableLiveData<String>("")
    val dateOfBirthLiveData: LiveData<String> get() = _dateOfDateOfBirth

    private val _gender = MutableLiveData<String>("")
    val genderLiveData: LiveData<String> get() = _gender

    private val _bloodType = MutableLiveData<String>("")
    val bloodTypeLiveData: LiveData<String> get() = _bloodType

    private val _verificationCode = MutableLiveData<String>("")
    val verificationCodeLiveData: LiveData<String> get() = _verificationCode

    // Error messages for validation (unchanged)
    private val _nameError = MutableLiveData<String?>(null)
    val nameError: LiveData<String?> get() = _nameError

    private val _emailError = MutableLiveData<String?>(null)
    val emailError: LiveData<String?> get() = _emailError

    private val _passwordError = MutableLiveData<String?>(null)
    val passwordError: LiveData<String?> get() = _passwordError

    private val _confirmPasswordError = MutableLiveData<String?>(null)
    val confirmPasswordError: LiveData<String?> get() = _confirmPasswordError

    private val _dateOfBirthError = MutableLiveData<String?>(null)
    val dateOfBirthError: LiveData<String?> get() = _dateOfBirthError

    private val _genderError = MutableLiveData<String?>(null)
    val genderError: LiveData<String?> get() = _genderError

    private val _bloodTypeError = MutableLiveData<String?>(null)
    val bloodTypeError: LiveData<String?> get() = _bloodTypeError

    private val _verificationCodeError = MutableLiveData<String?>(null)
    val verificationCodeError: LiveData<String?> get() = _verificationCodeError

    // State for navigation or UI updates (unchanged)
    private val _isAuthenticated = MutableLiveData<Boolean>(false)
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    private val _isCodeVerified = MutableLiveData<Boolean>(false)
    val isCodeVerified: LiveData<Boolean> get() = _isCodeVerified

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    // Navigation events for LoginMethodFragment (unchanged)
    private val _navigateToRegister = MutableLiveData<Boolean>(false)
    val navigateToRegister: LiveData<Boolean> get() = _navigateToRegister

    private val _navigateToLogin = MutableLiveData<Boolean>(false)
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    private val _navigateToGoogleLogin = MutableLiveData<Boolean>(false)
    val navigateToGoogleLogin: LiveData<Boolean> get() = _navigateToGoogleLogin

    // Track the current authentication flow (unchanged)
    private val _authFlow = MutableLiveData<AuthFlow>(AuthFlow.NONE)
    val authFlow: LiveData<AuthFlow> get() = _authFlow

    enum class AuthFlow {
        NONE, REGISTRATION, LOGIN, FORGOT_PASSWORD, GOOGLE_LOGIN // Added GOOGLE_LOGIN
    }

    // Trigger navigation to register (unchanged)
    fun onRegisterClicked() {
        _navigateToRegister.value = true
    }

    // Trigger navigation to login (unchanged)
    fun onLoginClicked() {
        _navigateToLogin.value = true
    }

    // Trigger Google Sign-In process
    fun onGoogleLoginClicked() {
        _authFlow.value = AuthFlow.GOOGLE_LOGIN
        _isLoading.value = true
        // The actual Google Sign-In intent is handled in GoogleLoginFragment
    }

    // Handle Google Sign-In result
    fun handleGoogleSignIn(idToken: String) {
        viewModelScope.launch {
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

    // Handle registration (unchanged)
    fun register() {
        _authFlow.value = AuthFlow.REGISTRATION
        // Reset errors
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _dateOfBirthError.value = null
        _genderError.value = null
        _bloodTypeError.value = null
        _error.value = null

        // Validation
        var isValid = true

        if (name.isBlank()) {
            _nameError.value = "Name is required"
            isValid = false
        }

        if (email.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Invalid email address"
            isValid = false
        }

        if (password.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            _passwordError.value = "Password must be at least 8 characters"
            isValid = false
        }

        if (confirmPassword.isBlank()) {
            _confirmPasswordError.value = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            _confirmPasswordError.value = "Passwords do not match"
            isValid = false
        }

        // Validate date of birth
        var parsedDateOfBirth: LocalDate? = null
        if (dateOfBirth.isBlank()) {
            _dateOfBirthError.value = "Date of birth is required"
            isValid = false
        } else {
            try {
                parsedDateOfBirth = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                if (parsedDateOfBirth.isAfter(LocalDate.now())) {
                    _dateOfBirthError.value = "Date of birth cannot be in the future"
                    isValid = false
                }
            } catch (e: DateTimeParseException) {
                _dateOfBirthError.value = "Invalid date format (use DD/MM/YYYY)"
                isValid = false
            }
        }

        // Validate gender
        if (gender.isBlank()) {
            _genderError.value = "Gender is required"
            isValid = false
        } else {
            try {
                Gender.valueOf(gender)
            } catch (e: IllegalArgumentException) {
                _genderError.value = "Invalid gender"
                isValid = false
            }
        }

        // Validate blood type
        if (bloodType.isBlank()) {
            _bloodTypeError.value = "Blood type is required"
            isValid = false
        } else {
            try {
                BloodType.valueOf(bloodType)
            } catch (e: IllegalArgumentException) {
                _bloodTypeError.value = "Invalid blood type"
                isValid = false
            }
        }

        if (!isValid) return

        // Create user
        viewModelScope.launch {
            _isLoading.value = true
            try {
                createUserUseCase(
                    userId = email,
                    password = password,
                    name = name,
                    address = null,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    bloodType = bloodType,
                    phone = ""
                )
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Handle login (unchanged)
    fun login() {
        _authFlow.value = AuthFlow.LOGIN
        // Reset errors
        _emailError.value = null
        _passwordError.value = null
        _error.value = null

        // Validation
        var isValid = true

        if (email.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Invalid email address"
            isValid = false
        }

        if (password.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        }

        if (!isValid) return

        // Authenticate user
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loginUserUseCase(
                    userId = email,
                    password = password
                )
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Handle code verification (unchanged)
    fun verifyCode() {
        // Reset errors
        _verificationCodeError.value = null
        _error.value = null

        // Validation
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

    // Reset navigation states (updated to include Google login)
    fun resetNavigationStates() {
        _navigateToRegister.value = false
        _navigateToLogin.value = false
        _navigateToGoogleLogin.value = false
        _isAuthenticated.value = false
        _isCodeVerified.value = false
        _authFlow.value = AuthFlow.NONE
    }
}