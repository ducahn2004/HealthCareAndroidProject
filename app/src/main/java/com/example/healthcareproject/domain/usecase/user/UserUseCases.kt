package com.example.healthcareproject.domain.usecase.user

import com.example.healthcareproject.domain.usecase.user.UpdateUserUseCase

data class UserUseCases(
    val createUser: CreateUserUseCase,
    val getUser: GetUserUseCase,
    val updateUser: UpdateUserUseCase,
    val deleteUser: DeleteUserUseCase,
    val login: LoginUserUseCase,
    val logout: LogoutUseCase,
)
