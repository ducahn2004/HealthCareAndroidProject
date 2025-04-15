package com.example.healthcareproject.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcareproject.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmergencyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmergencyContactAdapter
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_emergency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_emergency_contacts)
        fabAdd = view.findViewById(R.id.fab_add_contact)


        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EmergencyContactAdapter(
            onEditClick = { contact ->
                // Mở dialog chỉnh sửa
                val dialog = EmergencyContactDialogFragment.newInstance(contact)
                dialog.setOnSaveListener { updatedContact ->
                    updateContact(updatedContact)
                }
                dialog.show(parentFragmentManager, "EditContactDialog")
            },
            onDeleteClick = { contact ->
                deleteContact(contact)
            }
        )
        recyclerView.adapter = adapter


        updateContactList()


        fabAdd.setOnClickListener {
            val dialog = EmergencyContactDialogFragment.newInstance(null)
            dialog.setOnSaveListener { newContact ->
                addContact(newContact)
            }
            dialog.show(parentFragmentManager, "AddContactDialog")
        }

        // Nút Back
        view.findViewById<View>(R.id.ic_back_emergency_to_settings).setOnClickListener {
            findNavController().navigate(R.id.action_emergencyFragment_to_settingsFragment)
        }
    }

    private fun addContact(contact: EmergencyContact) {
        val contacts = SharedPrefsHelper.getEmergencyContacts(requireContext()).toMutableList()
        contacts.add(contact)
        SharedPrefsHelper.saveEmergencyContacts(requireContext(), contacts)
        updateContactList()
    }

    private fun updateContact(contact: EmergencyContact) {
        val contacts = SharedPrefsHelper.getEmergencyContacts(requireContext()).toMutableList()
        val index = contacts.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            contacts[index] = contact
            SharedPrefsHelper.saveEmergencyContacts(requireContext(), contacts)
            updateContactList()
        }
    }

    private fun deleteContact(contact: EmergencyContact) {
        val contacts = SharedPrefsHelper.getEmergencyContacts(requireContext())
            .filter { it.id != contact.id }
        SharedPrefsHelper.saveEmergencyContacts(requireContext(), contacts)
        updateContactList()
    }

    private fun updateContactList() {
        val contacts = SharedPrefsHelper.getEmergencyContacts(requireContext())
            .sortedBy { it.priority }
        adapter.submitList(contacts)
    }
}