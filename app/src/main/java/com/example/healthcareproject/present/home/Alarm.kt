package com.example.healthcareproject.present.home

import android.os.Parcel
import android.os.Parcelable

data class Alarm(
    val id: Long = System.currentTimeMillis(),
    val medications: List<String>,
    val time: String,
    val repeatPattern: String,
    val isActive: Boolean = true
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        medications = parcel.createStringArrayList() ?: emptyList(),
        time = parcel.readString() ?: "",
        repeatPattern = parcel.readString() ?: "",
        isActive = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeStringList(medications)
        parcel.writeString(time)
        parcel.writeString(repeatPattern)
        parcel.writeByte(if (isActive) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0 // Return 0 unless you're parceling special objects (e.g., file descriptors)
    }

    companion object CREATOR : Parcelable.Creator<Alarm> {
        override fun createFromParcel(parcel: Parcel): Alarm {
            return Alarm(parcel)
        }

        override fun newArray(size: Int): Array<Alarm?> {
            return arrayOfNulls(size)
        }
    }
}