package com.example.healthcareproject.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class RepeatPattern : Parcelable {
    Once,
    Daily,
    Weekly,
    Monthly,
    Yearly,
    None
}