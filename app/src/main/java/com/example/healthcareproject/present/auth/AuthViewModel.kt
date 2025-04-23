package com.example.healthcareproject.present.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.usecase.CreateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    // Form fields for registration (String properties for two-way binding)
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

    // LiveData for observing changes (used for validation and UI updates)
    private val _name = MutableLiveData<String>("")
    val nameLiveData: LiveData<String> get() = _name

    private val _email = MutableLiveData<String>("")
    val emailLiveData: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>("")
    val passwordLiveData: LiveData<String> get() = _password

    private val _confirmPassword = MutableLiveData<String>("")
    val confirmPasswordLiveData: LiveData<String> get() = _confirmPassword

    private val _dateOfBirth = MutableLiveData<String>("")
    val dateOfBirthLiveData: LiveData<String> get() = _dateOfBirth

    private val _gender = MutableLiveData<String>("")
    val genderLiveData: LiveData<String> get() = _gender

    private val _bloodType = MutableLiveData<String>("")
    val bloodTypeLiveData: LiveData<String> get() = _bloodType

    // Error messages for validation
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

    // State for navigation or UI updates
    private val _isRegistered = MutableLiveData<Boolean>(false)
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    // Handle registration
    fun register() {
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
                    address = null, // Address is optional
                    dateOfBirth = dateOfBirth, // Already in DD/MM/YYYY format
                    gender = gender,
                    bloodType = bloodType,
                    phone = "" // Not collected in the form
                )
                _isRegistered.value = true // Trigger navigation
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}