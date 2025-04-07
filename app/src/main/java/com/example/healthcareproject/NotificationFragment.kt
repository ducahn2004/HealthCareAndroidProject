package com.example.healthcareproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcareproject.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dữ liệu giả lập (thay bằng dữ liệu thực tế từ API hoặc database)
        val notifications = listOf(
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("ECG ALERT", "ECG is not stable. Need to Emergency!", "10:24pm", R.drawable.ic_ecg),
            Notification("OXYGEN LEVEL ALERT", "Oxygen Level is too high. Need to Emergency!", "10:24pm", R.drawable.ic_oxygen),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("UPDATE WEIGHT", "Weight is 65KG. Keep yourself!", "10:24pm", R.drawable.ic_weight),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("ECG ALERT", "ECG is not stable. Need to Emergency!", "10:24pm", R.drawable.ic_ecg),
            Notification("OXYGEN LEVEL ALERT", "Oxygen Level is too high. Need to Emergency!", "10:24pm", R.drawable.ic_oxygen),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("ECG ALERT", "ECG is not stable. Need to Emergency!", "10:24pm", R.drawable.ic_ecg),
            Notification("OXYGEN LEVEL ALERT", "Oxygen Level is too high. Need to Emergency!", "10:24pm", R.drawable.ic_oxygen),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("ECG ALERT", "ECG is not stable. Need to Emergency!", "10:24pm", R.drawable.ic_ecg),
            Notification("OXYGEN LEVEL ALERT", "Oxygen Level is too high. Need to Emergency!", "10:24pm", R.drawable.ic_oxygen),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate),
            Notification("ECG ALERT", "ECG is not stable. Need to Emergency!", "10:24pm", R.drawable.ic_ecg),
            Notification("OXYGEN LEVEL ALERT", "Oxygen Level is too high. Need to Emergency!", "10:24pm", R.drawable.ic_oxygen),
            Notification("HEART RATE ALERT", "Heart rate is too high. Need to Emergency!", "10:24pm", R.drawable.ic_heart_rate)

        )

        // Thiết lập RecyclerView
        if (notifications.isNotEmpty()) {
            binding.rvNotifications.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE

            binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
            binding.rvNotifications.adapter = NotificationAdapter(notifications)

            // Thêm divider tùy chỉnh
            val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
            binding.rvNotifications.addItemDecoration(dividerItemDecoration)
        } else {
            binding.rvNotifications.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}