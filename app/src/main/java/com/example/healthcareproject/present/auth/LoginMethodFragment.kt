package com.example.healthcareproject.present.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentLoginMethodBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginMethodFragment : Fragment() {

    private var _binding: FragmentLoginMethodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginMethodBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe navigation events
        viewModel.navigateToRegister.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_loginMethodFragment_to_registerFragment)
                viewModel.resetNavigationStates()
            }
        }

        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_loginMethodFragment_to_loginFragment)
                viewModel.resetNavigationStates()
            }
        }

        viewModel.navigateToGoogleLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_loginMethodFragment_to_googleLoginFragment)
                viewModel.resetNavigationStates()
            }
        }

        binding.btnRegister.setOnClickListener {
            viewModel.navigateToRegister()
        }

        binding.btnLogin.setOnClickListener {
            viewModel.navigateToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}