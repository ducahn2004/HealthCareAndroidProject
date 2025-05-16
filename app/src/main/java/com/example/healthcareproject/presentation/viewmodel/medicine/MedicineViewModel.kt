package com.example.healthcareproject.presentation.viewmodel.medicine

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Appointment
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.usecase.appointment.GetAppointmentsUseCase
import com.example.healthcareproject.domain.usecase.medicalvisit.GetMedicalVisitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val getMedicalVisitsUseCase: GetMedicalVisitsUseCase,
    private val getAppointmentsUseCase: GetAppointmentsUseCase
) : ViewModel() {

    private val _medicalVisits = MutableLiveData<List<MedicalVisit>>()
    val medicalVisits: LiveData<List<MedicalVisit>> = _medicalVisits

    private val _appointments = MutableLiveData<List<Appointment>>()
    val appointments: LiveData<List<Appointment>> = _appointments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData<String?>()

    private val _navigateToAddAppointmentEvent = MutableLiveData<Unit>()
    val navigateToAddAppointmentEvent: LiveData<Unit> = _navigateToAddAppointmentEvent

    private val _navigateToAddMedicalVisitEvent = MutableLiveData<Unit>()
    val navigateToAddMedicalVisitEvent: LiveData<Unit> = _navigateToAddMedicalVisitEvent

    fun loadMedicalVisits() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val medicalVisits = getMedicalVisitsUseCase()
                val appointments = getAppointmentsUseCase()
                _medicalVisits.value = medicalVisits
                _appointments.value = appointments

            } catch (e: Exception) {
                Timber.e(e, "Error loading data")
                error.value = "Failed to load visits: ${e.message}"
            } finally {
                Timber.d("Finished loadMedicalVisits")
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            try {
                val medicalVisits = getMedicalVisitsUseCase().filter {
                    it.diagnosis.contains(query, ignoreCase = true) ||
                            it.doctorName.contains(query, ignoreCase = true) ||
                            it.clinicName.contains(query, ignoreCase = true)
                }
                val appointments = getAppointmentsUseCase().filter {
                    (it.note?.contains(query, ignoreCase = true) ?: false) ||
                            it.doctorName.contains(query, ignoreCase = true) ||
                            it.location.contains(query, ignoreCase = true)
                }
                _medicalVisits.value = medicalVisits
                _appointments.value = appointments
            } catch (e: Exception) {
                error.value = "Search failed: ${e.message}"
            }
        }
    }

    fun navigateToAddAppointment() {
        _navigateToAddAppointmentEvent.value = Unit
    }

    fun navigateToAddMedicalVisit() {
        _navigateToAddMedicalVisitEvent.value = Unit
    }

    fun getLoadingVisibility(): Int {
        return if (isLoading.value == true) View.VISIBLE else View.GONE
    }

    fun getMedicalVisitsEmptyVisibility(): Int {
        return if (medicalVisits.value?.isEmpty() == true) View.VISIBLE else View.GONE
    }

    fun getAppointmentsEmptyVisibility(): Int {
        return if (appointments.value?.isEmpty() == true) View.VISIBLE else View.GONE
    }
}