package com.example.healthcareproject.present.navigation

interface MainNavigator {

    fun navigateToHeartRate()
    fun navigateToOxygen()
    fun navigateToEcg()
    fun navigateToWeight()


    fun navigateBackToHomeFromHeartRate()
    fun navigateToNotificationFromHeartRate()

    fun navigateBackToHomeFromOxygen()
    fun navigateToNotificationFromOxygen()

    fun navigateToAddMedication()
    fun navigateToMedicalHistoryDetail(visitId: String)
    fun navigateToAddAppointment()

    fun navigateBackToMedicineFromAddAppointment()

    fun navigateToHeartRateFromNotification()
    fun navigateToOxygenFromNotification()
    fun navigateToEcgFromNotification()
    fun navigateToWeightFromNotification()

    fun navigateToTheme()
    fun navigateToEmergency()
    fun navigateToInformation()
    fun navigateToChangePassword()

    fun navigateBackToSettingsFromTheme()

    fun navigateBackToSettingsFromInformation()

    fun navigateBackToSettingsFromEmergency()

    fun navigateBackToSettingsFromChangePassword()
}