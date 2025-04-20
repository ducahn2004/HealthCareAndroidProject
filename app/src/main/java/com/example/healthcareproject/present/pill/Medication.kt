package com.example.healthcareproject.present.pill

import android.os.Parcel
import android.os.Parcelable

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val timeOfDay: String,
    val startTimestamp: Long,
    val endTimestamp: Long? = null,
    val note: String,
    val visitId: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        name = parcel.readString() ?: "",
        dosage = parcel.readString() ?: "",
        frequency = parcel.readString() ?: "",
        timeOfDay = parcel.readString() ?: "",
        startTimestamp = parcel.readLong(),
        endTimestamp = parcel.readLong().let { if (it == -1L) null else it },
        note = parcel.readString() ?: "",
        visitId = parcel.readLong().let { if (it == -1L) null else it }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(dosage)
        parcel.writeString(frequency)
        parcel.writeString(timeOfDay)
        parcel.writeLong(startTimestamp)
        parcel.writeLong(endTimestamp ?: -1L)
        parcel.writeString(note)
        parcel.writeLong(visitId ?: -1L)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Medication> {
        override fun createFromParcel(parcel: Parcel): Medication = Medication(parcel)
        override fun newArray(size: Int): Array<Medication?> = arrayOfNulls(size)
    }
}