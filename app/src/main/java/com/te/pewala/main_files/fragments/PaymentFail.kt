package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R

class PaymentFail : Fragment() {

    private lateinit var mainLayout: LinearLayout
    private lateinit var detailsLayout: LinearLayout
    private lateinit var animation: LottieAnimationView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment_fail, container, false)

        mainLayout = view.findViewById(R.id.mainLayout_fail)
        detailsLayout = view.findViewById(R.id.details_fail)
        animation = view.findViewById(R.id.animation_fail)

        val mp = MediaPlayer.create(requireContext(), R.raw.failure)
        mp.start()

        Handler().postDelayed({
            detailsLayout.visibility = View.VISIBLE
            val animate1 = TranslateAnimation(0F, 0F, view.height.toFloat(), 0F)
            animate1.duration = 50
            animate1.fillAfter = true
            detailsLayout.startAnimation(animate1)
        }, 3000)
        
        return view
    }
}