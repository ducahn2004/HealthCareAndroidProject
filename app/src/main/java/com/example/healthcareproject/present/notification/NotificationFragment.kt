package com.example.healthcareproject.present.notification

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.example.healthcareproject.R
import com.example.healthcareproject.databinding.FragmentNotificationBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NotificationAdapter
    private lateinit var notifications: MutableList<Notification>
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var broadcastReceiver: android.content.BroadcastReceiver

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        context?.let { ctx ->
            if (isGranted) {
                startNotificationService(ctx)
            } else {
                Toast.makeText(
                    ctx,
                    "Please enable notifications in Settings",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
                }
                startActivity(intent)
            }
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

        notifications = loadNotifications()
        adapter = NotificationAdapter { notification ->
            if (!isAdded) return@NotificationAdapter
            when (notification.title) {
                "HEART RATE ALERT" -> findNavController().navigate(R.id.action_notificationFragment_to_heartRateFragment)
                "OXYGEN LEVEL ALERT" -> findNavController().navigate(R.id.action_notificationFragment_to_oxygenFragment)
                "ECG ALERT" -> findNavController().navigate(R.id.action_notificationFragment_to_ecgFragment)
                "UPDATE WEIGHT" -> findNavController().navigate(R.id.action_notificationFragment_to_weightFragment)
            }
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(context)
        binding.rvNotifications.adapter = adapter

        context?.let { ctx ->
            val dividerItemDecoration = DividerItemDecoration(ctx, LinearLayoutManager.VERTICAL)
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(ctx, R.drawable.divider)!!)
            binding.rvNotifications.addItemDecoration(dividerItemDecoration)

            val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private val background = ColorDrawable(ContextCompat.getColor(ctx, android.R.color.holo_red_light))
                private val deleteIcon = ContextCompat.getDrawable(ctx, R.drawable.ic_delete)
                private val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
                private val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (!isAdded) return
                    val position = viewHolder.adapterPosition
                    notifications.removeAt(position)
                    adapter.submitList(notifications.toList())
                    saveNotifications()
                    updateNotificationsList()
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
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)
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
        }

        context?.let { ctx ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        ctx,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startNotificationService(ctx)
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                startNotificationService(ctx)
            }

            localBroadcastManager = LocalBroadcastManager.getInstance(ctx)
            broadcastReceiver = object : android.content.BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (!isAdded || context == null) return // Prevent crash if fragment is detached
                    val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent?.getParcelableExtra("notification", Notification::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent?.getParcelableExtra("notification")
                    }
                    notification?.let {
                        notifications.add(0, it)
                        adapter.submitList(notifications.toList())
                        saveNotifications()
                        updateNotificationsList()
                    }
                }
            }
            localBroadcastManager.registerReceiver(broadcastReceiver, IntentFilter("NEW_NOTIFICATION"))
        }

        updateNotificationsList()
    }

    override fun onStop() {
        super.onStop()
        if (::localBroadcastManager.isInitialized && ::broadcastReceiver.isInitialized) {
            localBroadcastManager.unregisterReceiver(broadcastReceiver)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startNotificationService(context: Context) {
        val serviceIntent = Intent(context, NotificationService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun updateNotificationsList() {
        if (!isAdded || _binding == null) return
        _binding?.let { binding ->
            if (notifications.isNotEmpty()) {
                binding.rvNotifications.visibility = View.VISIBLE
                binding.emptyStateLayout.visibility = View.GONE
                adapter.submitList(notifications.toList())
            } else {
                binding.rvNotifications.visibility = View.GONE
                binding.emptyStateLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun saveNotifications() {
        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences("notifications", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            val json = Gson().toJson(notifications)
            editor.putString("notification_list", json)
            editor.apply()
        }
    }

    private fun loadNotifications(): MutableList<Notification> {
        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences("notifications", Context.MODE_PRIVATE)
            val json = prefs.getString("notification_list", null)
            return if (json != null) {
                Gson().fromJson(json, object : TypeToken<MutableList<Notification>>() {}.type)
            } else {
                mutableListOf()
            }
        }
        return mutableListOf()
    }
}