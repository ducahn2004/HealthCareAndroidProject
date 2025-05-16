package com.example.healthcareproject.presentation.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.healthcareproject.presentation.ui.fragment.AppointmentsFragment
import com.example.healthcareproject.presentation.ui.fragment.MedicalVisitsFragment
import timber.log.Timber

class MedicinePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        Timber.d("Creating fragment for position: $position")
        return when (position) {
            0 -> AppointmentsFragment()
            1 -> MedicalVisitsFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}