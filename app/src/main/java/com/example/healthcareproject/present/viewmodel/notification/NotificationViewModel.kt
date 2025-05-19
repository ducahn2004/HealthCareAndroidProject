package com.example.healthcareproject.present.viewmodel.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Notification
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.usecase.notification.DeleteNotificationUseCase
import com.example.healthcareproject.domain.usecase.notification.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase
) : ViewModel() {

    // Class con để lưu thông báo với timestamp đã định dạng
    data class FormattedNotification(
        val notification: Notification,
        val formattedTimestamp: String
    ) {
        val notificationId: String get() = notification.notificationId
        val type: NotificationType get() = notification.type
        val message: String get() = notification.message
    }

    private val _notifications = MutableLiveData<List<FormattedNotification>>(emptyList())
    val notifications: LiveData<List<FormattedNotification>> get() = _notifications

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                val notificationsList = getNotificationsUseCase()
                val formattedNotifications = notificationsList.map { notification ->
                    FormattedNotification(
                        notification = notification,
                        formattedTimestamp = notification.timestamp.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        )
                    )
                }
                _notifications.postValue(formattedNotifications)
                _error.postValue(null)
            } catch (e: Exception) {
                _notifications.postValue(emptyList())
                _error.postValue("Failed to load notifications: ${e.message}")
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                deleteNotificationUseCase(notificationId)
                loadNotifications() // Reload sau khi xóa
                _error.postValue(null)
            } catch (e: Exception) {
                _error.postValue("Failed to delete notification: ${e.message}")
            }
        }
    }

    fun clearError() {
        _error.postValue(null)
    }
}