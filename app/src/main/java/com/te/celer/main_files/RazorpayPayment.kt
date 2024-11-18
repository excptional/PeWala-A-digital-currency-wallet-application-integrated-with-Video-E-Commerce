package com.te.celer.main_files

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.te.celer.R
import com.te.celer.db.DBViewModel
import com.te.celer.db.LocalStorage
import com.te.celer.main_files.models.CartItems
import com.te.celer.server.razorpay.RazorpayOrderApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RazorpayPayment : AppCompatActivity(), PaymentResultListener {

    private var productNames = ""
    private lateinit var textView: TextView
    private lateinit var paymentLoader: LottieAnimationView
    private var finalAmount: Long = 0
    private var handler = Handler()
    private var orderItems = arrayListOf<CartItems>()
    private val orderRequest = arrayListOf<Long>()
    private val dbViewModel: DBViewModel by viewModels()
    private val localStorage = LocalStorage()
    private lateinit var address: String
    private var orderIds = ArrayList<String>()
    private lateinit var razorpayOrderId: String
    private val notification = Notification()
    private val checkout = Checkout()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_razorpay_payment)

        textView = findViewById(R.id.text_payment_razorpay)
        paymentLoader = findViewById(R.id.loader_payment_razorpay)

        checkout.setKeyID(getString(R.string.RAZORPAY_KEY_ID))

        val bundle = intent.extras

        orderItems = bundle!!.getSerializable("orderItems") as ArrayList<CartItems>
        finalAmount = bundle.getLong("finalAmount") * 100
//        finalAmount = 100
        address = bundle.getString("address")!!

        for (item in orderItems) {
            productNames += item.productName + ", "
            orderRequest.add(item.price!!.toLong() * 100)
        }

        orderRequest.add(finalAmount)

        productNames = productNames.substring(0, productNames.length - 2)

        startTextLoop()

        createOrder(orderRequest)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        try {
            val userdata = localStorage.getData(this, "user_data")

            dbViewModel.addTransaction(
                (finalAmount / 100).toString(),
                "Payment for order id of $razorpayOrderId",
                razorpayPaymentID!!,
                userdata!!["uid"]!!,
                "ADMIN",
                userdata["name"]!!,
                userdata["phone"]!!,
                userdata["image_url"]!!,
                "ADMIN",
                "ADMIN",
                "ADMIN",
                System.currentTimeMillis().toString()
            )

            for(idx in orderIds.indices) {
                dbViewModel.addOrder(
                    userdata["name"]!!,
                    userdata["phone"]!!,
                    address,
                    "Digital Pay",
                    userdata["uid"]!!,
                    orderItems[idx].brandName!!,
                    orderItems[idx].productName!!,
                    orderItems[idx].productImageUrl!!,
                    orderItems[idx].productId!!,
                    orderItems[idx].category!!,
                    orderItems[idx].price!!,
                    orderItems[idx].quantity!!,
                    orderItems[idx].sellerUID!!,
                    orderIds[idx],
                    System.currentTimeMillis().toString()
                )
            }

            notification.sendNotification(this, "Your order successfully placed", "for $productNames", "order")
            Toast.makeText(
                this,
                "Payment Successful: $razorpayPaymentID",
                Toast.LENGTH_SHORT
            ).show()

            lifecycleScope.launch {
                val intent = Intent(this@RazorpayPayment, MainActivity::class.java).apply {
                    putExtra("orderPlaced", "order")
                }
                startActivity(intent)
                finish()
                delay(3000)
            }
        } catch (e: Exception) {
            Log.e("ops1", "onPaymentSuccess: ${e.message}")
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
        Log.e("ope", "onPaymentSuccess: $response")
        onBackPressed()
    }

    private fun createOrder(orderRequest: ArrayList<Long>) {

        RazorpayOrderApiClient().paymentApi.createOrder(orderRequest)
            .enqueue(object : ArrayList<String>(), Callback<ArrayList<String>> {

                @RequiresApi(35)
                override fun onResponse(
                    call: Call<ArrayList<String>>,
                    response: Response<ArrayList<String>>
                ) {
                    if (response.isSuccessful) {
                        orderIds = response.body()!!
                        val status = orderIds.removeLast()
                        razorpayOrderId = orderIds.removeLast()
                        Log.d("orderIds", "onResponse: ${orderIds}")
                        Log.d("finalOrderIds", "onResponse: ${razorpayOrderId}")
                        if (status == "Success") {
//                            Toast.makeText(this@RazorpayPayment, "$productNames", Toast.LENGTH_LONG)
//                                .show()

                            startPayment(razorpayOrderId, finalAmount)
                        } else {
                            Toast.makeText(
                                this@RazorpayPayment,
                                "Failed to create order",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBackPressed()
                            Log.e("tag1", "onResponse: ${response.message()}")
                        }
                    } else {
                        Toast.makeText(
                            this@RazorpayPayment,
                            "Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("tag2", "onResponse: ${response.message()}")
                        onBackPressed()
                    }
                }

                override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                    Toast.makeText(
                        this@RazorpayPayment,
                        "Request failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("tag3", "onResponse: ${t.message}")
                    onBackPressed()
                }
            })
    }

    private fun startTextLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (textView.text.toString() == "Processing Payment") {
                    textView.text = "Please wait while we securely \n process your payment"
                } else {
                    textView.text = "Processing Payment"
                }
                handler.postDelayed(this, 2000)
            }
        }, 2000)
    }


    private fun startPayment(orderId: String, amount: Long) {

        try {
            val options = JSONObject()
            options.put("name", "Celer")
            options.put("description", "Order of $productNames")
            options.put("order_id", orderId)
            options.put("currency", "INR")
            options.put("amount", amount)
            options.put(
                "image",
                "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/pewala_app_icon.png?alt=media&token=817f21f9-e36c-4e27-b119-aa1cb0217140"
            )
            options.put("theme.color", "#6B3E2E")

            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Error in starting payment: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
}