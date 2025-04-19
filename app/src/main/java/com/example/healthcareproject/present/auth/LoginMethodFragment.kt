package com.example.healthcareproject.present.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R

class LoginMethodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nút "Register"
        view.findViewById<View>(R.id.btn_register).setOnClickListener {
            findNavController().navigate(R.id.action_loginMethodFragment_to_registerFragment)
        }

        // Nút "Login"
        view.findViewById<View>(R.id.btn_login).setOnClickListener {
            findNavController().navigate(R.id.action_loginMethodFragment_to_loginFragment)
        }

        // Nút "Login with Google"
        view.findViewById<View>(R.id.google_login_container).setOnClickListener {
            findNavController().navigate(R.id.action_loginMethodFragment_to_googleLoginFragment)
        }
    }
}