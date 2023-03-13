package com.example.trigeredgedigitalcurrencyproject.main_files

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.trigeredgedigitalcurrencyproject.R

class Wallet : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var addMoney: CardView
    private lateinit var walletBalance: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        backBtn = view.findViewById(R.id.back_btn_wallet)
        addMoney = view.findViewById(R.id.add_money_btn_wallet)
        walletBalance = view.findViewById(R.id.balance_wallet)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

}