package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.te.celer.R
import com.te.celer.main_files.adapters.PendingPaymentsTabAdapter

class PendingPayments : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var backBtn: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_payments, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        viewPager = view.findViewById(R.id.viewPager_pending_payments)
        tabLayout = view.findViewById(R.id.tabLayout_pending_payments)
        backBtn = view.findViewById(R.id.back_btn_pending_payments)

        val adapter = PendingPaymentsTabAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending"
                1 -> "Completed"
                else -> null
            }
        }.attach()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    @SuppressLint("MissingInflatedId", "InflateParams")
    private fun createCustomTabView(position: Int): View {
        val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.item_custom_tab, null)
        val textView: TextView = tabView.findViewById(R.id.tabTitle)
        val indicatorView: View = tabView.findViewById(R.id.tabIndicator)

        // Set the text and customize based on position
        when (position) {
            0 -> {
                textView.text = "Pending"
                tabView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.t_2))
                indicatorView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.t_3))
            }
            1 -> {
                textView.text = "Completed"
                tabView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.t_3))
                indicatorView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.t_2))
            }
        }
        return tabView
    }

}