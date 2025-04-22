package com.example.healthcareproject.present.pill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.present.medicine.MedicalVisit
import com.example.healthcareproject.present.pill.Medication
import java.time.LocalDate
import java.util.*

class AddMedicationViewModel : ViewModel() {

    private val _medications = MutableLiveData<List<Medication>>(emptyList())
    val medications: LiveData<List<Medication>> get() = _medications

    private val _selectedDate = MutableLiveData<Calendar?>()
    val selectedDate: LiveData<Calendar?> get() = _selectedDate

    private val _selectedTime = MutableLiveData<Calendar?>()
    val selectedTime: LiveData<Calendar?> get() = _selectedTime

    private val _selectedStartDate = MutableLiveData<LocalDate?>()
    val selectedStartDate: LiveData<LocalDate?> get() = _selectedStartDate

    private val _selectedEndDate = MutableLiveData<LocalDate?>()
    val selectedEndDate: LiveData<LocalDate?> get() = _selectedEndDate

    fun addMedication(medication: Medication) {
        val currentMedications = _medications.value?.toMutableList() ?: mutableListOf()
        currentMedications.add(medication)
        _medications.value = currentMedications
    }

    fun setSelectedDate(calendar: Calendar) {
        _selectedDate.value = calendar
    }

    fun setSelectedTime(calendar: Calendar) {
        _selectedTime.value = calendar
    }

    fun setSelectedStartDate(date: LocalDate) {
        _selectedStartDate.value = date
    }

    fun setSelectedEndDate(date: LocalDate?) {
        _selectedEndDate.value = date
    }
}