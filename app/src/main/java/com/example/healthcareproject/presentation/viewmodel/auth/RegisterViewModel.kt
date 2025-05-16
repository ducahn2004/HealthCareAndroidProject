package com.example.healthcareproject.presentation.viewmodel.auth

import android.content.SharedPreferences
import android.text.Editable
import android.util.Patterns
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.LinkGoogleCredentialUseCase
import com.example.healthcareproject.domain.usecase.user.CreateUserUseCase
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val linkGoogleCredentialUseCase: LinkGoogleCredentialUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name

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

    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError

    private val _addressError = MutableLiveData<String?>()
    val addressError: LiveData<String?> = _addressError

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _registerResult = MutableLiveData<String?>() // Lưu UID
    val registerResult: LiveData<String?> = _registerResult

    private val _registrationSuccess = MutableLiveData<String?>()
    val registrationSuccess: LiveData<String?> = _registrationSuccess

    private val _emailLinkSent = MutableLiveData<Boolean>()
    val emailLinkSent: LiveData<Boolean> = _emailLinkSent

    /**
     * Updates the name field.
     */
    fun afterNameChange(value: Editable) {
        _name.value = value.toString()
    }

    /**
     * Updates the email field.
     */
    fun afterEmailChange(value: Editable) {
        _email.value = value.toString()
    }

    /**
     * Updates the password field.
     */
    fun afterPasswordChange(value: Editable) {
        _password.value = value.toString()
    }

    /**
     * Updates the confirm password field.
     */
    fun afterConfirmPasswordChange(value: Editable) {
        _confirmPassword.value = value.toString()
    }

    /**
     * Updates the phone field.
     */
    fun afterPhoneChange(value: Editable) {
        _phone.value = value.toString()
    }

    /**
     * Updates the address field.
     */
    fun afterAddressChange(value: Editable) {
        _address.value = value.toString()
    }

    /**
     * Updates the date of birth field.
     */
    fun afterDateOfBirthChange(value: Editable) {
        _dateOfBirth.value = value.toString()
    }

    /**
     * Sets the date of birth from DatePicker.
     */
    fun setDateOfBirth(value: String) {
        _dateOfBirth.value = value
    }

    /**
     * Sets the gender from spinner.
     */
    fun setGender(value: String) {
        _gender.value = value
    }

    /**
     * Sets the blood type from spinner.
     */
    fun setBloodType(value: String) {
        _bloodType.value = value
    }

    /**
     * Validates input fields and initiates registration.
     */
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

        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _dateOfBirthError.value = null
        _genderError.value = null
        _bloodTypeError.value = null
        _phoneError.value = null
        _addressError.value = null

        var isValid = true
        if (nameValue.isBlank()) {
            _nameError.value = "Name is required"
            isValid = false
        }
        if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
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
        } else {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                LocalDate.parse(dateOfBirthValue, formatter)
            } catch (e: Exception) {
                _dateOfBirthError.value = "Invalid date format (dd/MM/yyyy)"
                isValid = false
            }
        }
        if (genderValue.isBlank()) {
            _genderError.value = "Gender is required"
            isValid = false
        }
        if (bloodTypeValue.isBlank()) {
            _bloodTypeError.value = "Blood type is required"
            isValid = false
        }
        if (phoneValue.isBlank()) {
            _phoneError.value = "Phone is required"
            isValid = false
        } else if (!Patterns.PHONE.matcher(phoneValue).matches()) {
            _phoneError.value = "Invalid phone number format"
            isValid = false
        }

        if (isValid) {
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
    }

    /**
     * Registers a new user and sends a verification code.
     */
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
                Timber.d("Attempting to register user with email: $email")
                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                val parsedDate = LocalDate.parse(dateOfBirth, inputFormatter)
                val formattedDate = parsedDate.format(outputFormatter)

                val uid = createUserUseCase(
                    email = email,
                    password = password,
                    name = name,
                    address = address,
                    dateOfBirth = formattedDate,
                    gender = gender,
                    bloodType = bloodType,
                    phone = phone
                )
                // Send email link instead of verification code
                sendEmailLink(email)
                _registerResult.value = uid
                _error.value = null
                Timber.d("Registration initiated, UID: $uid")
            } catch (e: Exception) {
                Timber.e(e, "Registration failed: ${e.message}")
                _error.value = when {
                    e.message?.contains("email address is already in use") == true ->
                        "This email is already registered. Please use a different email or log in."
                    e.message?.contains("Invalid Firebase Database path") == true ->
                        "Failed to save user data. Please try again."
                    else -> e.message ?: "Registration failed"
                }
                _registerResult.value = null
                _registrationSuccess.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
    private fun getActionCodeSettings(): ActionCodeSettings {
        return ActionCodeSettings.newBuilder()
            .setUrl("https://heart-careproject.firebaseapp.com/finishSignUp")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.healthcareproject", true, "1")
            .build()
    }

    fun sendEmailLink(email: String) {
        _isLoading.value = true
        auth.sendSignInLinkToEmail(email, getActionCodeSettings())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Email link sent to $email")
                    _emailLinkSent.value = true
                    _registrationSuccess.value = "Check your email for the sign-in link!"
                    sharedPreferences.edit()
                        .putString("pending_email", email)
                        .putString("auth_flow", "REGISTRATION")
                        .apply()
                } else {
                    Timber.e(task.exception, "Failed to send email link")
                    _error.value = task.exception?.message ?: "Failed to send email link"
                }
                _isLoading.value = false
            }
    }

    fun linkGoogleAccount(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            Timber.d("Attempting to link Google account for email: ${email.value}")
            val result = linkGoogleCredentialUseCase(idToken, email.value.orEmpty(), password.value.orEmpty())
            result.onSuccess {
                _error.value = null
                Timber.d("Google account linked successfully")
            }.onFailure {
                _error.value = "Failed to link Google account: ${it.message}"
                Timber.e(it, "Failed to link Google account")
            }
            _isLoading.value = false
        }
    }

    fun resetNavigationStates() {
        _registerResult.value = null
        _registrationSuccess.value = null
    }
}