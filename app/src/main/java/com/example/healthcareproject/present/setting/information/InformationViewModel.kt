package com.example.healthcareproject.present.setting.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.repository.UserRepository
import com.example.healthcareproject.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> get() = _userInfo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData for displaying user information
    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> get() = _name

    private val _address = MutableLiveData<String>("")
    val address: LiveData<String> get() = _address

    private val _dateOfBirth = MutableLiveData<String>("")
    val dateOfBirth: LiveData<String> get() = _dateOfBirth

    private val _gender = MutableLiveData<String>("")
    val gender: LiveData<String> get() = _gender  // Changed from String? to String

    private val _bloodType = MutableLiveData<String>("")
    val bloodType: LiveData<String> get() = _bloodType

    private val _phone = MutableLiveData<String>("")
    val phone: LiveData<String> get() = _phone

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> get() = _email


    fun loadUserInfoByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = getUserUseCase.invoke(forceUpdate = true)
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

    fun getUserInfo(): Map<String, String> {
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
        super.onCleared()
    }
}