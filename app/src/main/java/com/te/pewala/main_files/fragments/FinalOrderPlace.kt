package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
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
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.te.pewala.main_files.MainActivity
import kotlin.properties.Delegates

class FinalOrderPlace : Fragment() {

    private lateinit var closeBtn: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productId: String
    private lateinit var productName: TextView
    private lateinit var brandName: TextView
    private lateinit var quantity: TextView
    private lateinit var description: TextView
    private lateinit var placeOrder: Button
    private lateinit var plusBtn: ImageButton
    private lateinit var minusBtn: ImageButton
    private lateinit var updatedProductPrice: TextView
    private lateinit var totalPrice: TextView
    private lateinit var finalAmount: TextView
    private lateinit var address: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private var price = 0
    private var availableStocks by Delegates.notNull<Int>()
    private var count = 1
    private lateinit var uid: String
    private lateinit var addressStr: String
    private lateinit var locality: TextView
    private lateinit var city_postal: TextView
    private lateinit var state: TextView
    private lateinit var balanceStr: String
    private var finalAmountInt = 0

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_final_order_place, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        closeBtn = view.findViewById(R.id.close_btn_order_summary)
        productImage = view.findViewById(R.id.productImg_order_summary)
        productName = view.findViewById(R.id.productName_order_summary)
        brandName = view.findViewById(R.id.brandName_order_summary)
        quantity = view.findViewById(R.id.quantity_order_summary)
        description = view.findViewById(R.id.description_order_summary)
        updatedProductPrice = view.findViewById(R.id.updatedProductPrice_order_summary)
        totalPrice = view.findViewById(R.id.totalPrice_order_summary)
        plusBtn = view.findViewById(R.id.plus_order_summary)
        minusBtn = view.findViewById(R.id.minus_order_summary)
        finalAmount = view.findViewById(R.id.final_amount_order_summary)
        placeOrder = view.findViewById(R.id.placeOrder_btn_order_summary)
        address = view.findViewById(R.id.address_order_summary)
        whiteView = view.findViewById(R.id.whiteView_order_summary)
        loader = view.findViewById(R.id.loader_order_summary)
        locality = view.findViewById(R.id.area_order_summary)
        city_postal = view.findViewById(R.id.city_postal_order_summary)
        state = view.findViewById(R.id.state_order_summary)

        whiteView.visibility = View.VISIBLE
        loader.visibility = View.VISIBLE

        price = Integer.parseInt(requireArguments().getString("productPrice").toString())
        productId = requireArguments().getString("productId").toString()
        productName.text = requireArguments().getString("productName")
        brandName.text = requireArguments().getString("brandName")
        availableStocks = requireArguments().getString("quantity")!!.toInt()
        description.text = requireArguments().getString("description")
        Glide.with(view).load(requireArguments().getString("productImageUrl")).into(productImage)

        load(view)

        quantity.text = "1"

        var temp1: Int = price * count
        var temp2 = temp1 + 30
        finalAmountInt = temp2

        finalAmount.text = "₹$finalAmountInt"

