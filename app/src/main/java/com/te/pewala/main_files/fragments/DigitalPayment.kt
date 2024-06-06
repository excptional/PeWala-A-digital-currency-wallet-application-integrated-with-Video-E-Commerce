package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.db.AESCrypt
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.te.pewala.main_files.MainActivity

class DigitalPayment : Fragment() {

    private val receiverImageUrl = "https://firebasestorage.googleapis.com/v0/b/pewala-a26d9.appspot.com/o/profile_images%2Ffv0ooafai0UGRWIK3EOesgQcUtq2%2Fcropped4716528955880745731.jpg?alt=media&token=63990896-4f8a-471e-a485-ea1e5b4361cc"
    private lateinit var senderName: String
    private lateinit var senderPhone: String
    private var receiverName: String = "PeWala"
    private var receiverPhone: String = "8621028795"
    private lateinit var senderWalletId: String
    private lateinit var senderImageUrl: String
    private lateinit var balance: String
    private lateinit var whiteView: View
    private lateinit var loaderFinalPay: LottieAnimationView
    private lateinit var mainLayout: RelativeLayout
    private lateinit var backBtn: ImageButton
    private lateinit var payBtn: CardView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var amount: TextView
    private lateinit var titleText: TextView
    private lateinit var senderUid: String
    private lateinit var amountStr: String
    private lateinit var originalPIN: String
    private lateinit var tId: String
    private var receiverUid = "fv0ooafai0UGRWIK3EOesgQcUtq2"
    private val aesCrypt = AESCrypt()
    private val key = ByteArray(32)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_digital_payment, container, false)

        payBtn = view.findViewById(R.id.pay_btn_digital_pay)
        whiteView = view.findViewById(R.id.whiteView_digital_pay)
        loaderFinalPay = view.findViewById(R.id.loader_digital_pay)
        mainLayout = view.findViewById(R.id.main_layout_digital_pay)
        backBtn = view.findViewById(R.id.back_btn_digital_pay)
        amount = view.findViewById(R.id.amount_digital_pay)
        titleText = view.findViewById(R.id.title_digital_pay)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        loadData()

        amountStr = requireArguments().getString("amount").toString()
        amount.text = "₹$amountStr"

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        payBtn.setOnClickListener {
            showDialog()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_enter_pin)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val pinEditText: EditText = dialog.findViewById(R.id.pinEditText)
        val submit: CardView = dialog.findViewById(R.id.submit_btn_PIN_dialog)
        val payingText: TextView = dialog.findViewById(R.id.paying_text_PIN_dialog)

        payingText.text = "Paying  ₹$amountStr  to Trigredge"

        submit.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderFinalPay.visibility = View.VISIBLE
            val pin = pinEditText.text.toString()

            if (pin.length != 4) {
                Toast.makeText(requireContext(), "Enter valid PIN", Toast.LENGTH_SHORT).show()
                whiteView.visibility = View.GONE
                loaderFinalPay.visibility = View.GONE
            } else if (pin != originalPIN) {
                Toast.makeText(requireContext(), "Entered wrong PIN, try again", Toast.LENGTH_SHORT)
                    .show()
                pinEditText.text = null
                whiteView.visibility = View.GONE
                loaderFinalPay.visibility = View.GONE
            }
//          else if(!BCrypt.checkpw(pin, originalPIN)) {
//                Toast.makeText(requireContext(), "Entered wrong PIN, try again", Toast.LENGTH_SHORT).show()
//                pinEditText.text = null
//                whiteView.visibility = View.GONE
//                loaderFinalPay.visibility = View.GONE
//          }
            else {
                dbViewModel.payToAdmin(amountStr, senderUid)
                dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                    when (it) {
                        is Response.Success -> {
                            whiteView.visibility = View.GONE
                            loaderFinalPay.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Payment successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val time = System.currentTimeMillis().toString()
                            tId = "TID$time"

                            dbViewModel.addTransaction(
                                amountStr,
                                "Pay to admin",
                                tId,
                                senderUid,
                                receiverUid,
                                senderName,
                                senderPhone,
                                senderImageUrl,
                                receiverName,
                                receiverPhone,
                                receiverImageUrl,
                                time
                            )
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView()).popBackStack()
                            order()
                            dialog.hide()
                        }

                        is Response.Failure -> {
                            whiteView.visibility = View.GONE
                            loaderFinalPay.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                it.errorMassage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

    private fun order() {
        dbViewModel.fetchAccountDetails(senderUid)
        dbViewModel.accDetails.observe(viewLifecycleOwner) { doc ->
            dbViewModel.addOrder(
                doc.getString("name").toString(),
                doc.getString("phone").toString(),
                requireArguments().getString("address").toString(),
                "Digital Payment",
                senderUid,
                requireArguments().getString("brandName").toString(),
                requireArguments().getString("productName").toString(),
                requireArguments().getString("productImageUrl").toString(),
                requireArguments().getString("productId").toString(),
                requireArguments().getString("category").toString(),
                requireArguments().getString("productPrice").toString(),
                requireArguments().getString("quantity").toString(),
                requireArguments().getString("sellerUid").toString()
            )

            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loaderFinalPay.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Your order placed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        sendNotification(requireContext(), "Your order successfully placed", "For ${
                            requireArguments().getString("brandName").toString()
                        } ${requireArguments().getString("productName").toString()}")
                        requireActivity().onBackPressed()
                    }

                    is Response.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            it.errorMassage,
                            Toast.LENGTH_SHORT
                        ).show()
                        whiteView.visibility = View.GONE
                        loaderFinalPay.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("orderPlaced", "order")
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
            val importance = NotificationManager.IMPORTANCE_HIGH
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                senderUid = it.uid
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list1 ->
                    if (list1.exists()) {
                        if (list1.getString("pin")!!.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Set your 4 digit PIN before use this feature",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().onBackPressed()
                        } else {
                            senderName = list1.getString("name").toString()
                            senderPhone = list1.getString("phone").toString()
                            senderWalletId = list1.getString("card_id").toString()
                            senderImageUrl = list1.getString("image_url").toString()
                            balance = list1.getString("balance").toString()
                            originalPIN = list1.getString("pin")!!
//                            originalPIN = aesCrypt.decrypt(list1.getString("pin").toString(), key).toString()
                            titleText.text = "Paying from $senderName \nFor ${
                                requireArguments().getString("brandName").toString()
                            } ${requireArguments().getString("productName").toString()}"
                        }
                        whiteView.visibility = View.GONE
                        loaderFinalPay.visibility = View.GONE
                        mainLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}