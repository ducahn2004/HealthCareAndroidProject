package com.example.healthcareproject.domain.usecase

import com.example.healthcareproject.domain.repository.UserRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor (
    private val userRepository: UserRepository
){

}