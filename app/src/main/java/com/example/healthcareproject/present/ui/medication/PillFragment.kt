package com.example.healthcareproject.present.ui.medication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentPillBinding
import com.example.healthcareproject.present.navigation.MainNavigator
import com.example.healthcareproject.present.viewmodel.medication.PillViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PillFragment : Fragment() {
    private lateinit var binding: FragmentPillBinding
    private val viewModel: PillViewModel by viewModels()

    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pill,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupClickListeners()
        setupFragmentResultListener()
        observeErrors()
    }

    private fun setupViewPager() {
        val pagerAdapter = MedicationPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Current Medications"
                1 -> "Past Medications"
                else -> null
            }
        }.attach()
    }

    private fun setupClickListeners() {
        binding.fabAddMedication.setOnClickListener {
            Timber.d("FAB clicked: Showing AddMedicationDialogFragment")
            try {
                val dialog = AddMedicationDialogFragment.newInstance(
                    sourceFragment = AddMedicationDialogFragment.SOURCE_PILL_FRAGMENT
                )
                dialog.show(parentFragmentManager, "AddMedicationDialog")
            } catch (e: Exception) {
                Timber.e(e, "Failed to show AddMedicationDialogFragment")
                Toast.makeText(context, "Failed to open Add Medication: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(AddMedicationDialogFragment.RESULT_KEY_PILL_FRAGMENT) { _, bundle ->
            if (bundle.getBoolean("medicationAdded", false)) {
                viewModel.loadMedications()
                Toast.makeText(context, "Medication added successfully", Toast.LENGTH_SHORT).show()
            }
        }
        setFragmentResultListener(AddMedicationDialogFragment.RESULT_KEY_DEFAULT) { _, bundle ->
            if (bundle.getBoolean("medicationAdded", false)) {
                val source = bundle.getString("sourceFragment")
                if (source == null || source == AddMedicationDialogFragment.SOURCE_PILL_FRAGMENT) {
                    viewModel.loadMedications()
                    Toast.makeText(context, "Medication added successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeErrors() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMedications()
    }
}