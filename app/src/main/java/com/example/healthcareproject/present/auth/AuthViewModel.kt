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

    // Form fields
    private val _firstName = MutableLiveData<String>("")
    val firstName: LiveData<String> get() = _firstName

    private val _lastName = MutableLiveData<String>("")
    val lastName: LiveData<String> get() = _lastName

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>("")
    val password: LiveData<String> get() = _password

    private val _confirmPassword = MutableLiveData<String>("")
    val confirmPassword: LiveData<String> get() = _confirmPassword

    private val _dateOfBirth = MutableLiveData<LocalDate?>(null)
    val dateOfBirth: LiveData<LocalDate?> get() = _dateOfBirth
    val dateOfBirthString: LiveData<String> = MutableLiveData<String>().apply {
        value = ""
    } // For UI display

    private val _gender = MutableLiveData<Gender?>(null)
    val gender: LiveData<Gender?> get() = _gender

    private val _bloodType = MutableLiveData<BloodType?>(null)
    val bloodType: LiveData<BloodType?> get() = _bloodType

    // Error messages
    private val _firstNameError = MutableLiveData<String?>(null)
    val firstNameError: LiveData<String?> get() = _firstNameError

    private val _lastNameError = MutableLiveData<String?>(null)
    val lastNameError: LiveData<String?> get() = _lastNameError

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

    // UI states
    private val _isRegistered = MutableLiveData<Boolean>(false)
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    private val _navigateBack = MutableLiveData<Boolean>(false)
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    // Setters
    fun setFirstName(value: String) {
        _firstName.value = value
        _firstNameError.value = null
    }

    fun setLastName(value: String) {
        _lastName.value = value
        _lastNameError.value = null
    }

    fun setEmail(value: String) {
        _email.value = value
        _emailError.value = null
    }

    fun setPassword(value: String) {
        _password.value = value
        _passwordError.value = null
    }

    fun setConfirmPassword(value: String) {
        _confirmPassword.value = value
        _confirmPasswordError.value = null
    }

    fun setDateOfBirth(date: LocalDate?) {
        _dateOfBirth.value = date
        _dateOfBirthError.value = null
        (dateOfBirthString as MutableLiveData).value = date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
    }

    fun setGender(gender: Gender?) {
        _gender.value = gender
        _genderError.value = null
    }

    fun setBloodType(bloodType: BloodType?) {
        _bloodType.value = bloodType
        _bloodTypeError.value = null
    }

    fun onBackClicked() {
        _navigateBack.value = true
    }

    // Handle registration
    fun register() {
        // Reset errors
        _firstNameError.value = null
        _lastNameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _dateOfBirthError.value = null
        _genderError.value = null
        _bloodTypeError.value = null
        _error.value = null

        val firstName = _firstName.value ?: ""
        val lastName = _lastName.value ?: ""
        val email = _email.value ?: ""
        val password = _password.value ?: ""
        val confirmPassword = _confirmPassword.value ?: ""
        val dateOfBirth = _dateOfBirth.value
        val gender = _gender.value
        val bloodType = _bloodType.value

        // Validation
        var isValid = true

        if (firstName.isBlank()) {
            _firstNameError.value = "First name is required"
            isValid = false
        }

        if (lastName.isBlank()) {
            _lastNameError.value = "Last name is required"
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

        if (dateOfBirth == null) {
            _dateOfBirthError.value = "Date of birth is required"
            isValid = false
        } else if (dateOfBirth.isAfter(LocalDate.now())) {
            _dateOfBirthError.value = "Date of birth cannot be in the future"
            isValid = false
        }

        if (gender == null) {
            _genderError.value = "Gender is required"
            isValid = false
        }

        if (bloodType == null) {
            _bloodTypeError.value = "Blood type is required"
            isValid = false
        }

        if (!isValid) return

        // Create user
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fullName = "$firstName $lastName"
                createUserUseCase(
                    userId = email,
                    password = password,
                    name = fullName,
                    address = null,
                    dateOfBirth = dateOfBirth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    gender = gender.name,
                    bloodType = bloodType.name,
                    phone = ""
                )
                _isRegistered.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
}