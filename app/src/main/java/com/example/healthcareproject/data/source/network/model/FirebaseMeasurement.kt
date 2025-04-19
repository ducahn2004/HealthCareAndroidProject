package com.example.healthcareproject.data.source.network.model

import com.example.healthcareproject.domain.model.MeasurementType

data class FirebaseMeasurement(
    var measurementId: String = "",
    var userId: String = "",
    var type: MeasurementType = MeasurementType.None,
    var value: Float? = null,
    var valueList: List<Float>? = null,
    var timestamp: String = ""
)

