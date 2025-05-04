package com.example.healthcareproject.present.pill

import android.os.Parcel
import android.os.Parcelable
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import java.time.LocalDate

data class Medication(
    val medicationId: String,
    val userId: String,
    val visitId: String?,
    val name: String,
    val dosageUnit: DosageUnit,
    val dosageAmount: Float,
    val frequency: Int,
    val timeOfDay: List<String>,
    val mealRelation: MealRelation,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val notes: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        medicationId = parcel.readString() ?: "",
        userId = parcel.readString() ?: "",
        visitId = parcel.readString(),
        name = parcel.readString() ?: "",
        dosageUnit = DosageUnit.valueOf(parcel.readString() ?: DosageUnit.GallonPerDay.name),
        dosageAmount = parcel.readFloat(),
        frequency = parcel.readInt(),
        timeOfDay = parcel.createStringArrayList() ?: emptyList(),
        mealRelation = MealRelation.valueOf(parcel.readString() ?: MealRelation.AfterMeal.name),
        startDate = LocalDate.parse(parcel.readString()),
        endDate = LocalDate.parse(parcel.readString()),
        notes = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(medicationId)
        parcel.writeString(userId)
        parcel.writeString(visitId)
        parcel.writeString(name)
        parcel.writeString(dosageUnit.name)
        parcel.writeFloat(dosageAmount)
        parcel.writeInt(frequency)
        parcel.writeStringList(timeOfDay)
        parcel.writeString(mealRelation.name)
        parcel.writeString(startDate.toString())
        parcel.writeString(endDate.toString())
        parcel.writeString(notes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Medication> {
        override fun createFromParcel(parcel: Parcel): Medication = Medication(parcel)
        override fun newArray(size: Int): Array<Medication?> = arrayOfNulls(size)
    }
}