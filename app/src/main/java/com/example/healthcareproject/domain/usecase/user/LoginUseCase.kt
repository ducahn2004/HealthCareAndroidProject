package com.example.healthcareproject.domain.usecase.user

import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
}