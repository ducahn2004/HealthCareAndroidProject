package com.example.healthcareproject.present.notification

import android.os.Parcel
import android.os.Parcelable

data class Notification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val time: String,
    val iconResId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        title = parcel.readString() ?: "",
        message = parcel.readString() ?: "",
        time = parcel.readString() ?: "",
        iconResId = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(time)
        parcel.writeInt(iconResId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification = Notification(parcel)
        override fun newArray(size: Int): Array<Notification?> = arrayOfNulls(size)
    }
}