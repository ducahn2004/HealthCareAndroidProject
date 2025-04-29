package com.example.healthcareproject.domain.usecase.sos

import com.example.healthcareproject.domain.model.Sos
import com.example.healthcareproject.domain.repository.SosRepository
import javax.inject.Inject

class GetSosEventsUseCase @Inject constructor(
    private val sosRepository: SosRepository
) {
    suspend operator fun invoke(forceUpdate: Boolean = false): List<Sos> {
        return sosRepository.getSosList(forceUpdate)
    }
}