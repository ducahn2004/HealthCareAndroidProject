package com.example.healthcareproject.data.source.network.model

import com.example.healthcareproject.domain.model.BloodType
import com.example.healthcareproject.domain.model.Gender

data class FirebaseUser(
    var userId: String = "",
    var password: String = "",
    var name: String = "",
    var address: String? = null,
    var dateOfBirth: String = "",
    var gender: Gender = Gender.None,
    var bloodType: BloodType = BloodType.None,
    var phone: String = "",
    var createdAt: String = "",
    var updatedAt: String = ""
)


