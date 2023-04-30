package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
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
import androidx.core.app.NotificationCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.main_files.MainActivity

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
    private lateinit var title: String
    private lateinit var msg: String

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
        amount = view.findViewById(R.id.amount_success)
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

        title = "Payment successful"
        msg =
            "₹" + requireArguments().getString("amount") + " debited from your smart wallet to " +
                    requireArguments().getString("receiverWalletId") +"\nTransaction ID : " +
                    requireArguments().getString("tid")

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        Handler().postDelayed({
            detailsLayout.visibility = View.VISIBLE
            val animate1 = TranslateAnimation(0F, 0F, view.height.toFloat(), 0F)
            animate1.duration = 100
            animate1.fillAfter = true
            detailsLayout.startAnimation(animate1)
            sendNotification(requireContext(), title, msg)
        }, 3000)

        return view
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("fragmentToLoad", "Send")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channelName = "Default Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, "default_channel_id")
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build())
    }

}