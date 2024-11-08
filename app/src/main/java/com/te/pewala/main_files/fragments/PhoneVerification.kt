package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.BuildConfig
import com.te.pewala.R
import com.te.pewala.db.*
import com.te.pewala.server.MessageResponse
import com.te.pewala.server.TwoFactorApiClient
import com.te.pewala.server.TwoFactorApiInterface
import com.te.pewala.server.otp.Msg91ApiClient
import com.te.pewala.server.otp.Msg91Response
import com.te.pewala.server.otp.OTPCallback
import com.te.pewala.server.otp.OTPService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PhoneVerification : Fragment(), OTPCallback {

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
    private lateinit var phone: String
    private lateinit var otp: String
//    private lateinit var apiKey: String
//    private lateinit var sessionId: String
    private lateinit var uid: String
//    private lateinit var pin: String
//    private lateinit var temp: String
    private lateinit var otpSend: String
    private lateinit var backBtn: ImageView
    private val otpService = OTPService()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phone_varification, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        phone = requireArguments().getString("phone").toString()
        uid = requireArguments().getString("uid").toString()
//        pin = requireArguments().getString("pin").toString()
//        temp = requireArguments().getString("temp").toString()
        otpSend = requireArguments().getString("otp").toString()


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

        otpPh.text = "+91$phone"
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
                lifecycleScope.launch {
                    delay(2000) // Delay for 2 seconds
                    verifyOTP()
                }
            }
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun verifyOTP() {
        if (otpSend == otp) {
//            dbViewModel.changePIN(uid, pin)
            Toast.makeText(
                requireContext(),
                "Your phone number is verified successfully",
                Toast.LENGTH_SHORT
            ).show()
            Navigation.findNavController(requireView()).popBackStack()
            Navigation.findNavController(requireView()).popBackStack()
            Navigation.findNavController(requireView()).navigate(R.id.nav_account)
        } else {
            Toast.makeText(
                requireContext(),
                "The OTP you entered is incorrect. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
            timerText.isClickable = true
            timerText.text = "Resend OTP"
            whiteView.visibility = View.GONE
            otpLoader.visibility = View.GONE
        }

    }


    private fun resendTimer() {
        timerText.isClickable = false
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 10000)
                    timerText.text = "Resend in : 00:0" + millisUntilFinished / 1000
                else timerText.text = "Resend in : 00:" + millisUntilFinished / 1000
            }

            @SuppressLint("SetTextI18n", "ResourceAsColor")
            override fun onFinish() {
                timerText.isClickable = true
                timerText.setTextColor(R.color.t_2)
                timerText.text = "Resend OTP"
            }
        }.start()
    }

    @SuppressLint("ResourceAsColor")
    private fun resendOTP() {
        timerText.setTextColor(R.color.material_black)
        otpSend = otpService.generateOtp()
        otpService.sendOtpMessage(phone, otpSend, this)
        box1.text = null
        box2.text = null
        box3.text = null
        box4.text = null

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

    override fun onOtpSentSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        resendTimer()
        whiteView.visibility = View.GONE
    }

    override fun onOtpSentFailure(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        whiteView.visibility = View.GONE
    }

}