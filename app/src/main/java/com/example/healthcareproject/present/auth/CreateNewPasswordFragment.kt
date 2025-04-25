package com.example.healthcareproject.present.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthcareproject.present.MainActivity
import com.example.healthcareproject.R
import kotlinx.coroutines.launch

class CreateNewPasswordFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_new_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = view.findViewById<EditText>(R.id.et_confirm_password)

        view.findViewById<View>(R.id.btn_reset_password).setOnClickListener {
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            lifecycleScope.launch {
                if (password == confirmPassword) {
                    try {
                        viewModel.updatePassword(password)
                        saveLoginState(true)
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to update password: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    etConfirmPassword.error = "Passwords do not match"
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
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