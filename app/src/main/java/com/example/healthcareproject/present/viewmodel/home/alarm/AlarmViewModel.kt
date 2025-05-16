package com.example.healthcareproject.present.viewmodel.home.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.usecase.alert.GetAlertsUseCase
import com.example.healthcareproject.domain.usecase.alert.UpdateAlertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val getAlertsUseCase: GetAlertsUseCase,
    private val updateAlertUseCase: UpdateAlertUseCase
) : ViewModel() {

    private val _alerts = MutableLiveData<List<Alert>>()
    val alerts: LiveData<List<Alert>> get() = _alerts

    init {
        loadAlarms()
    }

    fun loadAlarms() {
        viewModelScope.launch {
            try {
                val alerts = getAlertsUseCase()
                _alerts.postValue(alerts)
            } catch (e: Exception) {
                _alerts.postValue(emptyList())
            }
        }
    }

    fun updateAlertStatus(alertId: String, status: Boolean) {
        viewModelScope.launch {
            val alert = _alerts.value?.find { it.alertId == alertId } ?: return@launch
            updateAlertUseCase(
                alertId = alertId,
                title = alert.title,
                message = alert.message,
                alertTime = alert.alertTime,
                repeatPattern = alert.repeatPattern,
                status = status
            )
        }
    }

    fun updateAlert(
        alertId: String,
        title: String,
        message: String,
        alertTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ) {
        viewModelScope.launch {
            updateAlertUseCase(
                alertId = alertId,
                title = title,
                message = message,
                alertTime = alertTime,
                repeatPattern = repeatPattern,
                status = status
            )
        }
    }
}