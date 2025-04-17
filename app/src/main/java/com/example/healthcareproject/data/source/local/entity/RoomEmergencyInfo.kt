package com.example.healthcareproject.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "emergency_info",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class EmergencyInfo(
    @PrimaryKey val emergencyId: String,
    val userId: String,
    val emergencyName: String,
    val emergencyPhone: String,
    val relationship: Relationship,
    val priority: Int
)

enum class Relationship {
    Parent,
    Sibling,
    Spouse,
    Child,
    Friend,
    Other
}
