package com.example.healthcareproject.present.setting.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.model.User
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
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> get() = _userInfo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isSaved = MutableLiveData<Boolean>(false)
    val isSaved: LiveData<Boolean> get() = _isSaved

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> get() = _dateOfBirth

    private val _gender = MutableLiveData<String>()
    val genderLiveData: LiveData<String> get() = _gender

    private val _bloodType = MutableLiveData<String>()
    val bloodTypeLiveData: LiveData<String> get() = _bloodType

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    private val _email = MutableLiveData<String>()
    private val email: LiveData<String> get() = _email

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
    }

    fun setBloodType(value: String) {
        _bloodType.value = value
    }

    fun setPhone(value: String) {
        _phone.value = value
    }

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
                    _gender.value = formatGenderForDisplay(it.gender)
                    _bloodType.value = formatBloodTypeForDisplay(it.bloodType)
                    _phone.value = it.phone
                    _email.value = it.userId
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load user info"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUserInfoByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nameValue = _name.value?.trim() ?: throw IllegalArgumentException("Name is required")
                val addressValue = _address.value ?: ""
                val dateOfBirthValue = _dateOfBirth.value?.takeIf { isValidDateFormat(it) && isValidDate(it) }
                    ?: throw IllegalArgumentException("Invalid date of birth. Use dd/mm/yyyy")
                val genderValue = _gender.value ?: ""
                val bloodTypeValue = _bloodType.value?.takeIf { it in listOf("A", "B", "AB", "O", "None") }
                    ?: throw IllegalArgumentException("Invalid blood type")
                val phoneValue = _phone.value ?: ""
                val userId = _email.value ?: throw IllegalArgumentException("User email not found")

                updateUserUseCase(
                    userId = userId,
                    name = nameValue,
                    address = addressValue,
                    dateOfBirth = dateOfBirthValue,
                    gender = genderValue,
                    bloodType = bloodTypeValue,
                    phone = phoneValue
                )

                _isSaved.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to save user info"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatDateForDisplay(date: LocalDate?): String {
        return date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US)) ?: ""
    }

    private fun formatGenderForDisplay(gender: Gender?): String {
        return gender?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
    }

    private fun formatBloodTypeForDisplay(bloodType: BloodType?): String {
        return when (bloodType) {
            BloodType.A -> "A"
            BloodType.B -> "B"
            BloodType.AB -> "AB"
            BloodType.O -> "O"
            BloodType.None -> "None"
            null -> ""
        }
    }

    private fun isValidDateFormat(date: String): Boolean {
        return date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US)
            LocalDate.parse(date, formatter).isBefore(LocalDate.now())
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