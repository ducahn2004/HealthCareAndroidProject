package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.data.source.network.model.FirebaseUser
import com.example.healthcareproject.domain.model.Gender
import com.example.healthcareproject.domain.model.BloodType
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        address: String?,
        dateOfBirth: String,
        gender: String,
        bloodType: String,
        phone: String
    ) {
        // Tạo tài khoản Firebase Auth
        userFirebaseDataSource.createUser(email, password)

        // Chuyển đổi gender từ String sang Gender enum
        val genderEnum = try {
            Gender.valueOf(gender.replace(" ", "").capitalize())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid gender: $gender. Expected: Male, Female, None")
        }

        // Chuyển đổi bloodType từ String sang BloodType enum
        val bloodTypeEnum = try {
            BloodType.valueOf(bloodType.replace(" ", "").capitalize())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid blood type: $bloodType. Expected: A, B, AB, O, None")
        }

        // Tạo FirebaseUser
        val user = FirebaseUser(
            userId = email,
            name = name,
            address = address,
            dateOfBirth = dateOfBirth,
            gender = genderEnum,
            bloodType = bloodTypeEnum,
            phone = phone
        )

        // Lưu vào Realtime Database
        userFirebaseDataSource.saveUser(user)
    }
}