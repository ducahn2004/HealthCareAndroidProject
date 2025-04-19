package com.example.healthcareproject.present.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.healthcareproject.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EmergencyContactDialogFragment : DialogFragment() {

    private var contact: EmergencyContact? = null
    private var onSaveListener: ((EmergencyContact) -> Unit)? = null

    companion object {
        private const val ARG_CONTACT = "contact"

        fun newInstance(contact: EmergencyContact?): EmergencyContactDialogFragment {
            val fragment = EmergencyContactDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_CONTACT, contact)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contact = arguments?.getSerializable(ARG_CONTACT) as? EmergencyContact
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_emergency_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilName: TextInputLayout = view.findViewById(R.id.til_name)
        val etName: TextInputEditText = view.findViewById(R.id.et_name)
        val tilPhoneNumber: TextInputLayout = view.findViewById(R.id.til_phone_number)
        val etPhoneNumber: TextInputEditText = view.findViewById(R.id.et_phone_number)
        val spRelationship: Spinner = view.findViewById(R.id.sp_relationship)
        val spPriority: Spinner = view.findViewById(R.id.sp_priority)
        val btnSave: Button = view.findViewById(R.id.btn_save)
        val btnCancel: Button = view.findViewById(R.id.btn_cancel)

        // Thiết lập Spinners
        val relationships = arrayOf("Parent", "Sibling", "Spouse", "Friend", "Other")
        spRelationship.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            relationships
        )

        val priorities = arrayOf("1", "2", "3", "4", "5")
        spPriority.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            priorities
        )

        // Điền dữ liệu nếu là chỉnh sửa
        contact?.let {
            etName.setText(it.name)
            etPhoneNumber.setText(it.phoneNumber)
            spRelationship.setSelection(relationships.indexOf(it.relationship))
            spPriority.setSelection(priorities.indexOf(it.priority.toString()))
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val relationship = spRelationship.selectedItem.toString()
            val priority = spPriority.selectedItem.toString().toInt()

            var isValid = true
            if (name.isEmpty()) {
                tilName.error = "Name is required"
                isValid = false
            } else {
                tilName.error = null
            }

            if (phoneNumber.isEmpty()) {
                tilPhoneNumber.error = "Phone number is required"
                isValid = false
            } else if (!phoneNumber.matches(Regex("^[0-9]{10,15}$"))) {
                tilPhoneNumber.error = "Invalid phone number"
                isValid = false
            } else {
                tilPhoneNumber.error = null
            }

            if (isValid) {
                val newContact = EmergencyContact(
                    id = contact?.id ?: System.currentTimeMillis(),
                    name = name,
                    phoneNumber = phoneNumber,
                    relationship = relationship,
                    priority = priority
                )
                onSaveListener?.invoke(newContact)
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnSaveListener(listener: (EmergencyContact) -> Unit) {
        this.onSaveListener = listener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}