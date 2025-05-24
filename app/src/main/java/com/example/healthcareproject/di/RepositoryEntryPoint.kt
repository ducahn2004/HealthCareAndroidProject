package com.example.healthcareproject.di

import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.AppointmentRepository
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.repository.NotificationRepository
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.domain.repository.UserRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {

    fun reminderRepository(): ReminderRepository

    fun notificationRepository(): NotificationRepository

    fun alertRepository(): AlertRepository

    fun appointmentRepository(): AppointmentRepository

    fun emergencyInfoRepository(): EmergencyInfoRepository

    fun medicalVisitRepository(): MedicalVisitRepository

    fun medicationRepository(): MedicationRepository

    fun userRepository(): UserRepository
}
