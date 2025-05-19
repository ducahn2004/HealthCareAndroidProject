package com.example.healthcareproject.domain.model

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Notification(
    val notificationId: String,
    val userId: String,
    val type: NotificationType,
    val relatedTable: RelatedTable,
    val relatedId: String,
    val message: String,
    val timestamp: LocalDateTime
) : Parcelable {
    constructor(parcel: Parcel) : this(
        notificationId = parcel.readString() ?: "",
        userId = parcel.readString() ?: "",
        type = NotificationType.valueOf(parcel.readString() ?: NotificationType.None.name),
        relatedTable = RelatedTable.valueOf(parcel.readString() ?: RelatedTable.None.name),
        relatedId = parcel.readString() ?: "",
        message = parcel.readString() ?: "",
        timestamp = LocalDateTime.parse(
            parcel.readString() ?: LocalDateTime.now().toString(),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(notificationId)
        parcel.writeString(userId)
        parcel.writeString(type.name)
        parcel.writeString(relatedTable.name)
        parcel.writeString(relatedId)
        parcel.writeString(message)
        parcel.writeString(timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification = Notification(parcel)
        override fun newArray(size: Int): Array<Notification?> = arrayOfNulls(size)
    }
}
