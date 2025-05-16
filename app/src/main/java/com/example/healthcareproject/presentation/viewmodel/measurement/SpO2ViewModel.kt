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
class SpO2ViewModel @Inject constructor(
    private val getMeasurementRealTimeUseCase: GetMeasurementRealTimeUseCase
) : ViewModel() {

    private val _spO2 = MutableLiveData<Float>()
    val spO2: LiveData<Float> get() = _spO2

    private val _spO2History = MutableLiveData<List<Measurement>>()
    val spO2History: LiveData<List<Measurement>> get() = _spO2History

    private val maxDataPoints = 20

    init {
        viewModelScope.launch {
            getMeasurementRealTimeUseCase().collectLatest { measurements ->
                val latestMeasurements = measurements.takeLast(maxDataPoints)
                _spO2History.postValue(latestMeasurements)

                val latestSpO2 = latestMeasurements.lastOrNull()?.spO2
                latestSpO2?.let {
                    _spO2.postValue(it)
                }
            }
        }
    }
}