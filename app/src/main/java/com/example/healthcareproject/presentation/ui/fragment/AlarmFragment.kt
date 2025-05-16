package com.example.healthcareproject.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentAlarmBinding
import com.example.healthcareproject.presentation.navigation.MainNavigator
import com.example.healthcareproject.presentation.ui.adapter.AlarmAdapter
import com.example.healthcareproject.presentation.viewmodel.home.alarm.ReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReminderViewModel by viewModels()
    private lateinit var adapter: AlarmAdapter
    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.ivBack.setOnClickListener {
            mainNavigator.navigateBackToHomeFromAlarm()
        }
    }

    private fun setupRecyclerView() {
        adapter = AlarmAdapter(
            onEditClick = { reminder ->
                EditAlarmDialog.newInstance(reminder).show(parentFragmentManager, "EditAlarmDialog")
            },
            onStatusChange = { reminderId, status ->
                viewModel.updateReminderStatus(reminderId, status)
            }
        )
        binding.recyclerViewAlarms.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAlarms.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.reminders.observe(viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }
        viewModel.loadReminders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}