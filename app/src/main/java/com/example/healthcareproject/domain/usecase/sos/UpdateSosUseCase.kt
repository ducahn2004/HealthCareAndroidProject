package com.example.healthcareproject.domain.usecase.sos

import com.example.healthcareproject.domain.repository.SosRepository
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateSosUseCase @Inject constructor(
    private val sosRepository: SosRepository
) {
    suspend operator fun invoke(
        sosId: String,
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    ) {
        sosRepository.updateSos(
            sosId = sosId,
            measurementId = measurementId,
            emergencyId = emergencyId,
            triggerReason = triggerReason,
            contacted = contacted,
            timestamp = timestamp
        )
    }
}