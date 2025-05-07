package com.example.healthcareproject.present.viewmodel.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.usecase.alert.DeleteAlertUseCase
import com.example.healthcareproject.domain.usecase.alert.GetAlertsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getAlertsUseCase: GetAlertsUseCase,
    private val deleteAlertUseCase: DeleteAlertUseCase
) : ViewModel() {

    private val _alerts = MutableLiveData<List<Alert>>(emptyList())
    val alerts: LiveData<List<Alert>> get() = _alerts

    init {
        loadAlerts()
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            try {
                val alertsList = getAlertsUseCase()
                _alerts.postValue(alertsList)
            } catch (e: Exception) {
                // Handle error (e.g., show toast or log)
                _alerts.postValue(emptyList())
            }
        }
    }

    fun deleteAlert(alertId: String) {
        viewModelScope.launch {
            try {
                deleteAlertUseCase(alertId)
                // Reload alerts after deletion
                loadAlerts()
            } catch (e: Exception) {
                // Handle error (e.g., show toast or log)
            }
        }
    }
}