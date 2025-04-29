package com.example.healthcareproject.domain.usecase.notification

data class NotificationUseCases(
    val getNotificationsUseCase: GetNotificationsUseCase,
    val getNotificationUseCase: GetNotificationUseCase,
    val createNotificationUseCase: CreateNotificationUseCase,
    val updateNotificationUseCase: UpdateNotificationUseCase,
    val deleteNotificationUseCase: DeleteNotificationUseCase
)