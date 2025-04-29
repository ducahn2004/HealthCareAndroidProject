package com.example.healthcareproject.domain.usecase.notification

import com.example.healthcareproject.domain.model.Notification
import com.example.healthcareproject.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String, forceUpdate: Boolean = false): Notification? {
        return notificationRepository.getNotification(notificationId, forceUpdate)
    }
}