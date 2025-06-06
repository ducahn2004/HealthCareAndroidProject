package com.example.healthcareproject.presentation.viewmodel.medicine

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.MedicationDataSource
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.usecase.medicalvisit.AddMedicalVisitWithMedicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddMedicalVisitViewModel @Inject constructor(
    private val addMedicalVisitWithMedicationsUseCase: AddMedicalVisitWithMedicationsUseCase,
    private val medicationDataSource: MedicationDataSource
) : ViewModel() {

    // Observable fields for data binding
    val diagnosis = ObservableField<String>("")
    val doctorName = ObservableField<String>("")
    val clinicName = ObservableField<String>("")
    val visitDateTime = ObservableField<LocalDateTime>()
    val formattedVisitDateTime = ObservableField<String>("Select Date and Time")
    val isLoading = ObservableField<Boolean>(false)

    // Medication list
    private val _medications = MutableLiveData<List<Medication>>(emptyList())
    val medications: LiveData<List<Medication>> = _medications

    // LiveData for UI events
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean> = _isFinished

    //Temporary VisitID
    private val visitId: String = UUID.randomUUID().toString()
    init {
        visitDateTime.addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                updateFormattedVisitDateTime()
            }
        })
        Timber.d("Initialized AddMedicalVisitViewModel with visitId: $visitId")
    }

    fun getVisitId(): String = visitId

    fun setVisitDateTime(dateTime: LocalDateTime) {
        visitDateTime.set(dateTime)
    }

    fun addMedication(medication: Medication) {
        val currentList = _medications.value?.toMutableList() ?: mutableListOf()
        val existingIndex = currentList.indexOfFirst {
            it.medicationId == medication.medicationId && medication.medicationId.isNotBlank()
        }
        if (existingIndex >= 0) {
            currentList[existingIndex] = medication
            Timber.d("Updated existing medication at index $existingIndex")
        } else {
            // Thêm mới nếu chưa tồn tại
            currentList.add(medication)
            Timber.d("Added new medication to list")
        }
        _medications.value = currentList
    }

    fun updateMedication(updatedMedication: Medication) {
        val currentList = _medications.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.medicationId == updatedMedication.medicationId }
        if (index != -1) {
            currentList[index] = updatedMedication
            _medications.value = currentList
        }
    }

    fun removeMedication(medication: Medication) {
        val currentList = _medications.value?.toMutableList() ?: mutableListOf()
        currentList.remove(medication)
        _medications.value = currentList
    }

    fun reorderMedications(from: Int, to: Int) {
        val currentList = _medications.value?.toMutableList() ?: mutableListOf()
        val medication = currentList.removeAt(from)
        currentList.add(to, medication)
        _medications.value = currentList
    }

    fun getMedications(): List<Medication> = _medications.value ?: emptyList()

    fun saveMedicalVisit() {
        if (isLoading.get() == true) return
        if (diagnosis.get().isNullOrBlank()) {
            _error.value = "Diagnosis is required"
            return
        }
        if (doctorName.get().isNullOrBlank()) {
            _error.value = "Doctor name is required"
            return
        }
        if (clinicName.get().isNullOrBlank()) {
            _error.value = "Facility is required"
            return
        }
        if (visitDateTime.get() == null) {
            _error.value = "Visit date and time are required"
            return
        }

        viewModelScope.launch {
            isLoading.set(true)

            val medicationData = getMedications().map { medication ->
                val timeOfDayList = when (val tod = medication.timeOfDay) {
                    is String -> tod.split(",").map { it.trim() }
                    is List<*> -> tod.filterIsInstance<String>()
                    else -> emptyList()
                }

                medication.name to mapOf(
                    "dosageUnit" to medication.dosageUnit,
                    "dosageAmount" to medication.dosageAmount,
                    "frequency" to medication.frequency,
                    "timeOfDay" to timeOfDayList,
                    "mealRelation" to medication.mealRelation,
                    "startDate" to medication.startDate,
                    "endDate" to medication.endDate,
                    "notes" to medication.notes,
                )
            }

            try {
                Timber.d("Saving MedicalVisit with visitId: $visitId")
                medicationData.forEach { (name, data) ->
                    Timber.d("Saving Medication with visitId: $visitId for medication: $name")
                }
                Timber.d("Preparing to save medications: $medications")
                addMedicalVisitWithMedicationsUseCase(
                    visitReason = clinicName.get() ?: "",
                    visitDate = visitDateTime.get()?.toLocalDate() ?: LocalDate.now(),
                    doctorName = doctorName.get() ?: "",
                    diagnosis = diagnosis.get(),
                    status = true,
                    medications = medicationData,
                    visitId = visitId
                )
                isLoading.set(false)
                _error.value = null
                _isFinished.value = true
            } catch (e: Exception) {
                Timber.e(e, "Failed to save medical visit: ${e.message}")
                isLoading.set(false)
                _error.value = e.message ?: "Failed to save medical visit"
            }
        }
    }

    private fun updateFormattedVisitDateTime() {
        val dateTime = visitDateTime.get()
        formattedVisitDateTime.set(
            dateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) ?: "Select Date and Time"
        )
    }

    override fun onCleared() {
        super.onCleared()
        medicationDataSource.removeListeners()
        Timber.d("AddMedicalVisitViewModel cleared, listeners removed")
    }
}