package com.example.healthcareproject.medicine

import android.os.Parcel
import android.os.Parcelable

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val timeOfDay: String,
    val startDate: String,
    val endDate: String,
    val note: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        name = parcel.readString() ?: "",
        dosage = parcel.readString() ?: "",
        frequency = parcel.readString() ?: "",
        timeOfDay = parcel.readString() ?: "",
        startDate = parcel.readString() ?: "",
        endDate = parcel.readString() ?: "",
        note = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(dosage)
        parcel.writeString(frequency)
        parcel.writeString(timeOfDay)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(note)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Medication> {
        override fun createFromParcel(parcel: Parcel): Medication = Medication(parcel)
        override fun newArray(size: Int): Array<Medication?> = arrayOfNulls(size)
    }
}