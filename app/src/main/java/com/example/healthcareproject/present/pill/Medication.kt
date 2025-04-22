package com.example.healthcareproject.present.pill

import android.os.Parcel
import android.os.Parcelable

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val timeOfDay: String,
    val startTimestamp: Long,
    val endTimestamp: Long?,
    val note: String,
    val visitId: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        name = parcel.readString() ?: "",
        dosage = parcel.readString() ?: "",
        frequency = parcel.readString() ?: "",
        timeOfDay = parcel.readString() ?: "",
        startTimestamp = parcel.readLong(),
        endTimestamp = parcel.readValue(Long::class.java.classLoader) as? Long,
        note = parcel.readString() ?: "",
        visitId = parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(dosage)
        parcel.writeString(frequency)
        parcel.writeString(timeOfDay)
        parcel.writeLong(startTimestamp)
        parcel.writeValue(endTimestamp)
        parcel.writeString(note)
        parcel.writeValue(visitId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Medication> {
        override fun createFromParcel(parcel: Parcel): Medication = Medication(parcel)
        override fun newArray(size: Int): Array<Medication?> = arrayOfNulls(size)
    }
}