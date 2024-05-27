package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.db.*
import com.te.pewala.db.MessageResponse
import retrofit2.Call
import retrofit2.Callback


class PhoneVerification : Fragment() {

    private lateinit var box1: EditText
    private lateinit var box2: EditText
    private lateinit var box3: EditText
    private lateinit var box4: EditText
    private lateinit var submitBtn: CardView
    private lateinit var timerText: TextView
    private lateinit var whiteView: View
    private lateinit var otpLoader: LottieAnimationView
    private lateinit var otpPh: TextView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private var count = 60
    private lateinit var phNum: String
    private lateinit var otp: String
    private lateinit var apiKey: String
    private lateinit var sessionId: String
    private lateinit var uid: String
    private lateinit var pin: String
    private lateinit var temp: String
    private lateinit var backBtn: ImageButton

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phone_varification, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        phNum = requireArguments().getString("phone").toString()
        sessionId = requireArguments().getString("sessionId").toString()
        apiKey = requireArguments().getString("api_key").toString()
        pin = requireArguments().getString("pin").toString()
        uid = requireArguments().getString("uid").toString()
        temp = requireArguments().getString("temp").toString()


        box1 = view.findViewById(R.id.box1)
        box2 = view.findViewById(R.id.box2)
        box3 = view.findViewById(R.id.box3)
        box4 = view.findViewById(R.id.box4)
        submitBtn = view.findViewById(R.id.submitButton)
        whiteView = view.findViewById(R.id.whiteView_verification)
        otpLoader = view.findViewById(R.id.loader_verification)
        otpPh = view.findViewById(R.id.otpPhNo)
        timerText = view.findViewById(R.id.timerText)
        backBtn = view.findViewById(R.id.back_btn_otp)

        otpPh.text = "+91$phNum"
        numberOtpMove()

        resendTimer()

        timerText.setOnClickListener {
            resendOTP()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        submitBtn.setOnClickListener {

            whiteView.visibility = View.VISIBLE
            otpLoader.visibility = View.VISIBLE

            otp =
                box1.text.toString() + box2.text.toString() + box3.text.toString() + box4.text.toString()

            if (otp.length != 4) {
                Toast.makeText(requireContext(), "Enter valid otp", Toast.LENGTH_SHORT).show()
                whiteView.visibility = View.GONE
                otpLoader.visibility = View.GONE
            } else {
                verifyOTP()
            }
        }

        return view
    }

    private fun verifyOTP() {
        val apiService: ApiInterface =
            ApiClient().getClient()!!.create(ApiInterface::class.java)

        val call: Call<MessageResponse?>? = apiService.verifyOTP(apiKey, sessionId, otp)

        call!!.enqueue(object : Callback<MessageResponse?> {

            override fun onFailure(call: Call<MessageResponse?>?, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                whiteView.visibility = View.GONE
                otpLoader.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<MessageResponse?>?,
                response: retrofit2.Response<MessageResponse?>?
            ) {
                try {
                    if (response!!.body()!!.getStatus().equals("Success")) {
                        dbViewModel.changePIN(uid, pin)
                        Handler().postDelayed({
                            Toast.makeText(
                                requireContext(),
                                temp,
                                Toast.LENGTH_SHORT
                            ).show()
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView()).popBackStack()
                            Navigation.findNavController(requireView()).navigate(R.id.nav_account)
                        }, 2000)
                    } else {
                        Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT)
                            .show()
                        timerText.isClickable = true
                        timerText.text = "Resend OTP"
                        whiteView.visibility = View.GONE
                        otpLoader.visibility = View.GONE

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun resendTimer() {
        timerText.isClickable = false
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 10000)
                    timerText.text = "Remaining : 00:0" + millisUntilFinished / 1000
                else timerText.text = "Remaining : 00:" + millisUntilFinished / 1000
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timerText.isClickable = true
                timerText.text = "Resend OTP"
            }
        }.start()
    }

    private fun resendOTP() {
        val apiService: ApiInterface =
            ApiClient().getClient()!!.create(ApiInterface::class.java)

        val call: Call<MessageResponse?>? =
            apiService.sentOTP(apiKey, phNum)
        call!!.enqueue(object : Callback<MessageResponse?> {

            override fun onFailure(call: Call<MessageResponse?>?, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<MessageResponse?>?,
                response: retrofit2.Response<MessageResponse?>?
            ) {
                sessionId = response!!.body()!!.getDetails()!!
                resendTimer()
            }
        })
    }

    private fun numberOtpMove() {
        box1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    box2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        box2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    box3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        box3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    box4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

}