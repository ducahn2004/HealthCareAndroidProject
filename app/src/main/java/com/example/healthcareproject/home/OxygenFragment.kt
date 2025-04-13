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

    private val spo2Data = mutableListOf<Float>() // Danh sách động để lưu 20 giá trị mới nhất
    private val maxDataPoints = 20 // Số lượng giá trị tối đa trên biểu đồ
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L // Cập nhật mỗi 1 giây
    private lateinit var timeFrame: String // Lưu khoảng thời gian hiện tại
    private val timeStamps = mutableListOf<Long>() // Lưu thời gian (timestamp) của từng giá trị

    private val updateRunnable = object : Runnable {
        override fun run() {
            // Tạo giá trị ngẫu nhiên cho SpO2 (80-100%)
            val newSpO2 = Random.nextFloat() * (100f - 80f) + 80f
            spo2Data.add(newSpO2)
            timeStamps.add(System.currentTimeMillis())

            // Nếu vượt quá 20 giá trị, xóa giá trị cũ nhất
            if (spo2Data.size > maxDataPoints) {
                spo2Data.removeAt(0)
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
        val view = inflater.inflate(R.layout.fragment_oxygen, container, false)

        // Khởi tạo các view
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

        // Thiết lập nút back
        val btnBack = view.findViewById<ImageView>(R.id.ic_back_spo2_to_home)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_back_spo2_to_home)
        }

        // Khởi tạo dữ liệu ban đầu
        val initialTime = System.currentTimeMillis()
        repeat(maxDataPoints) { index ->
            spo2Data.add(Random.nextFloat() * (100f - 80f) + 80f)
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
                axisMinimum = 80f // Giá trị SpO2 thường từ 80% trở lên
                axisMaximum = 100f
            }
            axisRight.isEnabled = false

            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun updateChartData() {
        // Tạo entries từ dữ liệu hiện tại
        val entries = spo2Data.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        // Tạo nhãn cho trục X dựa trên khoảng thời gian
        val labels = when (timeFrame) {
            "MINUTE" -> {
                // Nhãn hiển thị giây trong chu kỳ 60 giây
                Array(spo2Data.size) { index ->
                    val timestamp = timeStamps[index]
                    val seconds = (timestamp / 1000) % 60 // Lấy giây trong chu kỳ 60 giây
                    "${seconds}s"
                }
            }
            "HOUR" -> {
                // Nhãn hiển thị phút trong chu kỳ 60 phút
                Array(spo2Data.size) { index ->
                    val timestamp = timeStamps[index]
                    val minutes = (timestamp / 1000 / 60) % 60 // Lấy phút trong chu kỳ 60 phút
                    "${minutes}m"
                }
            }
            else -> {
                Array(spo2Data.size) { index ->
                    val timestamp = timeStamps[index]
                    val seconds = (timestamp / 1000) % 60
                    "${seconds}s"
                }
            }
        }

        // Cập nhật giá trị min, max, average trên giao diện
        val minValue = spo2Data.minOrNull() ?: 0f
        val maxValue = spo2Data.maxOrNull() ?: 0f
        val averageValue = spo2Data.average().toFloat()
        tvSpO2Value.text = "${spo2Data.last().toInt()}" // Giá trị SpO2 hiện tại (giá trị cuối cùng)
        tvMinValue.text = "${minValue.toInt()}"
        tvMaxValue.text = "${maxValue.toInt()}"
        tvAverageLabel.text = "Average ${(averageValue.toInt())}%"

        // Kiểm tra cảnh báo (SpO2 < 95%)
        val alertThreshold = 95f
        val isAlert = spo2Data.any { it < alertThreshold }

        // Đổi màu chữ của các TextView
        val textColor = if (isAlert) R.color.alert_text_color else R.color.primary_text_color
        tvTitle.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvDate.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvSpO2Value.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvPercentLabel.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvMinValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvMaxValue.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        tvAverageLabel.setTextColor(ContextCompat.getColor(requireContext(), textColor))

        // Đổi màu của icon SpO2 để khớp với màu chữ
        ivSpO2Icon.setColorFilter(ContextCompat.getColor(requireContext(), textColor))

        // Đổi màu chữ của giá trị trên biểu đồ
        val chartValueColor = if (isAlert) R.color.alert_text_color else R.color.chart_value_text_blue

        // Đổi màu gradient fill của biểu đồ
        val gradientTopColor = if (isAlert) R.color.chart_gradient_alert_top else R.color.chart_gradient_top

        // Tạo dataset cho biểu đồ
        val dataSet = LineDataSet(entries, "SpO2 (%)").apply {
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
                intArrayOf(
                    resources.getColor(gradientTopColor, null),
                    Color.parseColor("#FFFFFF")
                )
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