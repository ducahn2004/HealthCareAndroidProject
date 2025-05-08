package com.example.healthcareproject.present.ui.home

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.domain.model.Notification
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import com.example.healthcareproject.domain.usecase.notification.CreateNotificationUseCase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class HeartRateFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var lineChart: LineChart
    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvHeartRateValue: TextView
    private lateinit var tvBpmLabel: TextView
    private lateinit var tvMinValue: TextView
    private lateinit var tvMaxValue: TextView
    private lateinit var tvAverageLabel: TextView
    private lateinit var ivHeartIcon: ImageView

    private val heartRateData = mutableListOf<Float>()
    private val maxDataPoints = 20
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 5000L // 5s
    private lateinit var timeFrame: String
    private val timeStamps = mutableListOf<Long>()
    private var lastAlertTime: Long = 0

    @Inject
    lateinit var createNotificationUseCase: CreateNotificationUseCase

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!isAdded) return // Prevent running if fragment is detached
            val newHeartRate = Random.nextFloat() * (150f - 100f) + 100f
            heartRateData.add(newHeartRate)
            timeStamps.add(System.currentTimeMillis())

            if (heartRateData.size > maxDataPoints) {
                heartRateData.removeAt(0)
                timeStamps.removeAt(0)
            }

            updateChartData()
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_heart_rate, container, false)

        tabLayout = view.findViewById(R.id.tab_layout)
        lineChart = view.findViewById(R.id.chart_heart_rate)
        tvTitle = view.findViewById(R.id.tv_title)
        tvDate = view.findViewById(R.id.tv_date)
        tvHeartRateValue = view.findViewById(R.id.tv_heart_rate_value)
        tvBpmLabel = view.findViewById(R.id.tv_bpm_label)
        tvMinValue = view.findViewById(R.id.tv_min_value)
        tvMaxValue = view.findViewById(R.id.tv_max_value)
        tvAverageLabel = view.findViewById(R.id.tv_average_label)
        ivHeartIcon = view.findViewById(R.id.iv_heart_icon)

        val btnBack = view.findViewById<ImageView>(R.id.ic_back_heart_rate_to_home)
        btnBack.setOnClickListener {
            val previousDestinationId = findNavController().previousBackStackEntry?.destination?.id
            when (previousDestinationId) {
                R.id.notificationFragment -> {
                    findNavController().navigate(R.id.action_back_heart_rate_to_notification)
                }
                else -> {
                    findNavController().navigate(R.id.action_back_heart_rate_to_home)
                }
            }
        }

        val initialTime = System.currentTimeMillis()
        repeat(maxDataPoints) { index ->
            heartRateData.add(Random.nextFloat() * (150f - 100f) + 100f)
            timeStamps.add(initialTime - (maxDataPoints - 1 - index) * updateInterval)
        }

        timeFrame = "MINUTE"
        setupTabLayout()
        setupLineChart()
        updateChartData()

        handler.postDelayed(updateRunnable, updateInterval)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable)
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                timeFrame = when (tab?.position) {
                    0 -> "MINUTE"
                    1 -> "HOUR"
                    2 -> "DAY"
                    3 -> "WEEK"
                    else -> "MINUTE"
                }
                updateChartData()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupLineChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = false
            xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(true)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                textSize = 10f
                labelRotationAngle = 45f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                axisMaximum = 150f
            }
            axisRight.isEnabled = false

            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun updateChartData() {
        if (!isAdded) return // Prevent crash if fragment is detached

        val entries = heartRateData.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        val labels = when (timeFrame) {
            "MINUTE" -> Array(heartRateData.size) { index ->
                val timestamp = timeStamps[index]
                val seconds = (timestamp / 1000) % 60
                "${seconds}s"
            }
            "HOUR" -> Array(heartRateData.size) { index ->
                val timestamp = timeStamps[index]
                val minutes = (timestamp / 1000 / 60) % 60
                "${minutes}m"
            }
            "DAY" -> Array(heartRateData.size) { index ->
                val timestamp = timeStamps[index]
                val hours = (timestamp / 1000 / 3600) % 24
                "${hours}h"
            }
            "WEEK" -> Array(heartRateData.size) { index ->
                val timestamp = timeStamps[index]
                val day = (timestamp / 1000 / 86400).toInt() % 7
                when (day) {
                    0 -> "Mon"
                    1 -> "Tue"
                    2 -> "Wed"
                    3 -> "Thu"
                    4 -> "Fri"
                    5 -> "Sat"
                    6 -> "Sun"
                    else -> "Mon"
                }
            }
            else -> Array(heartRateData.size) { index ->
                val timestamp = timeStamps[index]
                val seconds = (timestamp / 1000) % 60
                "${seconds}s"
            }
        }

        val minValue = heartRateData.minOrNull() ?: 0f
        val maxValue = heartRateData.maxOrNull() ?: 0f
        val averageValue = if (heartRateData.isNotEmpty()) heartRateData.average().toFloat() else 0f
        tvHeartRateValue.text = "${heartRateData.lastOrNull()?.toInt() ?: 0}"
        tvMinValue.text = "${minValue.toInt()}"
        tvMaxValue.text = "${maxValue.toInt()}"
        tvAverageLabel.text = "Average ${averageValue.toInt()} BPM"

        val alertThreshold = 120f
        val currentHeartRate = heartRateData.lastOrNull() ?: 0f
        val currentTime = System.currentTimeMillis()
        val alertCooldown = 5_000L

        if (currentHeartRate > alertThreshold && (currentTime - lastAlertTime) > alertCooldown) {
            val notificationId = UUID.randomUUID().toString()
            val notification = Notification(
                notificationId = notificationId,
                userId = "", // Will be set by CreateNotificationUseCase
                type = NotificationType.Alert,
                relatedTable = RelatedTable.Measurement,
                relatedId = "heart_rate_$notificationId",
                message = "Heart rate is too high (${currentHeartRate.toInt()} BPM). Need to Emergency!",
                timestamp = LocalDateTime.now()
            )
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    createNotificationUseCase(
                        type = notification.type,
                        relatedTable = notification.relatedTable,
                        relatedId = notification.relatedId,
                        message = notification.message,
                        notificationTime = notification.timestamp
                    )
                    // Send to NotificationService
                    if (isServiceRunning()) {
                        val intent = Intent("HEART_RATE_ALERT")
                        intent.putExtra("notification", notification)
                        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                        lastAlertTime = currentTime
                    } else {
                        startNotificationService()
                    }
                } catch (e: Exception) {
                    // Handle error (e.g., log or show Toast)
                }
            }
        }

        val isAlert = heartRateData.any { it > alertThreshold }
        val gradientColors = if (isAlert) {
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.chart_gradient_alert_top),
                ContextCompat.getColor(requireContext(), R.color.chart_gradient_alert_bottom)
            )
        } else {
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.chart_gradient_normal_top),
                ContextCompat.getColor(requireContext(), R.color.chart_gradient_normal_bottom)
            )
        }

        val textColor = if (isAlert) R.color.alert_text_color else R.color.primary_text_color
        tvTitle.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvDate.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvHeartRateValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvBpmLabel.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvMinValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvMaxValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvAverageLabel.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        ivHeartIcon.setColorFilter(ContextCompat.getColor(requireContext(), textColor))

        val chartValueColor = if (isAlert) R.color.alert_text_color else R.color.chart_value_text_normal

        val dataSet = LineDataSet(entries, "Heart Rate (BPM)").apply {
            color = ContextCompat.getColor(requireContext(), R.color.chart_line_color)
            setCircleColor(Color.BLACK)
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(true)
            valueTextColor = ContextCompat.getColor(requireContext(), chartValueColor)
            valueTextSize = 10f
            enableDashedLine(10f, 5f, 0f)
            setDrawFilled(true)
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColors
            )
            fillDrawable = gradientDrawable
        }

        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun isServiceRunning(): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.healthcareproject.present.notification.NotificationService" == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun startNotificationService() {
        val serviceIntent = Intent(requireContext(), com.example.healthcareproject.present.service.MyFirebaseMessagingService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
    }
}