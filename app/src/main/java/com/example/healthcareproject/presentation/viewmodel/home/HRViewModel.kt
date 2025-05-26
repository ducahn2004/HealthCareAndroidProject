package com.example.healthcareproject.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.usecase.measurement.GetMeasurementRealTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HRViewModel @Inject constructor(
    private val getMeasurementRealTimeUseCase: GetMeasurementRealTimeUseCase
) : ViewModel() {

    private val _heartRateHistory = MutableLiveData<List<Measurement>>()
    val heartRateHistory: LiveData<List<Measurement>> get() = _heartRateHistory

    private val maxDataPoints = 20

    init {
        viewModelScope.launch {
            getMeasurementRealTimeUseCase().collectLatest { measurements ->
                val sortedMeasurements = measurements.sortedBy { it.dateTime }
                val latestMeasurements = if (sortedMeasurements.size > maxDataPoints) {
                    sortedMeasurements.takeLast(maxDataPoints)
                } else {
                    sortedMeasurements
                }
                _heartRateHistory.postValue(latestMeasurements)
            }
        }
    }
    fun getHeartRateDataByTimeFrame(timeFrame: String): Flow<List<Measurement>> {
        return getMeasurementRealTimeUseCase().map { measurements ->
            val now = LocalDateTime.now()
            val filtered = measurements.filter { measurement ->
                when (timeFrame) {
                    "MINUTE" -> measurement.dateTime.isAfter(now.minusMinutes(1))
                    "HOUR" -> measurement.dateTime.isAfter(now.minusHours(1))
                    "DAY" -> measurement.dateTime.isAfter(now.minusDays(1))
                    "WEEK" -> measurement.dateTime.isAfter(now.minusDays(7))
                    else -> true
                }
            }.sortedBy { it.dateTime }
            when (timeFrame) {
                "HOUR" -> aggregateByMinute(filtered)
                "DAY" -> aggregateByHour(filtered)
                "WEEK" -> aggregateByDay(filtered)
                else -> filtered.takeLast(maxDataPoints)
            }
        }
    }

    private fun aggregateByMinute(measurements: List<Measurement>): List<Measurement> {
        return measurements.groupBy { it.dateTime.truncatedTo(ChronoUnit.MINUTES) }
            .map { (dateTime, group) ->
                Measurement(
                    deviceId = group.first().deviceId,
                    measurementId = group.first().measurementId,
                    userId = group.first().userId,
                    bpm = group.map { it.bpm }.average().toFloat(),
                    spO2 = group.map { it.spO2 }.average().toFloat(),
                    dateTime = dateTime
                )
            }
    }

    private fun aggregateByHour(measurements: List<Measurement>): List<Measurement> {
        return measurements.groupBy { it.dateTime.truncatedTo(ChronoUnit.HOURS) }
            .map { (dateTime, group) ->
                Measurement(
                    deviceId = group.first().deviceId,
                    measurementId = group.first().measurementId,
                    userId = group.first().userId,
                    bpm = group.map { it.bpm }.average().toFloat(),
                    spO2 = group.map { it.spO2 }.average().toFloat(),
                    dateTime = dateTime
                )
            }
    }

    private fun aggregateByDay(measurements: List<Measurement>): List<Measurement> {
        return measurements.groupBy { it.dateTime.truncatedTo(ChronoUnit.DAYS) }
            .map { (dateTime, group) ->
                Measurement(
                    deviceId = group.first().deviceId,
                    measurementId = group.first().measurementId,
                    userId = group.first().userId,
                    bpm = group.map { it.bpm }.average().toFloat(),
                    spO2 = group.map { it.spO2 }.average().toFloat(),
                    dateTime = dateTime
                )
            }
    }
}

