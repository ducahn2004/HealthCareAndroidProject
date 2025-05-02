package com.example.healthcareproject.present

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.User
import com.example.healthcareproject.domain.usecase.CreateUserUseCase
import com.example.healthcareproject.domain.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun createUser(
        userId: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) {
        viewModelScope.launch {
            try {
                createUserUseCase(
                    userId = userId,
                    password = password,
                    name = name,
                    address = address,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    bloodType = bloodType,
                    phone = phone
                )
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getUser(userId: String, forceUpdate: Boolean = false) {
        viewModelScope.launch {
            try {
                val fetchedUser = getUserUseCase(forceUpdate)
                _user.value = fetchedUser
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}