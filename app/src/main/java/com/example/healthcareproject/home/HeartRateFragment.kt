package com.example.healthcareproject.home

import android.graphics.Color
import android.graphics.DashPathEffect
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
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.example.healthcareproject.R
import kotlin.random.Random

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

    private val heartRateData = mutableListOf<Float>() // Danh sách động để lưu 20 giá trị mới nhất
    private val maxDataPoints = 20 // Số lượng giá trị tối đa trên biểu đồ
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L // Cập nhật mỗi 1 giây
    private lateinit var timeFrame: String // Lưu khoảng thời gian hiện tại
    private val timeStamps = mutableListOf<Long>() // Lưu thời gian (timestamp) của từng giá trị

    private val updateRunnable = object : Runnable {
        override fun run() {
            // Tạo giá trị ngẫu nhiên cho nhịp tim (40-120 BPM)
            val newHeartRate = Random.nextFloat() * (120f - 40f) + 40f
            heartRateData.add(newHeartRate)
            timeStamps.add(System.currentTimeMillis())

            // Nếu vượt quá 20 giá trị, xóa giá trị cũ nhất
            if (heartRateData.size > maxDataPoints) {
                heartRateData.removeAt(0)
                timeStamps.removeAt(0)
            }

            // Cập nhật biểu đồ và giao diện
            updateChartData()

            // Lên lịch cập nhật tiếp theo
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_heart_rate, container, false)

        // Khởi tạo các view
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

        // Thiết lập nút back
        val btnBack = view.findViewById<ImageView>(R.id.ic_back_heart_rate_to_home)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_back_heart_rate_to_home)
        }

        // Khởi tạo dữ liệu ban đầu
        val initialTime = System.currentTimeMillis()
        repeat(maxDataPoints) { index ->
            heartRateData.add(Random.nextFloat() * (120f - 40f) + 40f)
            timeStamps.add(initialTime - (maxDataPoints - 1 - index) * updateInterval)
        }

        timeFrame = "MINUTE" // Khởi tạo mặc định là Minute
        setupTabLayout()
        setupLineChart()
        updateChartData()

        // Bắt đầu cập nhật dữ liệu
        handler.postDelayed(updateRunnable, updateInterval)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Dừng cập nhật khi fragment bị hủy
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
                updateChartData() // Cập nhật biểu đồ khi chuyển tab
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

            xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(true) // Bật nhãn trục X
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                textSize = 10f
                labelRotationAngle = 45f // Xoay nhãn 45 độ để tránh chồng lấn
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                axisMaximum = 150f // Tối đa để phù hợp với nhịp tim
            }
            axisRight.isEnabled = false

            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun updateChartData() {
        // Tạo entries từ dữ liệu hiện tại
        val entries = heartRateData.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        // Tạo nhãn cho trục X dựa trên khoảng thời gian
        val labels = when (timeFrame) {
            "MINUTE" -> {
                Array(heartRateData.size) { index ->
                    val timestamp = timeStamps[index]
                    val seconds = (timestamp / 1000) % 60 // Lấy giây trong chu kỳ 60 giây
                    "${seconds}s"
                }
            }
            "HOUR" -> {
                Array(heartRateData.size) { index ->
                    val timestamp = timeStamps[index]
                    val minutes = (timestamp / 1000 / 60) % 60 // Lấy phút trong chu kỳ 60 phút
                    "${minutes}m"
                }
            }
            "DAY" -> {
                Array(heartRateData.size) { index ->
                    val timestamp = timeStamps[index]
                    val hours = (timestamp / 1000 / 3600) % 24 // Lấy giờ trong chu kỳ 24 giờ
                    "${hours}h"
                }
            }
            "WEEK" -> {
                Array(heartRateData.size) { index ->
                    val timestamp = timeStamps[index]
                    val day = (timestamp / 1000 / 86400).toInt() % 7 // Lấy ngày trong chu kỳ 7 ngày
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
            }
            else -> {
                Array(heartRateData.size) { index ->
                    val timestamp = timeStamps[index]
                    val seconds = (timestamp / 1000) % 60
                    "${seconds}s"
                }
            }
        }

        // Cập nhật giá trị min, max, average trên giao diện
        val minValue = heartRateData.minOrNull() ?: 0f
        val maxValue = heartRateData.maxOrNull() ?: 0f
        val averageValue = heartRateData.average().toFloat()
        tvHeartRateValue.text = "${heartRateData.last().toInt()}"
        tvMinValue.text = "${minValue.toInt()}"
        tvMaxValue.text = "${maxValue.toInt()}"
        tvAverageLabel.text = "Average ${(averageValue.toInt())} BPM"

        // Kiểm tra cảnh báo (nhịp tim > 120 BPM)
        val alertThreshold = 120f
        val isAlert = heartRateData.any { it > alertThreshold }

        // Chọn màu gradient dựa trên trạng thái cảnh báo
        val gradientColors = if (isAlert) {
            intArrayOf(
                resources.getColor(R.color.chart_gradient_alert_top, null),
                resources.getColor(R.color.chart_gradient_alert_bottom, null)
            )
        } else {
            intArrayOf(
                resources.getColor(R.color.chart_gradient_normal_top, null),
                resources.getColor(R.color.chart_gradient_normal_bottom, null)
            )
        }

        // Đổi màu chữ của các TextView và icon
        val textColor = if (isAlert) R.color.alert_text_color else R.color.primary_text_color
        tvTitle.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvDate.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvHeartRateValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvBpmLabel.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvMinValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvMaxValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvAverageLabel.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        ivHeartIcon.setColorFilter(ContextCompat.getColor(requireContext(), textColor))

        // Đổi màu chữ của giá trị trên biểu đồ
        val chartValueColor = if (isAlert) R.color.alert_text_color else R.color.chart_value_text_normal

        // Tạo dataset cho biểu đồ
        val dataSet = LineDataSet(entries, "Heart Rate (BPM)").apply {
            color = resources.getColor(R.color.chart_line_color, null)
            setCircleColor(Color.BLACK)
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)

            setDrawValues(true)
            valueTextColor = resources.getColor(chartValueColor, null)
            valueTextSize = 10f

            enableDashedLine(10f, 5f, 0f)

            setDrawFilled(true)
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColors
            )
            fillDrawable = gradientDrawable
        }

        // Cập nhật nhãn trục X
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Cập nhật dữ liệu biểu đồ
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}