package com.example.healthcareproject.domain.usecase.notification

import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import com.example.healthcareproject.domain.repository.NotificationRepository
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        notificationId: String,
        type: NotificationType,
        relatedTable: RelatedTable,
        relatedId: String,
        message: String,
        notificationTime: LocalDateTime
    ) {
        notificationRepository.updateNotification(
            notificationId = notificationId,
            type = type,
            relatedTable = relatedTable,
            relatedId = relatedId,
            message = message,
            notificationTime = notificationTime
        )
    }
}