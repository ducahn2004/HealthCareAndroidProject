package com.example.healthcareproject.present.ui.medicine

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor() : ViewModel() {

    // Existing LiveData properties (assumed)
    val visitsBefore: LiveData<List<MedicalVisit>> = MutableLiveData()
    val visitsAfter: LiveData<List<MedicalVisit>> = MutableLiveData()
    val isLoading: LiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String?> = MutableLiveData()

    // Existing navigation event for add appointment
    private val _navigateToAddAppointmentEvent = MutableLiveData<Unit>()
    val navigateToAddAppointmentEvent: LiveData<Unit> = _navigateToAddAppointmentEvent

    // New navigation event for add medical visit
    private val _navigateToAddMedicalVisitEvent = MutableLiveData<Unit>()
    val navigateToAddMedicalVisitEvent: LiveData<Unit> = _navigateToAddMedicalVisitEvent

    // Existing methods (assumed)
    fun onSearchQueryChanged(query: String) {
        // Handle search query
    }

    fun loadMedicalVisits() {
        // Load visits
    }

    fun navigateToAddAppointment() {
        _navigateToAddAppointmentEvent.value = Unit
    }

    // New method for navigating to add medical visit
    fun navigateToAddMedicalVisit() {
        _navigateToAddMedicalVisitEvent.value = Unit
    }

    // Existing visibility methods (assumed)
    fun getLoadingVisibility(): Int {
        return if (isLoading.value == true) View.VISIBLE else View.GONE
    }

    fun getBeforeEmptyVisibility(): Int {
        return if (visitsBefore.value?.isEmpty() == true) View.VISIBLE else View.GONE
    }

    fun getAfterEmptyVisibility(): Int {
        return if (visitsAfter.value?.isEmpty() == true) View.VISIBLE else View.GONE
    }
}