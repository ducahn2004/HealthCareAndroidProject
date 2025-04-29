package com.example.healthcareproject.domain.usecase.notification

import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import com.example.healthcareproject.domain.repository.NotificationRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CreateNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        type: NotificationType,
        relatedTable: RelatedTable,
        relatedId: String,
        message: String,
        notificationTime: LocalDateTime
    ): String {
        return notificationRepository.createNotification(
            type = type,
            relatedTable = relatedTable,
            relatedId = relatedId,
            message = message,
            notificationTime = notificationTime
        )
    }
}