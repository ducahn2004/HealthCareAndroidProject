package com.example.healthcareproject.present.setting.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.repository.UserRepository
import com.example.healthcareproject.domain.usecase.GetUserUseCase
import com.example.healthcareproject.domain.usecase.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UpdateInformationViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> get() = _userInfo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isSaved = MutableLiveData<Boolean>(false)
    val isSaved: LiveData<Boolean> get() = _isSaved

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData for form fields
    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> get() = _name

    private val _address = MutableLiveData<String>("")
    val address: LiveData<String> get() = _address

    private val _dateOfBirth = MutableLiveData<String>("")
    val dateOfBirth: LiveData<String> get() = _dateOfBirth

    private val _gender = MutableLiveData<String>("")
    val gender: LiveData<String> get() = _gender

    private val _genderLiveData = MutableLiveData<String>("")
    val genderLiveData: LiveData<String> get() = _genderLiveData

    private val _bloodType = MutableLiveData<String>("")
    val bloodType: LiveData<String> get() = _bloodType

    private val _bloodTypeLiveData = MutableLiveData<String>("")
    val bloodTypeLiveData: LiveData<String> get() = _bloodTypeLiveData

    private val _phone = MutableLiveData<String>("")
    val phone: LiveData<String> get() = _phone

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> get() = _email

    // Update methods
    fun setName(value: String) {
        _name.value = value
    }

    fun setAddress(value: String) {
        _address.value = value
    }

    fun setDateOfBirth(value: String) {
        _dateOfBirth.value = value
    }

    fun setGender(value: String) {
        _gender.value = value
        _genderLiveData.value = value
    }

    fun setBloodType(value: String) {
        _bloodType.value = value
        _bloodTypeLiveData.value = value
    }

    fun setPhone(value: String) {
        _phone.value = value
    }

    fun getDateOfBirth(): String? = _dateOfBirth.value

    fun getGender(): String? = _gender.value

    fun getBloodType(): String? = _bloodType.value

    fun loadUserInfoByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = getUserUseCase.invoke(identifier = uid, forceUpdate = true, isUid = true)
                _userInfo.value = user
                user?.let {
                    _name.value = it.name
                    _address.value = it.address ?: ""
                    _dateOfBirth.value = formatDateForDisplay(it.dateOfBirth)

                    val formattedGender = formatGenderForDisplay(it.gender)
                    _gender.value = formattedGender
                    _genderLiveData.value = formattedGender

                    val formattedBloodType = formatBloodTypeForDisplay(it.bloodType)
                    _bloodType.value = formattedBloodType
                    _bloodTypeLiveData.value = formattedBloodType

                    _phone.value = it.phone
                    _email.value = it.userId
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUserInfoByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nameValue = _name.value ?: ""
                val addressValue = _address.value ?: ""
                val dateOfBirthValue = _dateOfBirth.value ?: ""
                val genderValue = _gender.value ?: ""
                val bloodTypeValue = _bloodType.value ?: ""
                val phoneValue = _phone.value ?: ""

                if (nameValue.trim().isEmpty()) {
                    _error.value = "Name is required"
                    return@launch
                }

                if (!isValidDateFormat(dateOfBirthValue) || !isValidDate(dateOfBirthValue)) {
                    _error.value = "Invalid date of birth. Use dd/mm/yyyy and ensure it's a valid date."
                    return@launch
                }

                val validBloodTypes = listOf("A", "B", "AB", "O", "None")
                if (bloodTypeValue !in validBloodTypes) {
                    _error.value = "Invalid blood type. Must be one of: A, B, AB, O, None"
                    return@launch
                }

                // Fetch the user's email (userId) from the stored email or user data
                val userId = _email.value ?: throw Exception("User email not found")

                updateUserUseCase(
                    userId = userId,
                    name = nameValue,
                    address = addressValue,
                    dateOfBirth = dateOfBirthValue,
                    gender = genderValue,
                    bloodType = bloodTypeValue,
                    phone = phoneValue
                )

                // Reload user info after update
                loadUserInfoByUid(uid)
                _isSaved.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun formatDateForDisplay(date: LocalDate?): String {
        if (date == null) return ""
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US)
        return date.format(formatter)
    }

    fun formatGenderForDisplay(gender: Gender?): String {
        if (gender == null) return ""
        return gender.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun formatBloodTypeForDisplay(bloodType: BloodType?): String {
        if (bloodType == null) return ""
        return when (bloodType) {
            BloodType.A -> "A"
            BloodType.B -> "B"
            BloodType.AB -> "AB"
            BloodType.O -> "O"
            BloodType.None -> "None"
        }
    }

    private fun isValidDateFormat(date: String): Boolean {
        return date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US)
            val parsedDate = LocalDate.parse(date, formatter)
            parsedDate != null && parsedDate.isBefore(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    fun getUpdatedUserInfo(): Map<String, String> {
        return mapOf(
            "name" to (_name.value ?: ""),
            "address" to (_address.value ?: ""),
            "dob" to (_dateOfBirth.value ?: ""),
            "gender" to (_gender.value ?: ""),
            "blood_type" to (_bloodType.value ?: ""),
            "phone" to (_phone.value ?: "")
        )
    }

    override fun onCleared() {
        _userInfo.value = null
        _error.value = null
        _isSaved.value = false
        super.onCleared()
    }
}