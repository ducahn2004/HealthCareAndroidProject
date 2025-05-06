package com.example.healthcareproject.present.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.GoogleSignInUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _googleSignInTrigger = MutableLiveData<Unit>()
    val googleSignInTrigger: LiveData<Unit> = _googleSignInTrigger

    fun onGoogleLoginClicked() {
        _error.value = null
        _googleSignInTrigger.value = Unit
    }

    fun handleGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val firebaseAuth = FirebaseAuth.getInstance()
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                // Thử đăng nhập bằng Google
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                _isAuthenticated.value = true
                _error.value = null
            } catch (e: Exception) {
                if (e.message?.contains("account-exists-with-different-credential") == true) {
                    // Email đã tồn tại với phương thức khác (Email/Password)
                    val email = extractEmailFromError(e.message ?: "")
                    try {
                        // Kiểm tra phương thức đăng nhập của email
                        val signInMethods = firebaseAuth.fetchSignInMethodsForEmail(email).await()
                            .signInMethods ?: emptyList()

                        if (signInMethods.contains("password")) {
                            // Tự động liên kết tài khoản Google với tài khoản hiện có
                            val currentUser = firebaseAuth.currentUser
                            if (currentUser != null && currentUser.email == email) {
                                // Người dùng đã đăng nhập, liên kết trực tiếp
                                currentUser.linkWithCredential(credential).await()
                                _isAuthenticated.value = true
                                _error.value = null
                            } else {
                                // Đăng nhập lại với Email/Password (không cần mật khẩu) hoặc xử lý xung đột
                                // Trong trường hợp này, cần đăng nhập tạm thời bằng Google để liên kết
                                val tempAuthResult = firebaseAuth.signInWithCredential(credential).await()
                                tempAuthResult.user?.linkWithCredential(credential)?.await()
                                _isAuthenticated.value = true
                                _error.value = null
                            }
                        } else {
                            _error.value = "Email is registered with a different provider."
                        }
                    } catch (linkException: Exception) {
                        _error.value = linkException.message ?: "Failed to link Google account"
                    }
                } else {
                    _error.value = when {
                        e.message?.contains("credential is incorrect") == true ->
                            "Invalid or expired Google credentials. Please try signing in again."
                        e.message?.contains("expired") == true ->
                            "Google Sign-In token has expired. Please try again."
                        else -> e.message ?: "Google Sign-In failed"
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Trích xuất email từ thông báo lỗi
    private fun extractEmailFromError(errorMessage: String): String {
        // Điều chỉnh dựa trên định dạng lỗi của Firebase
        return errorMessage.substringAfter("email ").substringBefore(" ").trim()
    }

    fun setError(error: String?) {
        _error.value = error
    }

    fun resetNavigationStates() {
        _isAuthenticated.value = false
    }
}