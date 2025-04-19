package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthcareproject.data.source.local.Converters
import com.example.healthcareproject.domain.model.Relationship


@Entity(
    tableName = "emergency_infos",
    foreignKeys = [
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
@TypeConverters(Converters::class)
data class RoomEmergencyInfo(
    @PrimaryKey val emergencyId: String,
    val userId: String,
    val emergencyName: String,
    val emergencyPhone: String,
    val relationship: Relationship,
    val priority: Int
)
