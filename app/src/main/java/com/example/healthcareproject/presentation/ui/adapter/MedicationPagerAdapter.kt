package com.example.healthcareproject.presentation.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.healthcareproject.presentation.ui.fragment.CurrentMedicationsFragment
import com.example.healthcareproject.presentation.ui.fragment.PastMedicationsFragment

class MedicationPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CurrentMedicationsFragment()
            1 -> PastMedicationsFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}