package com.example.healthcareproject.present.viewmodel.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthcareproject.domain.usecase.measurement.MeasurementUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpO2ViewModel @Inject constructor(
    private val measurementUseCases: MeasurementUseCases
) : ViewModel() {

    private val _spO2 = MutableLiveData<Float>()
    val spO2: LiveData<Float> get() = _spO2

    init {
        viewModelScope.launch {
            measurementUseCases.getMeasurementRealTimeUseCase().collectLatest { measurements ->
                val latestSpO2 = measurements.lastOrNull()?.spO2
                latestSpO2?.let {
                    _spO2.postValue(it)
                }
            }
        }
    }
}
