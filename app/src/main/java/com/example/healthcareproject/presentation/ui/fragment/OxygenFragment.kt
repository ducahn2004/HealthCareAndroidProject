package com.example.healthcareproject.presentation.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R
import com.example.healthcareproject.presentation.viewmodel.measurement.SpO2ViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OxygenFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var lineChart: LineChart
    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvSpO2Value: TextView
    private lateinit var tvPercentLabel: TextView
    private lateinit var tvMinValue: TextView
    private lateinit var tvMaxValue: TextView
    private lateinit var tvAverageLabel: TextView
    private lateinit var ivSpO2Icon: ImageView

    private val spo2Data = mutableListOf<Float>()
    private val timeStamps = mutableListOf<Long>()

    private lateinit var timeFrame: String

    private val viewModel: SpO2ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_oxygen, container, false)

        tabLayout = view.findViewById(R.id.tab_layout)
        lineChart = view.findViewById(R.id.chart_spo2)
        tvTitle = view.findViewById(R.id.tv_title)
        tvDate = view.findViewById(R.id.tv_date)
        tvSpO2Value = view.findViewById(R.id.tv_spo2_value)
        tvPercentLabel = view.findViewById(R.id.tv_percent_label)
        tvMinValue = view.findViewById(R.id.tv_min_value)
        tvMaxValue = view.findViewById(R.id.tv_max_value)
        tvAverageLabel = view.findViewById(R.id.tv_average_label)
        ivSpO2Icon = view.findViewById(R.id.iv_spo2_icon)

        val btnBack = view.findViewById<ImageView>(R.id.ic_back_spo2_to_home)
        btnBack.setOnClickListener {
            val previousDestinationId = findNavController().previousBackStackEntry?.destination?.id
            when (previousDestinationId) {
                R.id.notificationFragment -> {
                    findNavController().navigate(R.id.action_back_oxygen_to_notification)
                }
                else -> {
                    findNavController().navigate(R.id.action_back_oxygen_to_home)
                }
            }
        }

        timeFrame = "MINUTE"
        setupTabLayout()
        setupLineChart()

        observeSpO2()

        return view
    }

    private fun observeSpO2() {
        viewModel.spO2History.observe(viewLifecycleOwner) { measurements ->
            if (measurements.isNullOrEmpty()) return@observe

            spo2Data.clear()
            timeStamps.clear()

            measurements.forEach {
                spo2Data.add(it.spO2)

                // Convert LocalDateTime to epoch millis
                val epochMillis = it.dateTime
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                timeStamps.add(epochMillis)
            }

            updateChartData()
        }
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
                textColor = ContextCompat.getColor(requireContext(), R.color.chart_axis_text_color)
                textSize = 10f
                labelRotationAngle = 45f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 80f
                axisMaximum = 100f
            }
            axisRight.isEnabled = false

            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateChartData() {
        if (spo2Data.isEmpty()) return

        val entries = spo2Data.mapIndexed { index, value -> Entry(index.toFloat(), value) }

        val labels = timeStamps.take(entries.size).map {
            when (timeFrame) {
                "MINUTE" -> "${(it / 1000) % 60}s"
                "HOUR" -> "${(it / 1000 / 60) % 60}m"
                "DAY" -> "${(it / 1000 / 3600) % 24}h"
                "WEEK" -> {
                    when ((it / 1000 / 86400).toInt() % 7) {
                        0 -> "Mon"; 1 -> "Tue"; 2 -> "Wed"; 3 -> "Thu"
                        4 -> "Fri"; 5 -> "Sat"; else -> "Sun"
                    }
                }
                else -> "${(it / 1000) % 60}s"
            }
        }

        val minValue = spo2Data.minOrNull() ?: 0f
        val maxValue = spo2Data.maxOrNull() ?: 0f
        val avgValue = spo2Data.average().toFloat()
        tvSpO2Value.text = "${spo2Data.last().toInt()}"
        tvMinValue.text = "${minValue.toInt()}"
        tvMaxValue.text = "${maxValue.toInt()}"
        tvAverageLabel.text = "Average ${avgValue.toInt()}%"

        val isAlert = spo2Data.any { it < 95f }

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
        listOf(
            tvTitle, tvDate, tvSpO2Value,
            tvPercentLabel, tvMinValue, tvMaxValue, tvAverageLabel
        ).forEach {
            it.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        }
        ivSpO2Icon.setColorFilter(ContextCompat.getColor(requireContext(), textColor))

        val chartValueColor = if (isAlert) R.color.alert_text_color else R.color.chart_value_text_normal

        val dataSet = LineDataSet(entries, "SpO2 (%)").apply {
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
            fillDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColors
            )
        }

        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        lineChart.data = LineData(dataSet)
        lineChart.invalidate()
    }
}