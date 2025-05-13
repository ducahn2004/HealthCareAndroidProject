package com.example.healthcareproject.present.ui.medication

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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