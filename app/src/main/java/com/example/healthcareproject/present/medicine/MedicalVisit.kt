package com.example.healthcareproject.present.medicine

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.LocalDateTime

data class MedicalVisit(
    val visitId: String,
    val userId: String,
    val visitDate: LocalDate,
    val clinicName: String,
    val doctorName: String,
    val diagnosis: String,
    val treatment: String,
    val createdAt: LocalDateTime
) : Parcelable {
    constructor(parcel: Parcel) : this(
        visitId = parcel.readString() ?: "",
        userId = parcel.readString() ?: "",
        visitDate = LocalDate.parse(parcel.readString()),
        clinicName = parcel.readString() ?: "",
        doctorName = parcel.readString() ?: "",
        diagnosis = parcel.readString() ?: "",
        treatment = parcel.readString() ?: "",
        createdAt = LocalDateTime.parse(parcel.readString())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(visitId)
        parcel.writeString(userId)
        parcel.writeString(visitDate.toString())
        parcel.writeString(clinicName)
        parcel.writeString(doctorName)
        parcel.writeString(diagnosis)
        parcel.writeString(treatment)
        parcel.writeString(createdAt.toString())
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MedicalVisit> {
        override fun createFromParcel(parcel: Parcel): MedicalVisit = MedicalVisit(parcel)
        override fun newArray(size: Int): Array<MedicalVisit?> = arrayOfNulls(size)
    }
}