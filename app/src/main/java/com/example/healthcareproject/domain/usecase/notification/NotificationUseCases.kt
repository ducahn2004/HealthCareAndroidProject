package com.example.healthcareproject.domain.usecase.notification

data class NotificationUseCases(
    val getNotificationsUseCase: GetNotificationsUseCase,
    val getNotificationUseCase: GetNotificationUseCase,
    val updateNotificationUseCase: UpdateNotificationUseCase,
    val deleteNotificationUseCase: DeleteNotificationUseCase
)