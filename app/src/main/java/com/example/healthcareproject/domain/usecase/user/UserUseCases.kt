package com.example.healthcareproject.domain.usecase.user

data class UserUseCases(
    val createUser: CreateUserUseCase,
    val getUser: GetUserUseCase,
    val updateUser: UpdateUserUseCase,
    val deleteUser: DeleteUserUseCase,
    val login: LoginUseCase,
    val logout: LogoutUseCase,
)
