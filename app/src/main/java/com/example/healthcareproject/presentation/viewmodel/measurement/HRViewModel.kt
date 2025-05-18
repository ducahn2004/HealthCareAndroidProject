package com.example.healthcareproject.presentation.viewmodel.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.usecase.measurement.GetMeasurementRealTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HRViewModel @Inject constructor(
    private val getMeasurementRealTimeUseCase: GetMeasurementRealTimeUseCase
) : ViewModel() {

    private val _heartRateHistory = MutableLiveData<List<Measurement>>()
    val heartRateHistory: LiveData<List<Measurement>> get() = _heartRateHistory

    private val maxDataPoints = 100

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
}