        updatedProductPrice.text = "Product Price :  ₹$temp1"
        totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        plusBtn.setOnClickListener {
            if (count <= availableStocks) {
                count++
                quantity.text = "$count"
                temp1 = price * count
                temp2 = temp1 + 30
                finalAmountInt = temp2
                updatedProductPrice.text = "Product Price :  ₹$temp1"
                totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"
                finalAmount.text = "₹$temp2"
            } else {
                Toast.makeText(
                    requireContext(),
                    "You can't order more than 12 items",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        minusBtn.setOnClickListener {
            if (count > 1) {
                count--
                quantity.text = "$count"
                temp1 = price * count
                temp2 = temp1 + 30
                finalAmountInt = temp2
                updatedProductPrice.text = "Product Price :  ₹$temp1"
                totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"
                finalAmount.text = "₹$temp2"
            } else {
                Toast.makeText(
                    requireContext(),
                    "Already minimum no. of item selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        address.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_address)
        }

        placeOrder.setOnClickListener {
//            whiteView.visibility = View.VISIBLE
//            loader.visibility = View.VISIBLE
            showPaymentOptions(view)
//            order()
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun load(view: View) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                uid = it.uid
                dbViewModel.getAddress(uid)
                dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                    when (it) {
                        is Response.Success -> {
                            dbViewModel.addressData.observe(viewLifecycleOwner) { doc ->
                                if (doc.exists()) {
                                    locality.text = doc.getString("Locality")
                                    city_postal.text =
                                        doc.getString("City") + ", " + doc.getString("Postal Code")
                                    state.text = doc.getString("State")
                                    addressStr =
                                        "${doc.getString("Locality")}, ${doc.getString("Landmark")}, " +
                                                "${doc.getString("City")}, ${doc.getString("Postal Code")}, " +
                                                "${doc.getString("State")}"
                                    whiteView.visibility = View.GONE
                                    loader.visibility = View.GONE
                                    address.visibility = View.VISIBLE
                                }
                            }
                        }

                        is Response.Failure -> {
                            whiteView.visibility = View.GONE
                            loader.visibility = View.GONE
//                            Navigation.findNavController(view).popBackStack()
                            Navigation.findNavController(view).navigate(R.id.nav_address)
                        }
                    }
                }
                dbViewModel.fetchAccountDetails(uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) {
                    balanceStr = it.getString("Balance").toString()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun showPaymentOptions(view: View) {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setContentView(R.layout.payment_options_dialog)
        dialog.window?.attributes?.windowAnimations = R.anim.slide_up
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val avlBalance: TextView = dialog.findViewById(R.id.avl_balance_dialog)
        val warning: TextView = dialog.findViewById(R.id.warning_dialog)
        val digitalRupeePay: CardView = dialog.findViewById(R.id.pay_digital_rupees_card_dialog)
        val codPay: CardView = dialog.findViewById(R.id.pay_cod_card_dialog)
        val codText: TextView = dialog.findViewById(R.id.cod_text_dialog)
        val proceedBtn: Button = dialog.findViewById(R.id.proceed_btn_dialog)

        avlBalance.text = "Avl. Balance : $balanceStr"

        if (Integer.parseInt(balanceStr) < finalAmountInt) warning.visibility = View.VISIBLE

        var str = ""

        digitalRupeePay.setOnClickListener {
            digitalRupeePay.setCardBackgroundColor(Color.parseColor("#2196F3"))
            codPay.setCardBackgroundColor(Color.parseColor("#dddddd"))
            codText.setTextColor(Color.BLACK)
            str = "Digital Pay"
        }

        codPay.setOnClickListener {
            digitalRupeePay.setCardBackgroundColor(Color.BLACK)
            codPay.setCardBackgroundColor(Color.parseColor("#2196F3"))
            codText.setTextColor(Color.WHITE)
            str = "COD"
        }

        proceedBtn.setOnClickListener {
            when (str) {
                "Digital Pay" -> {
                    if (Integer.parseInt(balanceStr) < finalAmountInt) {
                        Toast.makeText(
                            requireContext(),
                            "You don't have sufficient balance in your smart wallet, add money or choose Cash On Delivery option",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val bundle = Bundle()
                        bundle.putString("amount", finalAmountInt.toString())
                        bundle.putString("quantity", count.toString())
                        bundle.putString("address", addressStr)
                        bundle.putString("productName", requireArguments().getString("productName"))
                        bundle.putString("brandName", requireArguments().getString("brandName"))
                        bundle.putString("productId", requireArguments().getString("productId"))
                        bundle.putString(
                            "productImageUrl",
                            requireArguments().getString("productImageUrl")
                        )
                        bundle.putString(
                            "productPrice",
                            requireArguments().getString("productPrice")
                        )
                        bundle.putString("category", requireArguments().getString("category"))
                        bundle.putString("sellerUid", requireArguments().getString("sellerUid"))
                        Navigation.findNavController(view).navigate(R.id.nav_digital_pay, bundle)
                        dialog.dismiss()
                    }
                }

                "COD" -> {
                    Toast.makeText(requireContext(), "COD is not available now", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Choose an option to proceed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }

    private fun order() {
        dbViewModel.fetchAccountDetails(uid)
        dbViewModel.accDetails.observe(viewLifecycleOwner) { doc ->
            dbViewModel.addOrder(
                doc.getString("Name").toString(),
                doc.getString("Phone").toString(),
                addressStr,
                "COD",
                uid,
                requireArguments().getString("brandName").toString(),
                requireArguments().getString("productName").toString(),
                requireArguments().getString("productImageUrl").toString(),
                productId,
                requireArguments().getString("category").toString(),
                requireArguments().getString("productPrice").toString(),
                count.toString(),
                requireArguments().getString("sellerUid").toString()
            )

            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
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
                        loader.visibility = View.GONE
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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, "default_channel_id")
            .setSmallIcon(R.drawable.pewala)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build())
    }

}