package com.example.healthcareproject.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class RepeatPattern : Parcelable {
    Daily,
    Weekly,
    Monthly,
    Yearly,
    None
}