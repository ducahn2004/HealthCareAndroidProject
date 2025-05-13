package com.example.healthcareproject.present.ui.home.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentAlarmBinding
import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.present.navigation.MainNavigator
import com.example.healthcareproject.present.viewmodel.home.alarm.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlarmViewModel by viewModels()
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
            onEditClick = { alert ->
                EditAlarmDialog.newInstance(alert).show(parentFragmentManager, "EditAlarmDialog")
            },
            onStatusChange = { alertId, status ->
                viewModel.updateAlertStatus(alertId, status)
            }
        )
        binding.recyclerViewAlarms.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAlarms.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            adapter.submitList(alerts)
        }
        viewModel.loadAlarms()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}