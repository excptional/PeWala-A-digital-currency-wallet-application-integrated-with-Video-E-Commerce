package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.te.pewala.R
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.models.CartItems
import com.te.pewala.server.razorpay.RazorpayOrderApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RazorpayPayment : Fragment(), PaymentResultListener {

    private var productNames = ""
    private lateinit var orderId: String
    private lateinit var textView: TextView
    private lateinit var paymentLoader: LottieAnimationView
    private var finalAmount: Long = 0
    private var handler = Handler()
        private var orderItems = arrayListOf<CartItems>()
    private val orderRequest = arrayListOf<Long>()
    private val dbViewModel: DBViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_razorpay_payment, container, false)

        textView = view.findViewById(R.id.text_payment_razorpay)
        paymentLoader = view.findViewById(R.id.loader_payment_razorpay)


        orderItems = requireArguments().getSerializable("orderItems") as ArrayList<CartItems>
        finalAmount = requireArguments().getLong("finalAmount") * 100

        for (item in orderItems) {
            productNames += item.productName + ", "
            orderRequest.add(item.price!!.toLong() * 100)
        }

        orderRequest.add(finalAmount)

        productNames = productNames.substring(0, productNames.length - 2)

        startTextLoop()

        createOrder(orderRequest)

        return view
    }

    private fun createOrder(orderRequest: ArrayList<Long>) {

        RazorpayOrderApiClient().paymentApi.createOrder(orderRequest)
            .enqueue(object : ArrayList<String>(), Callback<ArrayList<String>> {

                override fun onResponse(
                    call: Call<ArrayList<String>>,
                    response: Response<ArrayList<String>>
                ) {
                    if (response.isSuccessful) {
                        val responseLen = response.body()!!.size
                        val razorpayOrderId = response.body()!![responseLen - 2]
                        if (responseLen != 1) {
                            orderId = razorpayOrderId
                            Toast.makeText(requireContext(), "$productNames", Toast.LENGTH_LONG).show()

                            startPayment(orderId, finalAmount)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to create order",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().finish()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    }
                }

                override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Request failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().finish()
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
        val checkout = Checkout()
        checkout.setKeyID(getString(R.string.RAZORPAY_KEY_ID))

        try {
            val options = JSONObject()
            options.put("name", "PeWala")
            options.put("description", "Order of $productNames")
            options.put("order_id", orderId)
            options.put("currency", "INR")
            options.put("amount", amount)
            options.put(
                "image",
                "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/pewala_app_icon.png?alt=media&token=817f21f9-e36c-4e27-b119-aa1cb0217140"
            )
            options.put("theme.color", "#6B3E2E")

            checkout.open(requireActivity(), options)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error in starting payment: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        for(orderItem in orderItems) {

        }
        Toast.makeText(requireContext(), "Payment Successful: $razorpayPaymentID", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(requireContext(), "Payment Failed: $response", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

}