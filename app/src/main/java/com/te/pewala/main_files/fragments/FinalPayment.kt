package com.te.pewala.main_files.fragments

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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.google.android.material.textfield.TextInputEditText
import com.te.pewala.db.AESCrypt
import com.te.pewala.db.LocalStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class FinalPayment : Fragment() {

    private val localStorage = LocalStorage()
    private lateinit var whiteView: View
    private lateinit var loaderFinalPay: LottieAnimationView
    private lateinit var mainLayout: LinearLayout
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var walletId: TextView
    private lateinit var amountEditText: TextInputEditText
    private lateinit var noteEditText: TextInputEditText
    private lateinit var payBtn: CardView
    private lateinit var profileImg: CircleImageView
    private lateinit var receiverUid: String
    private lateinit var senderUid: String
//    private lateinit var balance: String
    private lateinit var senderWalletId: String
    private lateinit var senderName: String
    private lateinit var senderPhone: String
    private lateinit var senderImageUrl: String
    private lateinit var receiverWalletId: String
    private lateinit var receiverName: String
    private lateinit var receiverPhone: String
    private lateinit var receiverImageUrl: String
    private lateinit var tId: String
    private lateinit var originalPIN: String
    private lateinit var amount: String
    private lateinit var note: String
    private lateinit var backBtn: ImageButton
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private val aesCrypt = AESCrypt()
    private val key = ByteArray(32)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_final_payment, container, false)

        name = view.findViewById(R.id.name_final_pay)
        phone = view.findViewById(R.id.phone_final_pay)
        walletId = view.findViewById(R.id.walletId_final_pay)
        amountEditText = view.findViewById(R.id.amount_final_pay)
        noteEditText = view.findViewById(R.id.note_final_pay)
        payBtn = view.findViewById(R.id.pay_btn_final_pay)
        profileImg = view.findViewById(R.id.img_final_pay)
        whiteView = view.findViewById(R.id.whiteView_final_pay)
        loaderFinalPay = view.findViewById(R.id.loader_final_pay)
        mainLayout = view.findViewById(R.id.main_layout_final_pay)
        backBtn = view.findViewById(R.id.back_btn_final_pay)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        key.fill(1)

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        walletId.text = requireArguments().getString("walletId").toString()

        payBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderFinalPay.visibility = View.VISIBLE
            pay()
        }

        return view
    }

    private fun getUid(receiverWalletId: String) {
        dbViewModel.getPayerDetails(receiverWalletId)
        dbViewModel.payerDetails.observe(viewLifecycleOwner) {
            receiverUid = it[3]
            receiverImageUrl = it[2]
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val userdata = localStorage.getData(requireContext(),"user_data")
        if (userdata!!["pin"]!!.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Set your 4 digit PIN before use this feature",
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().onBackPressed()
        } else {
            senderName = userdata["name"]!!
            senderPhone = userdata["phone"]!!
            senderWalletId = userdata["card_id"]!!
            senderImageUrl = userdata["image_url"]!!
//            balance = list1.getString("balance").toString()
            originalPIN = aesCrypt.decrypt(userdata["pin"]!!, key)!!
            dbViewModel.getPayerDetails(
                requireArguments().getString("walletId").toString()
            )

            dbViewModel.payerDetails.observe(viewLifecycleOwner) { list ->
                if (list.isNullOrEmpty()) {
                    whiteView.visibility = View.GONE
                    loaderFinalPay.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Your given phone number is not registered in this app",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().onBackPressed()
                } else {
                    receiverName = list[0]
                    receiverPhone = list[1]
                    receiverWalletId = "$receiverPhone@smart"
                    name.text = "Paying ${list[0]}"
                    phone.text = "Phone : +91 ${list[1]}"
                    Glide.with(this).load(list[2]).into(profileImg)
                    mainLayout.visibility = View.VISIBLE
                    whiteView.visibility = View.GONE
                    loaderFinalPay.visibility = View.GONE
                    getUid(walletId.text.toString())
                }
            }
        }
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

        payingText.text = "Paying  ₹$amount  to +91 $receiverPhone"

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
            else {
                dbViewModel.payment(senderUid, receiverUid, amount, note)
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
                            tId = "TID" + System.currentTimeMillis()
                            val time =
                                SimpleDateFormat(
                                    "MMM dd, yyyy 'at' HH:mm aa",
                                    Locale.getDefault()
                                ).format(
                                    Date()
                                )
                            val timeInMillis = System.currentTimeMillis().toString()
                            val bundle = Bundle()
                            bundle.putString("senderName", senderName)
                            bundle.putString("senderPhone", senderPhone)
                            bundle.putString("senderWalletId", senderWalletId)
                            bundle.putString("receiverName", receiverName)
                            bundle.putString("receiverPhone", receiverPhone)
                            bundle.putString("receiverWalletId", receiverWalletId)
                            bundle.putString("tid", tId)
                            bundle.putString("amount", amount)
                            bundle.putString("time", time)
                            dbViewModel.addTransaction(
                                amount,
                                note,
                                tId,
                                senderUid,
                                receiverUid,
                                senderName,
                                senderPhone,
                                senderImageUrl,
                                receiverName,
                                receiverPhone,
                                receiverImageUrl,
                                timeInMillis
                            )
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView())
                                .navigate(R.id.nav_success, bundle)
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

    private fun pay() {
        amount = amountEditText.text.toString()
        note = noteEditText.text.toString()

        if (amount.isEmpty()) {
            whiteView.visibility = View.GONE
            loaderFinalPay.visibility = View.GONE
            amountEditText.error = "Enter an amount"
            Toast.makeText(requireContext(), "Enter an amount first", Toast.LENGTH_SHORT).show()
        } else if (amount.toDouble() <= 0) {
            whiteView.visibility = View.GONE
            loaderFinalPay.visibility = View.GONE
            amountEditText.error = "Enter an valid amount"
            Toast.makeText(requireContext(), "Enter an valid amount", Toast.LENGTH_SHORT).show()
        } else if (amount.toDouble() > 5000) {
            whiteView.visibility = View.GONE
            loaderFinalPay.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                "You can't send more than 5000 rupees",
                Toast.LENGTH_SHORT
            ).show()
        }
//        else if (balance.toDouble() - amount.toDouble() <= 0) {
//            whiteView.visibility = View.GONE
//            loaderFinalPay.visibility = View.GONE
//            Toast.makeText(
//                requireContext(),
//                "You have insufficient balance to pay",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
        else {
            showDialog()
            whiteView.visibility = View.GONE
            loaderFinalPay.visibility = View.GONE
        }
    }

}