package com.example.healthcareproject.domain.usecase.measurement

import javax.inject.Inject

class SpO2AnalysisUseCase @Inject constructor() {
    fun isAbnormal(spO2: Float): Boolean {
        return spO2 < 93.0f
    }
}