package com.example.healthcareproject.present.viewmodel.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.usecase.measurement.MeasurementUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HRViewModel @Inject constructor(
    private val measurementUseCases: MeasurementUseCases
) : ViewModel() {

    private val _heartRate = MutableLiveData<Float>()
    val heartRate: LiveData<Float> get() = _heartRate

    private val _heartRateHistory = MutableLiveData<List<Measurement>>()
    val heartRateHistory: LiveData<List<Measurement>> get() = _heartRateHistory

    private val maxDataPoints = 20

    init {
        viewModelScope.launch {
            measurementUseCases.getMeasurementRealTimeUseCase().collectLatest { measurements ->
                val latestMeasurements = measurements.takeLast(maxDataPoints)
                _heartRateHistory.postValue(latestMeasurements)

                val latestBpm = latestMeasurements.lastOrNull()?.bpm
                latestBpm?.let {
                    _heartRate.postValue(it)
                }
            }
        }
    }
}
