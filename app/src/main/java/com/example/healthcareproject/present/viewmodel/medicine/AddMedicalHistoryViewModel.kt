package com.example.healthcareproject.present.viewmodel.medicine

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.usecase.medicalvisit.AddMedicalVisitWithMedicationsUseCase
import com.example.healthcareproject.present.ui.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddMedicalVisitViewModel @Inject constructor(
    private val addMedicalVisitWithMedicationsUseCase: AddMedicalVisitWithMedicationsUseCase,
    private val authDataSource: AuthDataSource
) : ViewModel() {

    // Observable fields for data binding
    val diagnosis = ObservableField<String>("")
    val doctorName = ObservableField<String>("")
    val clinicName = ObservableField<String>("")
    val visitDate = ObservableField<LocalDate>()
    val visitTime = ObservableField<Calendar>()
    val formattedVisitDate = ObservableField<String>("Select Date")
    val formattedVisitTime = ObservableField<String>("Select Time")
    val isLoading = ObservableField<Boolean>(false)

    // Medication list
    private val medications = mutableListOf<Medication>()

    // LiveData for UI events
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean> = _isFinished

    init {
        // Set up observers for date/time changes
        visitDate.addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                updateFormattedVisitDate()
            }
        })
        visitTime.addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                updateFormattedVisitTime()
            }
        })
    }

    fun setVisitDate(calendar: Calendar) {
        visitDate.set(calendar.time.toLocalDate())
    }

    fun setVisitTime(calendar: Calendar) {
        visitTime.set(calendar)
    }

    fun addMedication(medication: Medication) {
        medications.add(medication)
    }

    fun getMedications(): List<Medication> = medications.toList()

    fun saveMedicalVisit() {
        viewModelScope.launch {
            isLoading.set(true)
            val patientName = authDataSource.getCurrentUserId() ?: run {
                isLoading.set(false)
                _error.value = "User not logged in"
                return@launch
            }

            val medicationData = medications.map { medication ->
                medication.name to mapOf(
                    "dosageUnit" to medication.dosageUnit,
                    "dosageAmount" to medication.dosageAmount,
                    "frequency" to medication.frequency,
                    "timeOfDay" to medication.timeOfDay,
                    "mealRelation" to medication.mealRelation,
                    "startDate" to medication.startDate,
                    "endDate" to medication.endDate,
                    "notes" to medication.notes
                )
            }

            try {
                addMedicalVisitWithMedicationsUseCase(
                    patientName = patientName,
                    visitReason = clinicName.get() ?: "",
                    visitDate = visitDate.get() ?: LocalDate.now(),
                    doctorName = doctorName.get() ?: "",
                    diagnosis = diagnosis.get(),
                    status = true,
                    medications = medicationData
                )
                isLoading.set(false)
                _error.value = null
                _isFinished.value = true
            } catch (e: Exception) {
                Timber.e(e, "Failed to save medical visit")
                isLoading.set(false)
                _error.value = e.message ?: "Failed to save medical visit"
            }
        }
    }

    private fun updateFormattedVisitDate() {
        val date = visitDate.get()
        formattedVisitDate.set(date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Select Date")
    }

    private fun updateFormattedVisitTime() {
        val time = visitTime.get()
        formattedVisitTime.set(time?.let {
            val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
            formatter.format(it.time)
        } ?: "Select Time")
    }
}