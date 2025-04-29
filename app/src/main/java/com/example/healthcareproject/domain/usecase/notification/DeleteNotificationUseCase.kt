package com.example.healthcareproject.domain.usecase.notification

import com.example.healthcareproject.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String) {
        notificationRepository.deleteNotification(notificationId)
    }
}