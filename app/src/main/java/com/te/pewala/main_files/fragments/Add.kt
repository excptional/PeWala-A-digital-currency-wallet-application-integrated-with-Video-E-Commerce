package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.UPIPayment
import com.te.pewala.main_files.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import java.util.Locale


class Add : Fragment() {

    private val UPI_PAYMENT_REQUEST_CODE = 123
    private lateinit var amountEditText: TextInputEditText
    private lateinit var addMoney: CardView
    private lateinit var whiteView: View
    private lateinit var loaderAdd: LottieAnimationView
    private lateinit var walletId: TextView
    private lateinit var backBtn: ImageView
    private lateinit var dbViewModel: DBViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var myUser: FirebaseUser
    private lateinit var walletBalance: String
    private lateinit var mainLayout: RelativeLayout
//    private var limit = 0.0
    private lateinit var amount: String
    private lateinit var tId: String
    private lateinit var msg: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        amountEditText = view.findViewById(R.id.amount_add)
        addMoney = view.findViewById(R.id.add_money_btn_add)
        whiteView = view.findViewById(R.id.whiteView_add)
        loaderAdd = view.findViewById(R.id.loader_add)
        backBtn = view.findViewById(R.id.back_btn_add)
        walletId = view.findViewById(R.id.walletId_add)
        mainLayout = view.findViewById(R.id.main_layout_add)

        mainLayout.visibility = View.GONE

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        amountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (amountEditText.text!!.isEmpty()) {
                    amountEditText.hint = "00000"
                }
            } else {
                amountEditText.hint = null
            }
        }

        addMoney.setOnClickListener {

            Toast.makeText(requireContext(), "This feature is not implemented yet", Toast.LENGTH_SHORT).show()

//            whiteView.visibility = View.VISIBLE
//            loaderAdd.visibility = View.VISIBLE
//            amount = amountEditText.text.toString()
//
//            if (amount.isEmpty()) {
//                whiteView.visibility = View.GONE
//                loaderAdd.visibility = View.GONE
//                amountEditText.error = "Enter an amount"
//                Toast.makeText(requireContext(), "Enter an amount first", Toast.LENGTH_SHORT).show()
//            } else if (amount.toDouble() <= 0) {
//                whiteView.visibility = View.GONE
//                loaderAdd.visibility = View.GONE
//                amountEditText.error = "Enter an valid amount"
//                Toast.makeText(requireContext(), "Enter an valid amount", Toast.LENGTH_SHORT).show()
//            } else if (limit == 0.0) {
//                whiteView.visibility = View.GONE
//                loaderAdd.visibility = View.GONE
//                amountEditText.error = "Your daily add limit was reached, try again tomorrow"
//                Toast.makeText(
//                    requireContext(),
//                    "Your daily add limit was reached, try again tomorrow",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (amount.toDouble() > 5000) {
//                whiteView.visibility = View.GONE
//                loaderAdd.visibility = View.GONE
//                amountEditText.error = "Entered amount is over limit"
//                Toast.makeText(
//                    requireContext(),
//                    "You can't add more than 5000 rupees in your wallet in a day",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (amount.toDouble() > limit) {
//                whiteView.visibility = View.GONE
//                loaderAdd.visibility = View.GONE
//                amountEditText.error = "You can't add more than ${limit.toInt()} rupees"
//                Toast.makeText(
//                    requireContext(),
//                    "You can't add more than ${limit.toInt()} rupees according to your daily limit",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (amount.toDouble() + walletBalance.toDouble() > 20000) {
//                whiteView.visibility = View.GONE
//                loaderAdd.visibility = View.GONE
//                amountEditText.error = "You reach the maximum limit of your wallet"
//                Toast.makeText(
//                    requireContext(),
//                    "You reach the maximum limit of your wallet, your wallet limit is 20,000 rupees",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                tId = "TID${System.currentTimeMillis()}"
//                msg = "₹" + amount + " added in your smart wallet. " +
//                        "\nTransaction ID : " + tId
//                val payment = UPIPayment(
//                    vpa = "bd27official@okhdfcbank",
//                    name = "Trigredge",
//                    description = "Adding money to your Trigredge wallet",
//                    transactionId = tId,
//                    amount = "$amount.00"
//                )
//                whiteView.visibility = View.GONE
//                loaderAdd.visibility = View.GONE
//                val paymentIntent = startPayment(payment)
//                startActivityForResult(paymentIntent, UPI_PAYMENT_REQUEST_CODE)
//            }
        }
        return view
    }

    private fun startPayment(payment: UPIPayment): Intent {
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", payment.vpa)
            .appendQueryParameter("pn", payment.name)
            .appendQueryParameter("tn", payment.description)
            .appendQueryParameter("am", payment.amount)
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("tr", payment.transactionId)
            .appendQueryParameter("mc", "")
            .appendQueryParameter("url", "")
            .appendQueryParameter("mode", "UPI")
            .build()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri

        return Intent.createChooser(intent, "Pay with")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val response = data?.getStringExtra("response")
                    if (response != null && response.toLowerCase(Locale.ROOT).contains("success")) {
                        Toast.makeText(requireContext(), "Added money successfully", Toast.LENGTH_SHORT).show()
                        dbViewModel.addMoney(amount, "", tId, myUser.uid)
                        amountEditText.text = null
                        Handler().postDelayed({
                            sendNotification(requireContext(), "Money added successfully", msg)
                        }, 2000)
                    } else {
                        Toast.makeText(requireContext(), "UPI Payment failed", Toast.LENGTH_SHORT).show()
                    }

                }
                RESULT_CANCELED -> {
                    Toast.makeText(requireContext(), "UPI Payment cancelled", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "UPI Payment failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                myUser = it
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.exists()) {
                        walletBalance = list.getString("balance").toString()
                        walletId.text = list.getString("card_id").toString()
//                        dbViewModel.checkDailyAddAmountLimit(myUser)
//                        dbViewModel.dailyAddLimit.observe(viewLifecycleOwner) {
//                            limit = it
//                        }
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderAdd.visibility = View.GONE
                    } else {
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderAdd.visibility = View.GONE
                    }
                }
            }
        }
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