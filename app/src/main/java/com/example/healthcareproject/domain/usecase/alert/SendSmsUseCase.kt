package com.example.healthcareproject.domain.usecase.alert

import android.telephony.SmsManager
import com.example.healthcareproject.domain.usecase.emergencyinfo.GetEmergencyInfosUseCase
import javax.inject.Inject

class SendSmsUseCase @Inject constructor(
    private val smsManager: SmsManager,
    private val getEmergencyInfosUseCase: GetEmergencyInfosUseCase
) {
    suspend operator fun invoke(message: String) {
        // Fetch emergency contacts
        val emergencyInfos = getEmergencyInfosUseCase()

        // Sort contacts by priority (ascending)
        val sortedEmergencyInfos = emergencyInfos.sortedBy { it.priority }

        // Send SMS to each contact
        sortedEmergencyInfos.forEach { emergencyInfo ->
            smsManager.sendTextMessage(
                emergencyInfo.emergencyPhone,
                null,
                message,
                null,
                null
            )
        }
    }
}
// Trong onCreate Activity
//// Kiểm tra quyền trước
//if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
//!= PackageManager.PERMISSION_GRANTED) {
//
//    // Chưa có quyền → yêu cầu người dùng cấp
//    ActivityCompat.requestPermissions(
//        this,
//        arrayOf(Manifest.permission.SEND_SMS),
//        SMS_PERMISSION_REQUEST_CODE
//    )
//} else {
//    // Đã có quyền → gửi luôn nếu cần
//    sendSms("0123456789", "Tin nhắn SOS")
//}
//}
//
//// Xử lý kết quả cấp quyền
//override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//    if (requestCode == SMS_PERMISSION_REQUEST_CODE &&
//        grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        // Người dùng đã cấp quyền
//        sendSms("0123456789", "Tin nhắn SOS")
//    } else {
//        // Từ chối cấp quyền
//        Toast.makeText(this, "Không thể gửi SMS nếu không cấp quyền!", Toast.LENGTH_SHORT).show()
//    }
//}