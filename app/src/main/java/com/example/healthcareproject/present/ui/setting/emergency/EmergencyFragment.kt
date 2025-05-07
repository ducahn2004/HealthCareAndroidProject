package com.example.healthcareproject.present.ui.setting.emergency

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.DialogEmergencyContactBinding
import com.example.healthcareproject.databinding.FragmentEmergencyBinding
import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.model.Relationship
import com.example.healthcareproject.present.ui.setting.emergency.EmergencyContactAdapter
import com.example.healthcareproject.present.viewmodel.setting.EmergencyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmergencyViewModel by viewModels()
    private lateinit var adapter: EmergencyContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeContacts()
        setupBackButton()
    }

    private fun setupRecyclerView() {
        adapter = EmergencyContactAdapter(
            onEditClick = { contact ->
                viewModel.prepareEditContact(contact.emergencyId)
                showAddEditDialog()
            },
            onDeleteClick = { contact ->
                showDeleteConfirmationDialog(contact)
            },
            onItemClick = { contact ->
                viewModel.prepareEditContact(contact.emergencyId)
                showAddEditDialog()
            }
        )
        binding.rvEmergencyContacts.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@EmergencyFragment.adapter
        }
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            viewModel.resetDialogInputs()
            showAddEditDialog()
        }
    }

    private fun setupBackButton() {
        binding.icBackEmergencyToSettings.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeContacts() {
        viewModel.contacts.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EmergencyViewModel.ContactsUiState.Loading -> {
                    Log.d("EmergencyFragment", "Loading contacts")
                }
                is EmergencyViewModel.ContactsUiState.Success -> {
                    try {
                        adapter.submitList(state.contacts)
                        Log.d("EmergencyFragment", "Updated contacts: ${state.contacts.size}")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error updating contacts: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("EmergencyFragment", "Error updating contacts: ${e.message}", e)
                    }
                }
                is EmergencyViewModel.ContactsUiState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    Log.e("EmergencyFragment", "Error state: ${state.message}")
                }
            }
        }
    }

    private fun showAddEditDialog() {
        val dialogBinding = DialogEmergencyContactBinding.inflate(layoutInflater)
        dialogBinding.viewmodel = viewModel
        dialogBinding.lifecycleOwner = viewLifecycleOwner

        // Setup Relationship Spinner
        val relationshipAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Relationship.entries.map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        dialogBinding.spRelationship.adapter = relationshipAdapter
        viewModel.selectedRelationship.observe(viewLifecycleOwner) { relationship ->
            dialogBinding.spRelationship.setSelection(relationship.ordinal)
        }
        dialogBinding.spRelationship.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setRelationship(Relationship.entries[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Setup Priority Spinner
        val priorityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf<Int>()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        dialogBinding.spPriority.adapter = priorityAdapter
        viewModel.availablePriorities.observe(viewLifecycleOwner) { priorities ->
            priorityAdapter.clear()
            priorityAdapter.addAll(priorities)
            priorityAdapter.notifyDataSetChanged()
            // Set default selection to the current selected priority or first available
            viewModel.selectedPriority.value?.let { selected ->
                val index = priorities.indexOf(selected)
                if (index >= 0) {
                    dialogBinding.spPriority.setSelection(index)
                }
            }
        }
        dialogBinding.spPriority.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.setPriority(priorityAdapter.getItem(position) ?: return)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Setup Dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSave.setOnClickListener {
            viewModel.addOrUpdateContact()
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(contact: EmergencyInfo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete ${contact.emergencyName}'s contact?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteContact(contact.emergencyId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}