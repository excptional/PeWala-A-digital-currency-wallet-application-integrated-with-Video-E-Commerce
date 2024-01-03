package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.te.pewala.R

class RedeemConfirmation : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var massageText: TextView

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_redeem_confirmation, container, false)

        backBtn = view.findViewById(R.id.back_btn_redeem_confirmation)
        massageText = view.findViewById(R.id.text_massage_redeem_confirmation)

        val amount = requireArguments().getString("amount").toString()

        massageText.text = "Your redeem request of ₹$amount was submitted successfully. Amount will be send to your respective bank account soon."

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

}