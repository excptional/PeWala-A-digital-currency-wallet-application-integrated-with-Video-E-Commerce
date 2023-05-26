package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.db.Response
import com.example.trigeredgedigitalcurrencyproject.db.UPIPayment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import org.mindrot.jbcrypt.BCrypt
import java.text.SimpleDateFormat
import java.util.*

class Redeem : Fragment() {

    private lateinit var amountEditText: TextInputEditText
    private lateinit var redeemMoney: CardView
    private lateinit var whiteView: View
    private lateinit var loaderRedeem: LottieAnimationView
    private lateinit var walletId: TextView
    private lateinit var backBtn: ImageButton
    private lateinit var dbViewModel: DBViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var myUser: FirebaseUser
    private lateinit var walletBalance: String
    private lateinit var mainLayout: RelativeLayout
    private lateinit var amount: String
    private lateinit var originalPIN: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_redeem, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        amountEditText = view.findViewById(R.id.amount_redeem)
        redeemMoney = view.findViewById(R.id.redeem_money_btn_redeem)
        whiteView = view.findViewById(R.id.whiteView_redeem)
        loaderRedeem = view.findViewById(R.id.loader_redeem)
        backBtn = view.findViewById(R.id.back_btn_redeem)
        walletId = view.findViewById(R.id.walletId_redeem)
        mainLayout = view.findViewById(R.id.main_layout_redeem)

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        redeemMoney.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderRedeem.visibility = View.VISIBLE
            amount = amountEditText.text.toString()
            if (amount.isEmpty()) {
                whiteView.visibility = View.GONE
                loaderRedeem.visibility = View.GONE
                amountEditText.error = "Enter an amount"
                Toast.makeText(requireContext(), "Enter an amount first", Toast.LENGTH_SHORT).show()
            } else if (amount.toDouble() <= 0) {
                whiteView.visibility = View.GONE
                loaderRedeem.visibility = View.GONE
                amountEditText.error = "Enter an valid amount"
                Toast.makeText(requireContext(), "Enter an valid amount", Toast.LENGTH_SHORT).show()
            }else if (amount.toDouble() <= 99) {
                whiteView.visibility = View.GONE
                loaderRedeem.visibility = View.GONE
                amountEditText.error = "Enter an amount at least 100"
                Toast.makeText(requireContext(), "Enter an amount at least 100", Toast.LENGTH_SHORT).show()
            } else if (amount.toDouble() > 5000) {
                whiteView.visibility = View.GONE
                loaderRedeem.visibility = View.GONE
                amountEditText.error = "Entered amount is over limit"
                Toast.makeText(
                    requireContext(),
                    "You can't redeem more than 5000 rupees from your wallet in a day",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (amount.toDouble() > walletBalance.toDouble()) {
                whiteView.visibility = View.GONE
                loaderRedeem.visibility = View.GONE
                amountEditText.error = "You don't have sufficient balance to redeem"
                Toast.makeText(
                    requireContext(),
                    "You don't have sufficient balance to redeem",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showDialog()
            }
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.enter_pin_dialog)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val pinEditText: EditText = dialog.findViewById(R.id.pinEditText)
        val submit: CardView = dialog.findViewById(R.id.submit_btn_PIN_dialog)
        val payingText: TextView = dialog.findViewById(R.id.paying_text_PIN_dialog)

        payingText.text = "Redeem ₹$amount from your smart wallet"

        submit.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderRedeem.visibility = View.VISIBLE
            val pin = pinEditText.text.toString()

            if (pin.length != 4) {
                Toast.makeText(requireContext(), "Enter valid PIN", Toast.LENGTH_SHORT).show()
                whiteView.visibility = View.GONE
                loaderRedeem.visibility = View.GONE
            } else if (pin != originalPIN) {
                Toast.makeText(requireContext(), "Entered wrong PIN, try again", Toast.LENGTH_SHORT)
                    .show()
                pinEditText.text = null
                whiteView.visibility = View.GONE
                loaderRedeem.visibility = View.GONE
            }
//          else if(!BCrypt.checkpw(pin, originalPIN)) {
//                Toast.makeText(requireContext(), "Entered wrong PIN, try again", Toast.LENGTH_SHORT).show()
//                pinEditText.text = null
//                whiteView.visibility = View.GONE
//                loaderRedeem.visibility = View.GONE
//          }
            else {
                dbViewModel.sendRedeemRequest(myUser.uid, amount)
                dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                    when(it) {
                        is Response.Success -> {
                            dialog.hide()
                            Toast.makeText(requireContext(), "Redeem request send successfully", Toast.LENGTH_SHORT).show()
                            val bundle = Bundle()
                            bundle.putString("amount", amount)
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView()).navigate(R.id.nav_redeem_confirmation, bundle)
                        }
                        is Response.Failure -> {
                            dialog.hide()
                            Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                            whiteView.visibility = View.GONE
                            loaderRedeem.visibility = View.GONE
                        }
                    }
                }
            }
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                myUser = it
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.isNotEmpty()) {
                        originalPIN = list[6]
                        walletBalance = list[5]
                        walletId.text = list[2]
                        dbViewModel.checkDailyAddAmountLimit(myUser)
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderRedeem.visibility = View.GONE
                    } else {
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderRedeem.visibility = View.GONE
                    }
                }
            }
        }
    }
}
