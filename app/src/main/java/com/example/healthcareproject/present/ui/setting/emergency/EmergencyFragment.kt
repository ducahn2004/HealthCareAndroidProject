package com.example.healthcareproject.present.ui.setting.emergency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentEmergencyBinding
import com.example.healthcareproject.present.viewmodel.setting.EmergencyViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmergencyViewModel by viewModels()
    private lateinit var adapter: EmergencyInfoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        setupBackButton()
        observeViewModel()
        viewModel.fetchEmergencyContacts()
    }

    private fun setupRecyclerView() {
        binding.rvEmergencyContacts.layoutManager = LinearLayoutManager(context)
        adapter = EmergencyInfoAdapter(
            onEditClick = { contact ->
                viewModel.setContactForEdit(contact)
                val dialog = EmergencyInfoDialogFragment.newInstance(contact)
                dialog.show(parentFragmentManager, "EditContactDialog")
            },
            onDeleteClick = { contact ->
                viewModel.deleteEmergencyContact(contact.emergencyId)
            }
        )
        binding.rvEmergencyContacts.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            viewModel.setContactForEdit(null)
            val dialog = EmergencyInfoDialogFragment.newInstance()
            dialog.show(parentFragmentManager, "AddContactDialog")
        }
    }

    private fun setupBackButton() {
        binding.icBackEmergencyToSettings.setOnClickListener {
            findNavController().navigate(R.id.action_emergencyFragment_to_settingsFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.contacts.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EmergencyViewModel.ContactsUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is EmergencyViewModel.ContactsUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.contacts)
                }
                is EmergencyViewModel.ContactsUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.operationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EmergencyViewModel.OperationState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                }
                is EmergencyViewModel.OperationState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                is EmergencyViewModel.OperationState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}