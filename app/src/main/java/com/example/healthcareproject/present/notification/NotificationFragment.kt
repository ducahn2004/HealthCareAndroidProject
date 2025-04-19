package com.example.healthcareproject.present.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NotificationAdapter
    private lateinit var notifications: MutableList<Notification>
    private lateinit var broadcastReceiver: BroadcastReceiver

    // Permission launcher for POST_NOTIFICATIONS
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startNotificationService()
        } else {
            Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo danh sách notifications với id duy nhất
        notifications = mutableListOf(
            Notification(id = 1, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 2, title = "ECG ALERT", message = "ECG is not stable. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_ecg),
            Notification(id = 3, title = "OXYGEN LEVEL ALERT", message = "Oxygen Level is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_oxygen),
            Notification(id = 4, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 5, title = "UPDATE WEIGHT", message = "Weight is 65KG. Keep yourself!", time = "10:24pm", iconResId = R.drawable.ic_weight),
            Notification(id = 6, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 7, title = "ECG ALERT", message = "ECG is not stable. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_ecg),
            Notification(id = 8, title = "OXYGEN LEVEL ALERT", message = "Oxygen Level is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_oxygen),
            Notification(id = 9, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 10, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 11, title = "ECG ALERT", message = "ECG is not stable. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_ecg),
            Notification(id = 12, title = "OXYGEN LEVEL ALERT", message = "Oxygen Level is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_oxygen),
            Notification(id = 13, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 14, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 15, title = "ECG ALERT", message = "ECG is not stable. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_ecg),
            Notification(id = 16, title = "OXYGEN LEVEL ALERT", message = "Oxygen Level is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_oxygen),
            Notification(id = 17, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 18, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            Notification(id = 19, title = "ECG ALERT", message = "ECG is not stable. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_ecg),
            Notification(id = 20, title = "OXYGEN LEVEL ALERT", message = "Oxygen Level is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_oxygen),
            Notification(id = 21, title = "HEART RATE ALERT", message = "Heart rate is too high. Need to Emergency!", time = "10:24pm", iconResId = R.drawable.ic_heart_rate),
            // Thêm các thông báo UPDATE WEIGHT mới
            Notification(id = 22, title = "UPDATE WEIGHT", message = "Weight increased to 66KG. Monitor your diet!", time = "09:15am", iconResId = R.drawable.ic_weight),
            Notification(id = 23, title = "UPDATE WEIGHT", message = "Weight decreased to 64KG. Good progress!", time = "08:30am", iconResId = R.drawable.ic_weight),
            Notification(id = 24, title = "UPDATE WEIGHT", message = "Weight is 65.5KG. Keep up the good work!", time = "07:45am", iconResId = R.drawable.ic_weight)
        )

        // Thiết lập RecyclerView
        adapter = NotificationAdapter { notification ->
            // Xử lý sự kiện nhấn vào thông báo
            when (notification.title) {
                "HEART RATE ALERT" -> findNavController().navigate(R.id.action_notificationFragment_to_heartRateFragment)
                "OXYGEN LEVEL ALERT" -> findNavController().navigate(R.id.action_notificationFragment_to_oxygenFragment)
                "ECG ALERT" -> findNavController().navigate(R.id.action_notificationFragment_to_ecgFragment)
                "UPDATE WEIGHT" -> findNavController().navigate(R.id.action_notificationFragment_to_weightFragment)
            }
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter

        // Thêm divider tùy chỉnh
        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        binding.rvNotifications.addItemDecoration(dividerItemDecoration)

        // Thiết lập ItemTouchHelper để quẹt xóa
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            // Nền đỏ khi quẹt
            private val background = ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
            // Biểu tượng thùng rác
            private val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
            private val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
            private val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Không hỗ trợ kéo thả
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                notifications.removeAt(position) // Xóa thông báo khỏi danh sách
                adapter.submitList(notifications.toList()) // Cập nhật danh sách

                // Cập nhật giao diện empty state
                if (notifications.isEmpty()) {
                    binding.rvNotifications.visibility = View.GONE
                    binding.emptyStateLayout.visibility = View.VISIBLE
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top

                // Vẽ nền đỏ
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                // Tính toán vị trí để vẽ biểu tượng thùng rác (căn giữa theo chiều dọc, cách mép phải 16dp)
                val iconMargin = 16
                val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val iconBottom = iconTop + intrinsicHeight
                val iconLeft = itemView.right - intrinsicWidth - iconMargin
                val iconRight = itemView.right - iconMargin

                deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                deleteIcon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvNotifications)
        // Check and request POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startNotificationService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startNotificationService()
        }

        // Setup BroadcastReceiver
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent?.getParcelableExtra("notification", Notification::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent?.getParcelableExtra("notification")
                }
                notification?.let {
                    notifications.add(0, it)
                    adapter.submitList(notifications.toList())
                    updateNotificationsList()
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("NEW_NOTIFICATION"))
        // Cập nhật danh sách ban đầu
        updateNotificationsList()
    }

    private fun startNotificationService() {
        val serviceIntent = Intent(requireContext(), NotificationService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
    }

    private fun updateNotificationsList() {
        if (notifications.isNotEmpty()) {
            binding.rvNotifications.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
            adapter.submitList(notifications.toList())
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