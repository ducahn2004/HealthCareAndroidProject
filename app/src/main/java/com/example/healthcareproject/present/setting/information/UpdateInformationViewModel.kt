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

    // MutableLiveData for fields (used internally and for observation)
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> get() = _dateOfBirth

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> get() = _gender

    private val _bloodType = MutableLiveData<String>()
    val bloodType: LiveData<String> get() = _bloodType

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    fun getDateOfBirth(): String? = userForm.dateOfBirth

    fun setDateOfBirth(date: String) {
        userForm.dateOfBirth = date
    }

    fun getGender(): String? = _gender.value

    fun setGender(gender: String) {
        _gender.value = gender
    }

    fun getBloodType(): String? = _bloodType.value

    fun setBloodType(bloodType: String) {
        _bloodType.value = bloodType
    }

    fun loadUserInfo(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = getUserUseCase(userId)
                _userInfo.value = user
                user?.let {
                    userForm.name = it.name
                    userForm.address = it.address ?: ""
                    userForm.dateOfBirth = formatDateForDisplay(it.dateOfBirth)
                    _gender.value = formatGenderForDisplay(it.gender)
                    _bloodType.value = formatBloodTypeForDisplay(it.bloodType)
                    userForm.phone = it.phone
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUserInfo(
        userId: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Use the values from the UserForm/MutableLiveData fields
                val nameValue = userForm.name
                val addressValue = userForm.address
                val dateOfBirthValue = userForm.dateOfBirth
                val genderValue = _gender.value ?: ""
                val bloodTypeValue = _bloodType.value ?: ""
                val phoneValue = userForm.phone

                // Validate inputs
                if (nameValue.trim().isEmpty()) {
                    _error.value = "Name is required"
                    return@launch
                }

                if (!isValidDateFormat(dateOfBirthValue) || !isValidDate(dateOfBirthValue)) {
                    _error.value = "Invalid date of birth. Use dd/mm/yyyy and ensure it's a valid date."
                    return@launch
                }

                // Validate blood type
                val validBloodTypes = listOf("A", "B", "AB", "O", "None")
                if (bloodTypeValue !in validBloodTypes) {
                    _error.value = "Invalid blood type. Must be one of: A, B, AB, O, None"
                    return@launch
                }

                // Update user via UpdateUserUseCase
                updateUserUseCase(
                    userId = userId,
                    password = password,
                    name = nameValue,
                    address = addressValue,
                    dateOfBirth = dateOfBirthValue,
                    gender = genderValue,
                    bloodType = bloodTypeValue,
                    phone = phoneValue
                )

                // Update the user info state
                loadUserInfo(userId) // Refresh the user info after saving
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
            sdf.isLenient = false // Do not accept invalid dates (e.g., 31/04)
            val parsedDate = sdf.parse(date)
            parsedDate != null && parsedDate.before(Date()) // Ensure date is not in the future
        } catch (e: Exception) {
            false
        }
    }

    fun getUpdatedUserInfo(): Map<String, String> {
        return mapOf(
            "name" to userForm.name,
            "address" to userForm.address,
            "dob" to userForm.dateOfBirth,
            "gender" to (_gender.value ?: ""),
            "blood_type" to (_bloodType.value ?: ""),
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