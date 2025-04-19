package com.example.healthcareproject.data.source.network.model

import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable

data class FirebaseNotification(
    var notificationId: String = "",
    var userId: String = "",
    var type: NotificationType = NotificationType.None,
    var relatedTable: RelatedTable = RelatedTable.None,
    var relatedId: String = "",
    var message: String = "",
    var timestamp: String = ""
)
