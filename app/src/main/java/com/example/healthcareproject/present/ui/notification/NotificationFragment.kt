package com.example.healthcareproject.present.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentNotificationBinding
import com.example.healthcareproject.present.viewmodel.notification.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeAlerts()
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(viewModel)
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@NotificationFragment.adapter
        }
    }

    private fun observeAlerts() {
        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            adapter.submitList(alerts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}