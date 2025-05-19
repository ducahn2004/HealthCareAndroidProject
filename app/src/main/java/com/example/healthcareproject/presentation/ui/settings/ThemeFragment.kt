package com.example.healthcareproject.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthcareproject.R

class ThemeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_theme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = view.findViewById<RadioGroup>(R.id.rg_theme)
        val rbLight = view.findViewById<RadioButton>(R.id.rb_light)
        val rbDark = view.findViewById<RadioButton>(R.id.rb_dark)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)

        // Thiết lập trạng thái ban đầu của radio buttons dựa trên theme hiện tại
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> rbDark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> rbLight.isChecked = true
            else -> {
                val prefs = requireActivity().getSharedPreferences("theme_prefs", 0)
                val savedTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                if (savedTheme == AppCompatDelegate.MODE_NIGHT_YES) rbDark.isChecked = true
                else if (savedTheme == AppCompatDelegate.MODE_NIGHT_NO) rbLight.isChecked = true
            }
        }

        // Xử lý khi người dùng chọn theme
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_light -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    saveThemePreference(AppCompatDelegate.MODE_NIGHT_NO)
                }
                R.id.rb_dark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_themeFragment_to_settingsFragment)
        }
    }

    private fun saveThemePreference(themeMode: Int) {
        val prefs = requireActivity().getSharedPreferences("theme_prefs", 0)
        prefs.edit().putInt("theme_mode", themeMode).apply()
    }

    companion object {
        fun newInstance() = ThemeFragment()
    }
}