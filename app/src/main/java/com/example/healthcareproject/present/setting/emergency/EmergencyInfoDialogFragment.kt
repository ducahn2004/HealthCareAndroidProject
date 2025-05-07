package com.example.healthcareproject.present.setting.emergency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.DialogEmergencyContactBinding
import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.model.Relationship
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyInfoDialogFragment : DialogFragment() {

    private var _binding: DialogEmergencyContactBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmergencyViewModel by viewModels()
    private var currentContact: EmergencyInfo? = null

    companion object {
        private const val ARG_CONTACT = "contact"

        fun newInstance(contact: EmergencyInfo? = null): EmergencyInfoDialogFragment {
            val fragment = EmergencyInfoDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_CONTACT, contact)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentContact = arguments?.getParcelable(ARG_CONTACT)
        viewModel.setContactForEdit(currentContact)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEmergencyContactBinding.inflate(inflater, container, false)
        // Set the viewmodel for databinding
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupButtons()
        observeViewModel()
    }

    private fun setupSpinners() {
        val relationships = Relationship.values().map { it.name }.toTypedArray()
        binding.spRelationship.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            relationships
        )

        val priorities = arrayOf("1", "2", "3", "4", "5")
        binding.spPriority.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            priorities
        )

        // Set initial selections based on viewModel data
        viewModel.relationship.observe(viewLifecycleOwner) { relationship ->
            val relationshipIndex = Relationship.values().indexOf(relationship)
            if (relationshipIndex >= 0) {
                binding.spRelationship.setSelection(relationshipIndex)
            }
        }

        viewModel.priority.observe(viewLifecycleOwner) { priority ->
            val priorityIndex = binding.spPriority.adapter.let { adapter ->
                (0 until adapter.count).indexOfFirst { adapter.getItem(it) == priority.toString() }
            }
            if (priorityIndex >= 0) {
                binding.spPriority.setSelection(priorityIndex)
            }
        }

        // Handle spinner selections
        binding.spRelationship.setOnItemSelectedListener { _, _, position, _ ->
            viewModel.relationship.value = Relationship.values()[position]
        }

        binding.spPriority.setOnItemSelectedListener { _, _, position, _ ->
            viewModel.priority.value = binding.spPriority.getItemAtPosition(position).toString().toInt()
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            viewModel.dialogState.value?.let { state ->
                when (state) {
                    is EmergencyViewModel.DialogUiState.EditContact -> {
                        viewModel.updateEmergencyContact(state.contact.emergencyId)
                    }
                    else -> {
                        viewModel.addEmergencyContact()
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.dialogState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EmergencyViewModel.DialogUiState.Error -> {
                    binding.tilName.error = if (state.message.contains("Name")) state.message else null
                    binding.tilPhoneNumber.error = if (state.message.contains("Phone")) state.message else null
                }
                else -> {
                    // Clear errors
                    binding.tilName.error = null
                    binding.tilPhoneNumber.error = null
                }
            }
        }

        viewModel.operationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EmergencyViewModel.OperationState.Success -> {
                    dismiss()
                }
                is EmergencyViewModel.OperationState.Error -> {
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                is EmergencyViewModel.OperationState.Loading -> {
                    binding.btnSave.isEnabled = false
                }
            }
        }
    }

    // Extension function for Spinner to easily set OnItemSelectedListener
    private fun android.widget.Spinner.setOnItemSelectedListener(
        onItemSelected: (parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
    ) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemSelected(parent, view, position, id)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}