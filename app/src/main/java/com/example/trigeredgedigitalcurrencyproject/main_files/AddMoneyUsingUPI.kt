package com.example.trigeredgedigitalcurrencyproject.main_files

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.google.firebase.auth.FirebaseUser
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails

class AddMoneyUsingUPI : AppCompatActivity(), PaymentStatusListener {

    private lateinit var amount: String
    private lateinit var uid: String
    private lateinit var tId: String
    private lateinit var dbViewModel: DBViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var myUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money_using_upi)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        getUser()

        amount = intent.getStringExtra("amount").toString()
        uid = intent.getStringExtra("uid").toString()

        paymentGatewayStart(amount)

    }

    private fun paymentGatewayStart(i: String) {
        tId = "TID${System.currentTimeMillis()}"
        val easyUpiPayment= EasyUpiPayment.Builder()
            .with(this)
            .setAmount("$i.00")
            .setPayeeName("Tathagata Banerjee")
            .setPayeeVpa("tathagatabn-4@okicici")
            .setTransactionId(tId)
            .setTransactionRefId(tId)
            .setDescription("Add money to your smart wallet")
            .build()

        easyUpiPayment.startPayment()
        easyUpiPayment.setPaymentStatusListener(this)
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails?) {
        val t_d= transactionDetails?.status +"\n"+ transactionDetails?.transactionId  }

    override fun onTransactionSuccess() {
        Toast.makeText(this,"Transaction Success", Toast.LENGTH_SHORT).show()
        dbViewModel.addAddMoneyRecords(amount, tId, myUser)
    }

    override fun onTransactionSubmitted() {
        TODO("Not yet implemented")
    }

    override fun onTransactionFailed() {
        Toast.makeText(this,"Something went wrong, try again", Toast.LENGTH_SHORT).show()
    }

    override fun onTransactionCancelled() {
        Toast.makeText(this,"Transaction Cancel", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onAppNotFound() {
        Toast.makeText(this,"No app found", Toast.LENGTH_SHORT).show()
    }

    fun getUser() {
        authViewModel.userdata.observe(this) {
            if (it != null) {
                myUser = it
            }
        }
    }
}