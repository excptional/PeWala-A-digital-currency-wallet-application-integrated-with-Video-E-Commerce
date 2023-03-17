package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.main_files.AddMoneyUsingUPI
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser

class Add : Fragment() {

    private lateinit var amountEditText: TextInputEditText
    private lateinit var addMoney: CardView
    private lateinit var whiteView: View
    private lateinit var loaderAdd: LottieAnimationView
    private lateinit var walletId: TextView
    private lateinit var backBtn: ImageButton
    private lateinit var dbViewModel: DBViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var myUser: FirebaseUser
    private lateinit var walletBalance: String
    private lateinit var mainLayout: LinearLayout
    private var limit = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        amountEditText = view.findViewById(R.id.amount_add)
        addMoney = view.findViewById(R.id.add_money_btn_add)
        whiteView = view.findViewById(R.id.whiteView_add)
        loaderAdd = view.findViewById(R.id.loader_add)
        backBtn = view.findViewById(R.id.back_btn_add)
        walletId = view.findViewById(R.id.walletId_add)
        mainLayout = view.findViewById(R.id.main_layout_add)

        loadData(view)

        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) myUser = it
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        addMoney.setOnClickListener {

            whiteView.visibility = View.VISIBLE
            loaderAdd.visibility = View.VISIBLE
            val amount = amountEditText.text.toString()

            if (amount.isEmpty()) {
                whiteView.visibility = View.GONE
                loaderAdd.visibility = View.GONE
                amountEditText.error = "Enter an amount"
                Toast.makeText(requireContext(), "Enter an amount first", Toast.LENGTH_SHORT).show()
            } else if (amount.toDouble() <= 0) {
                whiteView.visibility = View.GONE
                loaderAdd.visibility = View.GONE
                amountEditText.error = "Enter an valid amount"
                Toast.makeText(requireContext(), "Enter an valid amount", Toast.LENGTH_SHORT).show()
            } else if (limit == 0.0) {
                whiteView.visibility = View.GONE
                loaderAdd.visibility = View.GONE
                amountEditText.error = "Your daily add limit was reached, try again tomorrow"
                Toast.makeText(
                    requireContext(),
                    "Your daily add limit was reached, try again tomorrow",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (amount.toDouble() > 5000) {
                whiteView.visibility = View.GONE
                loaderAdd.visibility = View.GONE
                amountEditText.error = "Entered amount is over limit"
                Toast.makeText(
                    requireContext(),
                    "You can't add more than 5000 rupees in your wallet in a day",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (amount.toDouble() > limit) {
                whiteView.visibility = View.GONE
                loaderAdd.visibility = View.GONE
                amountEditText.error = "You can't add not more than ${limit.toInt()} rupees"
                Toast.makeText(
                    requireContext(),
                    "You can't add not more than ${limit.toInt()} rupees according to your daily  limit",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (amount.toDouble() + walletBalance.toDouble() > 20000) {
                whiteView.visibility = View.GONE
                loaderAdd.visibility = View.GONE
                amountEditText.error = "You reach the maximum limit of your wallet"
                Toast.makeText(
                    requireContext(),
                    "You reach the maximum limit of your wallet, your wallet limit is 20,000 rupees",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(requireContext(), AddMoneyUsingUPI::class.java)
                intent.putExtra("amount", amount)
                intent.putExtra("uid", myUser)
                startActivity(intent)
                whiteView.visibility = View.GONE
                loaderAdd.visibility = View.GONE
            }
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(view: View) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                myUser = it
                dbViewModel.fetchAccountDetails(it)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.isNotEmpty()) {
                        walletBalance = list[5]
                        walletId.text = "(${list[2]})"
                        dbViewModel.checkDailyAddAmountLimit(myUser)
                        dbViewModel.dailyAddLimit.observe(viewLifecycleOwner) {
                            limit = it
                        }
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
}