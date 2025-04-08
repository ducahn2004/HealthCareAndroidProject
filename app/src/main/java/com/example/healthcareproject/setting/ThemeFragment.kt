package com.example.healthcareproject.setting

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

        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> rbDark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> rbLight.isChecked = true
            else -> { // Trường hợp hệ thống tự động hoặc chưa thiết lập
                val prefs = requireActivity().getSharedPreferences("theme_prefs", 0)
                val isDark = prefs.getBoolean("is_dark_theme", false)
                if (isDark) rbDark.isChecked = true else rbLight.isChecked = true
            }
        }


        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_light -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    saveThemePreference(false)
                }
                R.id.rb_dark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    saveThemePreference(true)
                }
            }
        }

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_themeFragment_to_settingsFragment)
        }
    }

    private fun saveThemePreference(isDark: Boolean) {
        val prefs = requireActivity().getSharedPreferences("theme_prefs", 0)
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
    }

    companion object {
        fun newInstance() = ThemeFragment()
    }
}