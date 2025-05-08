package com.example.healthcareproject.present.ui.medicine

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentAddMedicalVisitBinding
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.present.ui.medication.AddMedicationDialogFragment
import com.example.healthcareproject.present.viewmodel.medicine.AddMedicalVisitViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddMedicalVisitFragment : Fragment() {

    private var _binding: FragmentAddMedicalVisitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMedicalVisitViewModel by viewModels()
    private lateinit var medicationAdapter: MedicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicalVisitBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        medicationAdapter = MedicationAdapter()
        binding.rvMedications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicationAdapter
        }
    }

    private fun setupClickListeners() {
        val calendar = Calendar.getInstance()

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    viewModel.setVisitDate(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.tvTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                    viewModel.setVisitTime(selectedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        binding.btnAddMedication.setOnClickListener {
            val dialog = AddMedicationDialogFragment.newInstance()
            dialog.setTargetFragment(this, REQUEST_CODE_ADD_MEDICATION)
            dialog.show(parentFragmentManager, "AddMedicationDialog")
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveMedicalVisit()
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isFinished.observe(viewLifecycleOwner) { isFinished ->
            if (isFinished == true) {
                Toast.makeText(requireContext(), "Medical visit saved successfully", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }

        viewModel.isLoading.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val isLoading = viewModel.isLoading.get() ?: false
                binding.btnSave.isEnabled = !isLoading
                binding.btnAddMedication.isEnabled = !isLoading
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_MEDICATION && resultCode == Activity.RESULT_OK) {
            val medication = data?.getParcelableExtra<Medication>(EXTRA_MEDICATION)
            if (medication != null) {
                viewModel.addMedication(medication)
                medicationAdapter.submitList(viewModel.getMedications())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_ADD_MEDICATION = 1001
        const val EXTRA_MEDICATION = "extra_medication"
    }
}

class MedicationAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<MedicationAdapter.ViewHolder>() {

    private var medications: List<Medication> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medication = medications[position]
        holder.bind(medication)
    }

    override fun getItemCount(): Int = medications.size

    fun submitList(newMedications: List<Medication>) {
        medications = newMedications
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(medication: Medication) {
            itemView.findViewById<TextView>(android.R.id.text1).text = "${medication.name} (${medication.dosageAmount} ${medication.dosageUnit})"
        }
    }
}