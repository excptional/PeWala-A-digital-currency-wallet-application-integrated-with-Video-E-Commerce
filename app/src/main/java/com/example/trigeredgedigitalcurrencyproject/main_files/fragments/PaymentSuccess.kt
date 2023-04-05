package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.example.trigeredgedigitalcurrencyproject.R

class PaymentSuccess : Fragment() {

    private lateinit var mainLayout: LinearLayout
    private lateinit var detailsLayout: LinearLayout
    private lateinit var animation: LottieAnimationView
    private lateinit var tid: TextView
    private lateinit var senderName: TextView
    private lateinit var senderPhone: TextView
    private lateinit var senderWalletId: TextView
    private lateinit var receiverName: TextView
    private lateinit var receiverPhone: TextView
    private lateinit var receiverWalletId: TextView
    private lateinit var amount: TextView
    private lateinit var time: TextView
    private lateinit var backBtn: CardView

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment_success, container, false)

        mainLayout = view.findViewById(R.id.mainLayout_success)
        detailsLayout = view.findViewById(R.id.details_success)
        animation = view.findViewById(R.id.animation_success)
        senderName = view.findViewById(R.id.name_from_success)
        senderPhone = view.findViewById(R.id.phone_from_success)
        senderWalletId = view.findViewById(R.id.wallet_id_from_success)
        receiverName = view.findViewById(R.id.name_to_success)
        receiverPhone = view.findViewById(R.id.phone_to_success)
        receiverWalletId = view.findViewById(R.id.wallet_id_to_success)
        amount= view.findViewById(R.id.amount_success)
        tid = view.findViewById(R.id.tid_success)
        time = view.findViewById(R.id.time_success)
        backBtn = view.findViewById(R.id.back_btn_success)

        senderName.text = requireArguments().getString("senderName")
        senderPhone.text = "From : +91 " + requireArguments().getString("senderPhone")
        senderWalletId.text = requireArguments().getString("senderWalletId")
        receiverName.text = requireArguments().getString("receiverName")
        receiverPhone.text = "To : +91 " + requireArguments().getString("receiverPhone")
        receiverWalletId.text = requireArguments().getString("receiverWalletId")
        tid.text = "Transaction ID : " + requireArguments().getString("tid")
        time.text = requireArguments().getString("time")
        amount.text = "₹" + requireArguments().getString("amount")

        val mp = MediaPlayer.create(requireContext(), R.raw.success)
        mp.start()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        Handler().postDelayed({
            detailsLayout.visibility = View.VISIBLE
            val animate1 = TranslateAnimation(0F, 0F, view.height.toFloat(), 0F)
            animate1.duration = 100
            animate1.fillAfter = true
            detailsLayout.startAnimation(animate1)
        }, 3000)

        return view
    }
}