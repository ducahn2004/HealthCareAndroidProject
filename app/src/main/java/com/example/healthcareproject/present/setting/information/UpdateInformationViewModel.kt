package com.example.healthcareproject.present.setting.information

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UpdateInformationViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val userRepository: UserRepository // Inject UserRepository to fetch email if needed
) : ViewModel() {

    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> get() = _userInfo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isSaved = MutableLiveData<Boolean>(false)
    val isSaved: LiveData<Boolean> get() = _isSaved

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Data class for two-way binding
    inner class UserForm : BaseObservable() {
        @get:Bindable
        var name: String = ""
            set(value) {
                if (field != value) {
                    field = value
                    _name.value = value
                    notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.name)
                }
            }

        @get:Bindable
        var address: String = ""
            set(value) {
                if (field != value) {
                    field = value
                    _address.value = value
                    notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.address)
                }
            }

        @get:Bindable
        var dateOfBirth: String = ""
            set(value) {
                if (field != value) {
                    field = value
                    _dateOfBirth.value = value
                    notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.dateOfBirth)
                }
            }

        @get:Bindable
        var phone: String = ""
            set(value) {
                if (field != value) {
                    field = value
                    _phone.value = value
                    notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.phone)
                }
            }
    }

    // Instance of UserForm for binding
    val userForm = UserForm()

    // MutableLiveData for fields
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> get() = _dateOfBirth

    private val _genderLiveData = MutableLiveData<String>()
    val genderLiveData: LiveData<String> get() = _genderLiveData

    private val _bloodTypeLiveData = MutableLiveData<String>()
    val bloodTypeLiveData: LiveData<String> get() = _bloodTypeLiveData

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    fun getDateOfBirth(): String? = userForm.dateOfBirth

    fun setDateOfBirth(date: String) {
        userForm.dateOfBirth = date
    }

    fun getGender(): String? = _genderLiveData.value

    fun setGender(gender: String) {
        _genderLiveData.value = gender
    }

    fun getBloodType(): String? = _bloodTypeLiveData.value

    fun setBloodType(bloodType: String) {
        _bloodTypeLiveData.value = bloodType
    }

    fun loadUserInfoByUid(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = getUserUseCase.invoke(identifier = uid, forceUpdate = true, isUid = true)
                _userInfo.value = user
                user?.let {
                    userForm.name = it.name
                    userForm.address = it.address ?: ""
                    userForm.dateOfBirth = formatDateForDisplay(it.dateOfBirth)
                    _genderLiveData.value = formatGenderForDisplay(it.gender)
                    _bloodTypeLiveData.value = formatBloodTypeForDisplay(it.bloodType)
                    userForm.phone = it.phone
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
                val nameValue = userForm.name
                val addressValue = userForm.address
                val dateOfBirthValue = userForm.dateOfBirth
                val genderValue = _genderLiveData.value ?: ""
                val bloodTypeValue = _bloodTypeLiveData.value ?: ""
                val phoneValue = userForm.phone

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
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(formatter)
    }

    fun formatDateTimeForDisplay(dateTime: LocalDateTime?): String {
        if (dateTime == null) return ""
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return dateTime.format(formatter)
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
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            val parsedDate = sdf.parse(date)
            parsedDate != null && parsedDate.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getUpdatedUserInfo(): Map<String, String> {
        return mapOf(
            "name" to userForm.name,
            "address" to userForm.address,
            "dob" to userForm.dateOfBirth,
            "gender" to (_genderLiveData.value ?: ""),
            "blood_type" to (_bloodTypeLiveData.value ?: ""),
            "phone" to userForm.phone
        )
    }

    override fun onCleared() {
        _userInfo.value = null
        _error.value = null
        _isSaved.value = false
        super.onCleared()
    }
}