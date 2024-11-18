package com.te.celer.main_files.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.te.celer.main_files.fragments.Completed
import com.te.celer.main_files.fragments.Pending


class PendingPaymentsTabAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Pending()
            1 -> Completed()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}