package com.example.healthcareproject.present.navigation

import android.os.Bundle
import androidx.navigation.NavController
import com.example.healthcareproject.R
import javax.inject.Inject

class MainNavigatorImpl @Inject constructor(
    private val navController: NavController
) : MainNavigator {

    override fun navigateToHeartRate() {
        navController.navigate(R.id.action_homeFragment_to_heartRateFragment)
    }

    override fun navigateToOxygen() {
        navController.navigate(R.id.action_homeFragment_to_oxygenFragment)
    }

    override fun navigateToEcg() {
        navController.navigate(R.id.action_homeFragment_to_ecgFragment)
    }

    override fun navigateToWeight() {
        navController.navigate(R.id.action_homeFragment_to_weightFragment)
    }

    override fun navigateBackToHomeFromHeartRate() {
        navController.navigate(R.id.action_back_heart_rate_to_home)
    }

    override fun navigateToNotificationFromHeartRate() {
        navController.navigate(R.id.action_back_heart_rate_to_notification)
    }

    override fun navigateBackToHomeFromOxygen() {
        navController.navigate(R.id.action_back_oxygen_to_home)
    }

    override fun navigateToNotificationFromOxygen() {
        navController.navigate(R.id.action_back_oxygen_to_notification)
    }

    override fun navigateToAddMedication() {
        navController.navigate(R.id.action_pillFragment_to_addMedicationFragment)
    }

    override fun navigatePillFragmentToMedicalHistoryDetail(visitId: String) {
        val bundle = Bundle().apply {
            putString("visitId", visitId)
            putString("sourceFragment", "PillFragment")
        }
        navController.navigate(R.id.action_pillFragment_to_medicalHistoryDetailFragment, bundle)
    }

    override fun navigateMedicineToMedicalHistoryDetail(visitId: String) {
        val bundle = Bundle().apply {
            putString("visitId", visitId)
            putString("sourceFragment", "MedicineFragment")
        }
        navController.navigate(R.id.action_medicineFragment_to_medicalHistoryDetailFragment, bundle)
    }

    override fun navigateToAddAppointment() {
        navController.navigate(R.id.action_medicineFragment_to_addAppointmentFragment)
    }

    override fun navigateBackMedicineFragmentFromMedicalHistoryDetail() {
        navController.navigate(R.id.action_back_medical_history_detail_to_medicine)
    }

    override fun navigateBackPillFragmentFromMedicalHistoryDetail() {
        navController.navigate(R.id.action_back_medical_history_detail_to_pill)
    }

    override fun navigateToAddMedicalVisit() {
        navController.navigate(R.id.action_medicineFragment_to_addMedicalVisitFragment)
    }

    override fun navigateBackToMedicineFromAddAppointment() {
        navController.navigate(R.id.action_addAppointmentFragment_to_medicineFragment)
    }

    override fun navigateBackToMedicineFromMedicalHistoryDetail() {
        navController.navigate(R.id.action_back_medical_history_detail_to_medicine)
    }

    override fun navigateBackToMedicineFromAddMedicalVisit() {
        navController.navigate(R.id.action_addMedicalVisitFragment_to_medicineFragment)
    }

    override fun navigateToHeartRateFromNotification() {
        navController.navigate(R.id.action_notificationFragment_to_heartRateFragment)
    }

    override fun navigateToOxygenFromNotification() {
        navController.navigate(R.id.action_notificationFragment_to_oxygenFragment)
    }

    override fun navigateToEcgFromNotification() {
        navController.navigate(R.id.action_notificationFragment_to_ecgFragment)
    }

    override fun navigateToWeightFromNotification() {
        navController.navigate(R.id.action_notificationFragment_to_weightFragment)
    }

    override fun navigateToTheme() {
        navController.navigate(R.id.action_settingsFragment_to_themeFragment)
    }

    override fun navigateToEmergency() {
        navController.navigate(R.id.action_settingsFragment_to_emergencyFragment)
    }

    override fun navigateToInformation() {
        navController.navigate(R.id.action_settingsFragment_to_informationFragment)
    }

    override fun navigateToChangePassword() {
        navController.navigate(R.id.action_settingsFragment_to_changePasswordFragment)
    }

    override fun navigateBackToSettingsFromTheme() {
        navController.navigate(R.id.action_themeFragment_to_settingsFragment)
    }

    override fun navigateBackToSettingsFromInformation() {
        navController.navigate(R.id.action_informationFragment_to_settingsFragment)
    }

    override fun navigateBackToSettingsFromEmergency() {
        navController.navigate(R.id.action_emergencyFragment_to_settingsFragment)
    }

    override fun navigateBackToSettingsFromChangePassword() {
        navController.navigate(R.id.action_changePasswordFragment_to_settingsFragment)
    }
}