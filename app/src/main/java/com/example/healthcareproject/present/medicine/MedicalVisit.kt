package com.example.healthcareproject.present.medicine

import android.os.Parcel
import android.os.Parcelable

data class MedicalVisit(
    val id: Long = System.currentTimeMillis(),
    val condition: String,
    val doctor: String,
    val facility: String,
    val date: String,
    val time: String,
    val location: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        condition = parcel.readString() ?: "",
        doctor = parcel.readString() ?: "",
        facility = parcel.readString() ?: "",
        date = parcel.readString() ?: "",
        time = parcel.readString() ?: "",
        location = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(condition)
        parcel.writeString(doctor)
        parcel.writeString(facility)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(location)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MedicalVisit> {
        override fun createFromParcel(parcel: Parcel): MedicalVisit = MedicalVisit(parcel)
        override fun newArray(size: Int): Array<MedicalVisit?> = arrayOfNulls(size)
    }
}