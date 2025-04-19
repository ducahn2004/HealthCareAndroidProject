package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.Converters
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import java.time.LocalDateTime

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("relatedTable"), Index("relatedId")]
)
@TypeConverters(Converters::class)
data class RoomNotification(
    @PrimaryKey val notificationId: String,
    val userId: String,
    val type: NotificationType,
    val relatedTable: RelatedTable,
    val relatedId: String,
    val message: String,
    val timestamp: LocalDateTime
)
