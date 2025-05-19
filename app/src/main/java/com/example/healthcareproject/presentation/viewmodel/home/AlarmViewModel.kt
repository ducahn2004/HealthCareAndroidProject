package com.example.healthcareproject.presentation.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Reminder
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.usecase.reminder.GetRemindersUseCase
import com.example.healthcareproject.domain.usecase.reminder.UpdateReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase
) : ViewModel() {

    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> get() = _reminders

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            try {
                val reminders = getRemindersUseCase()
                _reminders.postValue(reminders)
            } catch (e: Exception) {
                _reminders.postValue(emptyList())
            }
        }
    }

    fun updateReminderStatus(reminderId: String, status: Boolean) {
        viewModelScope.launch {
            val reminder = _reminders.value?.find { it.reminderId == reminderId } ?: return@launch
            updateReminderUseCase(
                reminderId = reminderId,
                title = reminder.title,
                message = reminder.message,
                reminderTime = reminder.reminderTime,
                repeatPattern = reminder.repeatPattern,
                startDate = reminder.startDate,
                endDate = reminder.endDate,
                status = status
            )
        }
    }

    fun updateReminder(
        reminderId: String,
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        startDate: LocalDate,
        endDate: LocalDate,
        status: Boolean
    ) {
        viewModelScope.launch {
            updateReminderUseCase(
                reminderId = reminderId,
                title = title,
                message = message,
                reminderTime = reminderTime,
                repeatPattern = repeatPattern,
                startDate = startDate,
                endDate = endDate,
                status = status
            )
        }
    }
}