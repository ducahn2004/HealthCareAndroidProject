package com.example.healthcareproject.present.medicine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicalVisitUseCases: MedicalVisitUseCases
) : ViewModel() {
    // LiveData objects for binding
    val isLoading = MutableLiveData(false)
    val visitsBefore = MutableLiveData<List<MedicalVisit>>(emptyList())
    val visitsAfter = MutableLiveData<List<MedicalVisit>>(emptyList())
    val error = MutableLiveData<String?>(null)

    // Visibility helpers for data binding
    val isVisitsBeforeEmpty = MutableLiveData(true)
    val isVisitsAfterEmpty = MutableLiveData(true)

    val navigateToAddAppointmentEvent = MutableLiveData<Unit>()

    // Cache for unfiltered lists
    private var allVisitsBefore: List<MedicalVisit> = emptyList()
    private var allVisitsAfter: List<MedicalVisit> = emptyList()

    init {
        loadMedicalVisits()
    }

    fun loadMedicalVisits() {
        viewModelScope.launch {
            isLoading.value = true

            try {
                // Get visits from use case
                val visits = medicalVisitUseCases.getMedicalVisitsUseCase()

                // Partition visits into before and after current date
                val (before, after) = visits.partition { visit ->
                    visit.visitDate.isBefore(LocalDate.now())
                }

                // Store in cache
                allVisitsBefore = before
                allVisitsAfter = after

                // Update LiveData
                updateVisitsLists(before, after)
                isLoading.value = false
                error.value = null
            } catch (e: Exception) {
                isLoading.value = false
                error.value = e.message
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            val filteredBefore = if (query.isBlank()) {
                allVisitsBefore
            } else {
                allVisitsBefore.filter { visit ->
                    visit.diagnosis.contains(query, ignoreCase = true) ||
                            visit.doctorName.contains(query, ignoreCase = true) ||
                            visit.clinicName.contains(query, ignoreCase = true)
                }
            }

            val filteredAfter = if (query.isBlank()) {
                allVisitsAfter
            } else {
                allVisitsAfter.filter { visit ->
                    visit.diagnosis.contains(query, ignoreCase = true) ||
                            visit.doctorName.contains(query, ignoreCase = true) ||
                            visit.clinicName.contains(query, ignoreCase = true)
                }
            }

            updateVisitsLists(filteredBefore, filteredAfter)
        }
    }

    private fun updateVisitsLists(before: List<MedicalVisit>, after: List<MedicalVisit>) {
        visitsBefore.value = before
        visitsAfter.value = after

        // Update visibility helpers
        isVisitsBeforeEmpty.value = before.isEmpty()
        isVisitsAfterEmpty.value = after.isEmpty()
    }

    fun navigateToAddAppointment() {
        // This will be implemented to navigate to add appointment screen
        // Implementation would depend on your navigation strategy
        navigateToAddAppointmentEvent.value = Unit
    }

    // Helper method to get loading visibility (1=VISIBLE, 8=GONE)
    fun getLoadingVisibility(): Int {
        return if (isLoading.value == true) 0 else 8
    }

    // Helper method for empty list visibility
    fun getBeforeEmptyVisibility(): Int {
        return if (isVisitsBeforeEmpty.value == true) 0 else 8
    }

    fun getAfterEmptyVisibility(): Int {
        return if (isVisitsAfterEmpty.value == true) 0 else 8
    }
}