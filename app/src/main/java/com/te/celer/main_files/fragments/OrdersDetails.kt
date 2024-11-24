package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.te.celer.R
import com.te.celer.db.AESCrypt
import com.te.celer.db.DBViewModel
import com.te.celer.db.Response

class OrdersDetails : Fragment() {

    private lateinit var deliveryDetails: TextView
    private lateinit var backBtn: ImageView
    private lateinit var brandName: TextView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productImg: ImageView
    private lateinit var buyerName: TextView
    private lateinit var address: TextView
    private lateinit var quantity: TextView
    private lateinit var orderTime: TextView
    private lateinit var deliveryTime: TextView
    private lateinit var acceptBtn: CardView
    private lateinit var rejectBtn: CardView
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private lateinit var selectedDate: String
    private val dbViewModel: DBViewModel by viewModels()
    private lateinit var orderId: TextView
    private lateinit var orderIdString: String
    private lateinit var sellerUidString: String
    private lateinit var buyerUidString: String
    private lateinit var btnLayout: LinearLayout
    private lateinit var decisionLayout: LinearLayout
    private lateinit var buyerLayout: LinearLayout
    private lateinit var codeLayout: LinearLayout
    private lateinit var confirmationLayout: RelativeLayout
    private lateinit var confirmationCodeET: TextInputEditText
    private lateinit var submitCode: CardView
    private lateinit var confirmationCode: String
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView
    private lateinit var text4: TextView
    private lateinit var text5: TextView
    private lateinit var text6: TextView
    private lateinit var statusText: TextView
    private lateinit var deliveryText: TextView
    private val aesCrypt = AESCrypt()
    val key = ByteArray(32)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders_details, container, false)

        key.fill(1)
        requireActivity().window.statusBarColor = Color.WHITE

        backBtn = view.findViewById(R.id.back_btn_order_details)
        brandName = view.findViewById(R.id.brandName_order_details)
        productName = view.findViewById(R.id.productName_order_details)
        productImg = view.findViewById(R.id.productImg_order_details)
        productPrice = view.findViewById(R.id.productPrice_order_details)
        buyerName = view.findViewById(R.id.buyerName_order_details)
        address = view.findViewById(R.id.address_order_details)
        quantity = view.findViewById(R.id.quantity_order_details)
        orderTime = view.findViewById(R.id.time_order_details)
        deliveryText = view.findViewById(R.id.delivery_text_order_details)
        deliveryDetails = view.findViewById(R.id.delivery_order_details)
        acceptBtn = view.findViewById(R.id.accept_btn_order_details)
        rejectBtn = view.findViewById(R.id.reject_btn_order_details)
        whiteView = view.findViewById(R.id.whiteView_order_details)
        loader = view.findViewById(R.id.loader_order_details)
        orderId = view.findViewById(R.id.orderId_order_details)
        btnLayout = view.findViewById(R.id.btn_layout_order_details)
        decisionLayout = view.findViewById(R.id.decision_layout_order_details)
        buyerLayout = view.findViewById(R.id.buyer_layout_order_details)
        codeLayout = view.findViewById(R.id.code_layout_order_details)
        confirmationLayout = view.findViewById(R.id.confirmation_layout_order_details)
        submitCode = view.findViewById(R.id.submit_btn_order_details)
        confirmationCodeET = view.findViewById(R.id.confirmation_code_order_details)
        text1 = view.findViewById(R.id.text1)
        text2 = view.findViewById(R.id.text2)
        text3 = view.findViewById(R.id.text3)
        text4 = view.findViewById(R.id.text4)
        text5 = view.findViewById(R.id.text5)
        text6 = view.findViewById(R.id.text6)
        statusText = view.findViewById(R.id.status_order_details)
        deliveryText = view.findViewById(R.id.delivery_text_order_details)

        confirmationCode = requireArguments().getString("confirmationCode").toString()
        statusText.text = requireArguments().getString("status").toString()
        deliveryText.text = "Your order will be delivered by " + requireArguments().getString("deliveryDate").toString()
        buyerUidString = requireArguments().getString("buyerUid").toString()
        sellerUidString = requireArguments().getString("sellerUid").toString()
        orderIdString = requireArguments().getString("orderId").toString()
        brandName.text = requireArguments().getString("brandName").toString()
        productName.text = requireArguments().getString("productName").toString()
        productPrice.text = "₹" + requireArguments().getString("productPrice").toString()
        buyerName.text = "Ordered by " + requireArguments().getString("buyerName").toString()
        address.text = requireArguments().getString("address").toString()
        quantity.text = "Quantity - " + requireArguments().getString("quantity").toString()
        orderTime.text = "Ordered on " + requireArguments().getString("orderTime").toString()
        orderId.text = requireArguments().getString("orderId").toString()
        Glide.with(view).load(requireArguments().getString("productImg").toString())
            .into(productImg)

        val decryptedCode = aesCrypt.decrypt(confirmationCode, key).toString()

        if(statusText.text == "Pending") {
            deliveryText.text =
                "Will be delivered by " + requireArguments().getString("deliveryDate").toString()
            deliveryDetails.visibility = View.GONE
        } else if(statusText.text == "Delivered") {
            deliveryDetails.visibility = View.VISIBLE
            deliveryDetails.text =
                "Delivered on " + requireArguments().getString("deliveryDate").toString()
        } else
            deliveryText.visibility = View.GONE

        if (requireArguments().getString("user").toString() == "Seller") {
            if(statusText.text != "Delivered")
                confirmationLayout.visibility = View.VISIBLE

        } else {
            buyerLayout.visibility = View.VISIBLE
            if(statusText.text == "Delivered")
                codeLayout.visibility = View.GONE

            text1.text = decryptedCode[0].toString()
            text2.text = decryptedCode[1].toString()
            text3.text = decryptedCode[2].toString()
            text4.text = decryptedCode[3].toString()
            text5.text = decryptedCode[4].toString()
            text6.text = decryptedCode[5].toString()

        }

        submitCode.setOnClickListener {
            if(confirmationCodeET.text.toString() != decryptedCode) {
                Toast.makeText(requireContext(), "Entered wrong code, try again", Toast.LENGTH_SHORT).show()
            } else {
                whiteView.visibility = View.VISIBLE
                loader.visibility = View.VISIBLE
                dbViewModel.completeOrder(sellerUidString, buyerUidString, orderIdString)
                dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                    when (it) {
                        is Response.Success -> {
                            whiteView.visibility = View.GONE
                            loader.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Order is completed now",
                                Toast.LENGTH_SHORT
                            ).show()
                            confirmationLayout.visibility = View.GONE
                            statusText.text = "DELIVERED"
                            deliveryDetails.text =
                                "Order has been delivered"
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

        acceptBtn.setOnClickListener {
            showDialogAccept()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        rejectBtn.setOnClickListener {
            showDialogReject()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun showDialogAccept() {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_select_date)
        dialog.window?.attributes?.windowAnimations = R.anim.pop

        val calender: CalendarView = dialog.findViewById(R.id.calender_dialog)
        val chooseDateLayout: LinearLayout = dialog.findViewById(R.id.chooseDateLayout_dialog)
        val confirmationLayout: LinearLayout = dialog.findViewById(R.id.confirmationLayout_dialog)
        val yesBtn: CardView = dialog.findViewById(R.id.accept_btn_dialog)
        val noBtn: CardView = dialog.findViewById(R.id.reject_btn_dialog)
        val deliveryDate: TextView = dialog.findViewById(R.id.delivery_date_dialog)

        calender.minDate = System.currentTimeMillis() - 1000

        calender.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = "${getMonth(month + 1)} $dayOfMonth, $year"
            deliveryDate.text = "To deliver on $selectedDate"
            chooseDateLayout.visibility = View.GONE
            confirmationLayout.visibility = View.VISIBLE
//            dialog.hide()
        }

        yesBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            dbViewModel.acceptOrders(sellerUidString, buyerUidString, orderIdString, selectedDate)
            dialog.hide()
            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        requireActivity().onBackPressed()
                        Toast.makeText(
                            requireContext(),
                            "You accept the order\n(Order Id : $orderIdString)",
                            Toast.LENGTH_SHORT
                        ).show()
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

        noBtn.setOnClickListener {
            chooseDateLayout.visibility = View.VISIBLE
            confirmationLayout.visibility = View.GONE
        }

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogReject() {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_reject_order)
        dialog.window?.attributes?.windowAnimations = R.anim.pop

        val yesBtn: CardView = dialog.findViewById(R.id.yes_btn_dialog)
        val noBtn: CardView = dialog.findViewById(R.id.no_btn_dialog)
        val orderIdText: TextView = dialog.findViewById(R.id.orderId_dialog)

        orderIdText.text = "Order Id : $orderIdString"

        yesBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            dbViewModel.rejectOrders(sellerUidString, buyerUidString, orderIdString)
            dialog.hide()
            dbViewModel.dbResponse.observe(viewLifecycleOwner) {

                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        requireActivity().onBackPressed()
                        Toast.makeText(
                            requireContext(),
                            "You reject the order\n(Order Id : $orderIdString)",
                            Toast.LENGTH_SHORT
                        ).show()
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

        noBtn.setOnClickListener {
            dialog.hide()
        }

        dialog.show()
    }

    private fun getMonth(m: Int): String {
        return when (m) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> {
                ""
            }
        }
    }

}