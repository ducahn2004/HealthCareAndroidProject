package com.example.healthcareproject.present.viewmodel.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthcareproject.domain.usecase.measurement.GetMeasurementRealTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMeasurementRealTimeUseCase: GetMeasurementRealTimeUseCase
) : ViewModel() {

    private val _heartRate = MutableLiveData<Float>()
    val heartRate: LiveData<Float> get() = _heartRate

    private val _spO2 = MutableLiveData<Float>()
    val spO2: LiveData<Float> get() = _spO2

    init {
        viewModelScope.launch {
            getMeasurementRealTimeUseCase().collectLatest { measurements ->
                val latestHeartRate = measurements.lastOrNull()?.bpm
                val latestSpO2 = measurements.lastOrNull()?.spO2

                latestHeartRate?.let { _heartRate.postValue(it) }
                latestSpO2?.let { _spO2.postValue(it) }
            }
        }
    }
}