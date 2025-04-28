package com.example.healthcareproject.present.auth

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.CreateUserUseCase
import com.example.healthcareproject.domain.usecase.LoginUserUseCase
import com.example.healthcareproject.domain.usecase.SendPasswordResetEmailUseCase
import com.example.healthcareproject.domain.usecase.UpdatePasswordUseCase
import com.example.healthcareproject.domain.usecase.ResetPasswordUseCase
import com.example.healthcareproject.domain.usecase.VerifyCodeUseCase
import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val userFirebaseDataSource: UserFirebaseDataSource
) : ViewModel() {

    enum class AuthFlow {
        REGISTRATION,
        LOGIN,
        FORGOT_PASSWORD
    }

    private val _authFlow = MutableLiveData<AuthFlow>(AuthFlow.LOGIN)
    val authFlow: LiveData<AuthFlow> = _authFlow

    private val _currentUid = MutableLiveData<String?>()
    val currentUid: LiveData<String?> = _currentUid

    fun setAuthFlow(flow: AuthFlow) {
        resetForm()
        _authFlow.value = flow
    }

    private val _navigateToGoogleLogin = MutableLiveData<Boolean>()
    val navigateToGoogleLogin: LiveData<Boolean> = _navigateToGoogleLogin

    private val _navigateToRegister = MutableLiveData<Boolean>()
    val navigateToRegister: LiveData<Boolean> = _navigateToRegister

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun onGoogleLoginClicked() {
        _navigateToGoogleLogin.value = true
    }

    fun resetNavigationStates() {
        _navigateToGoogleLogin.value = false
        _navigateToRegister.value = false
        _navigateToLogin.value = false
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

    fun setGender(value: String) {
        _gender.value = value
    }

    fun setBloodType(value: String) {
        _bloodType.value = value
    }

    fun setPhone(value: String) {
        _phone.value = value
    }

    fun setAddress(value: String?) {
        _address.value = value
    }

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isCodeVerified = MutableLiveData<Boolean>()
    val isCodeVerified: LiveData<Boolean> = _isCodeVerified

    private val _verificationCodeError = MutableLiveData<String?>()
    val verificationCodeError: LiveData<String?> = _verificationCodeError

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

    private val _timerText = MutableLiveData<String>("00:59")
    val timerText: LiveData<String> = _timerText

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

    private val _addressError = MutableLiveData<String?>()
    val addressError: LiveData<String?> = _addressError

    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError

    fun setError(message: String): String? {
        _error.value = message
        return _error.value
    }

    fun onLoginClicked() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""

        _emailError.value = null
        _passwordError.value = null

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

        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _dateOfBirthError.value = null
        _genderError.value = null
        _bloodTypeError.value = null
        _addressError.value = null
        _phoneError.value = null

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
        if (phoneValue.isBlank()) {
            _phoneError.value = "Phone is required"
            isValid = false
        } else if (!android.util.Patterns.PHONE.matcher(phoneValue).matches()) {
            _phoneError.value = "Invalid phone number format"
            isValid = false
        }
        if (!isValid) {
            return
        }

        println("AuthViewModel: Starting registration for email: $emailValue")
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
                val uid = userFirebaseDataSource.getUidByEmail(email.value ?: "")
                _currentUid.value = uid
                _isAuthenticated.value = true
                println("Google Sign-In successful, UID: $uid")
            } catch (e: Exception) {
                _error.value = e.message ?: "Google Sign-In failed"
                println("Google Sign-In failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private var countDownTimer: CountDownTimer? = null

    fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(59000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                _timerText.value = String.format("%02d:%02d", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                _timerText.value = "00:00"
            }
        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    fun navigateToRegister() {
        Timber.d("Navigate to Register clicked")
        _navigateToRegister.value = true
    }

    fun navigateToLogin() {
        Timber.d("Navigate to Login clicked")
        _navigateToLogin.value = true
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
                _isCodeVerified.postValue(true)
                Timber.d("Code verification successful")
            } catch (e: Exception) {
                Timber.e(e, "Code verification failed")
                _verificationCodeError.postValue(e.message ?: "Invalid verification code")
            } finally {
                _isLoading.postValue(false)
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
                _email.postValue(email)
                Timber.d("Verification code sent successfully to $email")
                _error.postValue("Verification code sent. Please check your email.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to send verification code")
                _error.postValue(e.message ?: "Failed to send verification code")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun sendVerificationCode(email: String) {
        _emailError.value = null
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                userFirebaseDataSource.sendVerificationCode(email)
                _email.postValue(email)
                Timber.d("Verification code sent successfully to $email")
                _error.postValue("Verification code sent. Please check your email.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to send verification code")
                _error.postValue(e.message ?: "Failed to send verification code")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun login(email: String, password: String) {
        _error.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val uid = loginUserUseCase(email, password)
                _currentUid.value = uid.toString()
                _isAuthenticated.value = true
                println("Login successful for email: $email, UID: $uid")
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
                println("Login failed: ${e.message}")
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
                println("AuthViewModel: Calling CreateUserUseCase for email: $email")
                createUserUseCase(
                    userId = email,
                    password = password,
                    name = name,
                    address = address,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    bloodType = bloodType,
                    phone = phone
                )
                val uid = userFirebaseDataSource.getUidByEmail(email)
                _currentUid.value = uid
                sendVerificationCode(email)
                _isAuthenticated.value = true
                println("AuthViewModel: Registration successful for email: $email, UID: $uid")
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
                println("AuthViewModel: Registration failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        val emailValue = email.value ?: ""
        if (emailValue.isBlank()) {
            _error.value = "Email is required to update password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                updatePasswordUseCase(emailValue, currentPassword, newPassword)
                _password.value = newPassword
                println("Password updated successfully")
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update password"
                println("Failed to update password: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(newPassword: String) {
        val emailValue = email.value ?: ""
        if (emailValue.isBlank()) {
            _error.value = "Email is required to reset password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                resetPasswordUseCase(emailValue, newPassword)
                _password.value = newPassword
                println("Password reset successfully")
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to reset password"
                println("Failed to reset password: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetVerificationState() {
        _isCodeVerified.value = false
        _verificationCodeError.value = null
    }

    fun onForgotPasswordStarted() {
        _error.value = null
        _emailError.value = null
    }

    fun resetForm() {
        _name.value = ""
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _dateOfBirth.value = ""
        _gender.value = ""
        _bloodType.value = ""
        _phone.value = ""
        _address.value = null
        _error.value = null
        _nameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null
        _dateOfBirthError.value = null
        _genderError.value = null
        _bloodTypeError.value = null
        _addressError.value = null
        _phoneError.value = null
    }
}