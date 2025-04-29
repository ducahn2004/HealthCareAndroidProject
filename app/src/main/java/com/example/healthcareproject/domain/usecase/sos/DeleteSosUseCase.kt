package com.example.healthcareproject.domain.usecase.sos

import com.example.healthcareproject.domain.repository.SosRepository
import javax.inject.Inject

class DeleteSosUseCase @Inject constructor(
    private val sosRepository: SosRepository
) {
    suspend operator fun invoke(sosId: String) {
        sosRepository.deleteSos(sosId)
    }
}