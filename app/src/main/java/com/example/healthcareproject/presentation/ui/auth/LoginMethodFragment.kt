package com.example.healthcareproject.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.databinding.FragmentLoginMethodBinding
import com.example.healthcareproject.presentation.navigation.AuthNavigator
import com.example.healthcareproject.presentation.viewmodel.auth.LoginMethodViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginMethodFragment : Fragment() {

    private var _binding: FragmentLoginMethodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginMethodViewModel by activityViewModels()
    private lateinit var navigator: AuthNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginMethodBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        navigator = AuthNavigator(findNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe navigation events
        viewModel.navigateToGoogleLogin.observe(viewLifecycleOwner) { navigate: Boolean ->
            if (navigate) {
                navigator.fromLoginMethodToGoogleLogin()
                viewModel.resetNavigationStates()
            }
        }

        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate: Boolean ->
            if (navigate) {
                navigator.fromLoginMethodToLogin()
                viewModel.resetNavigationStates()
            }
        }

        viewModel.navigateToRegister.observe(viewLifecycleOwner) { navigate: Boolean ->
            if (navigate) {
                navigator.fromLoginMethodToRegister()
                viewModel.resetNavigationStates()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}