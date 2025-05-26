package com.example.healthcareproject.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.usecase.measurement.GetMeasurementRealTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class SpO2ViewModel @Inject constructor(
    private val getMeasurementRealTimeUseCase: GetMeasurementRealTimeUseCase
) : ViewModel() {

    private val maxDataPoints = 20

    fun getSpO2DataByTimeFrame(timeFrame: String): Flow<List<Measurement>> {
        return getMeasurementRealTimeUseCase()
            .debounce(1000) // Prevent rapid updates
            .map { measurements ->
                Timber.d("Raw measurements count: ${measurements.size}, data: $measurements")
                if (measurements.isEmpty()) {
                    Timber.w("No measurements received from repository")
                    emptyList()
                } else {
                    val now = LocalDateTime.now()
                    val filtered = measurements.filter { measurement ->
                        when (timeFrame) {
                            "MINUTE" -> measurement.dateTime.isAfter(now.minusMinutes(60)) // Include sample data
                            "HOUR" -> measurement.dateTime.isAfter(now.minusHours(6))
                            "DAY" -> measurement.dateTime.isAfter(now.minusDays(2))
                            "WEEK" -> measurement.dateTime.isAfter(now.minusDays(14))
                            else -> true
                        }
                    }.sortedBy { it.dateTime }
                    Timber.d("Filtered measurements count: ${filtered.size}, data: $filtered")
                    val result = when (timeFrame) {
                        "HOUR" -> aggregateByMinute(filtered)
                        "DAY" -> aggregateByHour(filtered)
                        "WEEK" -> aggregateByDay(filtered)
                        else -> filtered.takeLast(maxDataPoints)
                    }
                    Timber.d("Aggregated result count: ${result.size}, data: $result")
                    result.takeLast(maxDataPoints)
                }
            }.catch { e ->
                Timber.e(e, "Error in getSpO2DataByTimeFrame")
                emit(emptyList())
            }
    }

    private fun aggregateByMinute(measurements: List<Measurement>): List<Measurement> {
        val grouped = measurements.groupBy { it.dateTime.truncatedTo(ChronoUnit.MINUTES) }
        Timber.d("Grouped by minute count: ${grouped.size}")
        return grouped.map { (dateTime, group) ->
            Measurement(
                deviceId = group.first().deviceId,
                measurementId = group.first().measurementId,
                userId = group.first().userId,
                bpm = group.map { it.bpm }.average().toFloat(),
                spO2 = group.map { it.spO2 }.average().toFloat(),
                dateTime = dateTime
            )
        }.takeLast(maxDataPoints)
    }

    private fun aggregateByHour(measurements: List<Measurement>): List<Measurement> {
        val grouped = measurements.groupBy { it.dateTime.truncatedTo(ChronoUnit.HOURS) }
        Timber.d("Grouped by hour count: ${grouped.size}")
        return grouped.map { (dateTime, group) ->
            Measurement(
                deviceId = group.first().deviceId,
                measurementId = group.first().measurementId,
                userId = group.first().userId,
                bpm = group.map { it.bpm }.average().toFloat(),
                spO2 = group.map { it.spO2 }.average().toFloat(),
                dateTime = dateTime
            )
        }.takeLast(maxDataPoints)
    }

    private fun aggregateByDay(measurements: List<Measurement>): List<Measurement> {
        val grouped = measurements.groupBy { it.dateTime.truncatedTo(ChronoUnit.DAYS) }
        Timber.d("Grouped by day count: ${grouped.size}")
        return grouped.map { (dateTime, group) ->
            Measurement(
                deviceId = group.first().deviceId,
                measurementId = group.first().measurementId,
                userId = group.first().userId,
                bpm = group.map { it.bpm }.average().toFloat(),
                spO2 = group.map { it.spO2 }.average().toFloat(),
                dateTime = dateTime
            )
        }.takeLast(maxDataPoints)
    }
}