package com.example.healthcareproject.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.ui.auth.AuthViewModel

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFirstName = view.findViewById<EditText>(R.id.et_first_name)
        val etLastName = view.findViewById<EditText>(R.id.et_last_name)
        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = view.findViewById<EditText>(R.id.et_confirm_password)

        view.findViewById<View>(R.id.btn_create_account).setOnClickListener {
            viewModel.firstName = etFirstName.text.toString()
            viewModel.lastName = etLastName.text.toString()
            viewModel.email = etEmail.text.toString()
            viewModel.password = etPassword.text.toString()

            // Kiểm tra confirm password (thay bằng logic thực tế)
            if (etPassword.text.toString() == etConfirmPassword.text.toString()) {
                findNavController().navigate(R.id.action_registerFragment_to_verifyCodeFragment)
            } else {
                // Hiển thị lỗi nếu confirm password không khớp
            }
        }
    }
}