package com.example.healthcareproject.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.MainActivity
import com.example.healthcareproject.R

class VerifyCodeFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_verify_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCode = view.findViewById<EditText>(R.id.et_code)

        view.findViewById<View>(R.id.btn_verify).setOnClickListener {
            viewModel.verificationCode = etCode.text.toString()

            // Giả sử mã xác nhận đúng (thay bằng logic thực tế)
            if (viewModel.verificationCode == "1234") {
                // Nếu đang trong luồng đăng ký
                if (viewModel.isLoginSuccessful) {
                    saveLoginState(true)
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    // Nếu đang trong luồng quên mật khẩu
                    findNavController().navigate(R.id.action_verifyCodeFragment_to_createNewPasswordFragment)
                }
            } else {
                // Hiển thị lỗi nếu mã không đúng
            }
        }
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }
}