package com.example.healthcareproject.present.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.measurement.GetMeasurementsStreamUseCase
import com.example.healthcareproject.domain.usecase.measurement.HRAnalysisUseCase
import com.example.healthcareproject.domain.usecase.measurement.SpO2AnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMeasurementsStreamUseCase: GetMeasurementsStreamUseCase,
    private val hrAnalysisUseCase: HRAnalysisUseCase,
    private val spO2AnalysisUseCase: SpO2AnalysisUseCase
) : ViewModel() {

    private val _heartRate = MutableLiveData<String>(DEFAULT_HEART_RATE)
    val heartRate: LiveData<String> = _heartRate

    private val _oxygenLevel = MutableLiveData<String>(DEFAULT_OXYGEN_LEVEL)
    val oxygenLevel: LiveData<String> = _oxygenLevel

    private val _ecgStatus = MutableLiveData<String>(DEFAULT_ECG_STATUS)
    val ecgStatus: LiveData<String> = _ecgStatus

    private val _weight = MutableLiveData<String>(DEFAULT_WEIGHT)
    val weight: LiveData<String> = _weight

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _heartRateAlert = MutableLiveData<String?>()
    val heartRateAlert: LiveData<String?> = _heartRateAlert

    private val _oxygenLevelAlert = MutableLiveData<String?>()
    val oxygenLevelAlert: LiveData<String?> = _oxygenLevelAlert

    init {
        // Start SOS analysis in background
        viewModelScope.launch {
            hrAnalysisUseCase()
        }
        viewModelScope.launch {
            spO2AnalysisUseCase()
        }
        // Start real-time UI updates
        startRealTimeMeasurements()
    }

    fun startRealTimeMeasurements() {
        _isLoading.value = true
        getMeasurementsStreamUseCase().onEach { measurements ->
            val latestMeasurement = measurements.maxByOrNull { it.measurementId }
            if (latestMeasurement != null) {
                _heartRate.value = "${latestMeasurement.bpm.toInt()}bpm"
                _oxygenLevel.value = "${latestMeasurement.spO2.toInt()}%"

                // SpO2 analysis for UI alerts
                val spO2Value = latestMeasurement.spO2
                _oxygenLevelAlert.value = when {
                    spO2Value < 90 -> "Critical: SpO2 ${spO2Value.toInt()}%"
                    spO2Value in 90.0f..92.9f -> "Warning: SpO2 ${spO2Value.toInt()}%"
                    spO2Value in 93.0f..95.9f -> "Moderate: SpO2 ${spO2Value.toInt()}%"
                    else -> null
                }

                // HR analysis for UI alerts (simplified, as HRAnalysisUseCase uses user data)
                val heartRate = latestMeasurement.bpm
                _heartRateAlert.value = if (heartRate < 60 || heartRate > 100) {
                    "Abnormal HR: ${heartRate.toInt()}bpm"
                } else {
                    null
                }

                _ecgStatus.value = "GOOD" // Placeholder
                _weight.value = "65kg" // Placeholder
                _error.value = null
            } else {
                resetToDefaultValues()
            }
            _isLoading.value = false
        }.catch { e ->
            resetToDefaultValues()
            _error.value = e.message ?: "Failed to load real-time data"
            _isLoading.value = false
        }.launchIn(viewModelScope)
    }

    fun refreshMeasurements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val measurements = getMeasurementsStreamUseCase().first()
                val latestMeasurement = measurements.maxByOrNull { it.measurementId }
                if (latestMeasurement != null) {
                    _heartRate.value = "${latestMeasurement.bpm.toInt()}bpm"
                    _oxygenLevel.value = "${latestMeasurement.spO2.toInt()}%"

                    // SpO2 analysis for UI alerts
                    val spO2Value = latestMeasurement.spO2
                    _oxygenLevelAlert.value = when {
                        spO2Value < 90 -> "Critical: SpO2 ${spO2Value.toInt()}%"
                        spO2Value in 90.0f..92.9f -> "Warning: SpO2 ${spO2Value.toInt()}%"
                        spO2Value in 93.0f..95.9f -> "Moderate: SpO2 ${spO2Value.toInt()}%"
                        else -> null
                    }

                    // HR analysis for UI alerts
                    val heartRate = latestMeasurement.bpm
                    _heartRateAlert.value = if (heartRate < 60 || heartRate > 100) {
                        "Abnormal HR: ${heartRate.toInt()}bpm"
                    } else {
                        null
                    }

                    _ecgStatus.value = "GOOD"
                    _weight.value = "65kg"
                    _error.value = null
                } else {
                    resetToDefaultValues()
                }
            } catch (e: Exception) {
                resetToDefaultValues()
                _error.value = e.message ?: "Failed to refresh data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun resetToDefaultValues() {
        _heartRate.value = DEFAULT_HEART_RATE
        _oxygenLevel.value = DEFAULT_OXYGEN_LEVEL
        _ecgStatus.value = DEFAULT_ECG_STATUS
        _weight.value = DEFAULT_WEIGHT
        _heartRateAlert.value = null
        _oxygenLevelAlert.value = null
    }

    companion object {
        private const val DEFAULT_HEART_RATE = "N/A"
        private const val DEFAULT_OXYGEN_LEVEL = "N/A"
        private const val DEFAULT_ECG_STATUS = "N/A"
        private const val DEFAULT_WEIGHT = "65kg"
    }
}