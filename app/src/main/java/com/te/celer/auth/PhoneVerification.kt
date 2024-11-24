package com.te.celer.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.R
import com.te.celer.db.*
import com.te.celer.main_files.MainActivity
import com.te.celer.server.otp.OTPCallback
import com.te.celer.server.otp.OTPService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.te.celer.db.Response.Success
import com.te.celer.db.Response.Failure
import com.te.celer.main_files.SplashScreen


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
//    private lateinit var uid: String
//    private lateinit var pin: String
//    private lateinit var temp: String
    private lateinit var otpSend: String
    private lateinit var backBtn: ImageView
    private val otpService = OTPService()
    private lateinit var name: String
    private lateinit var password: String
    private lateinit var aadhar: String
    private lateinit var userType: String

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phone_verification, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        phone = requireArguments().getString("phone").toString()
//        uid = requireArguments().getString("uid").toString()
//        pin = requireArguments().getString("pin").toString()
//        temp = requireArguments().getString("temp").toString()
        otpSend = requireArguments().getString("otp").toString()
        name = requireArguments().getString("name").toString()
        password = requireArguments().getString("password").toString()
        aadhar = requireArguments().getString("aadhar").toString()
        userType = requireArguments().getString("userType").toString()


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
            submitBtn.isClickable = false

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
        if (otp == otpSend) {
//            dbViewModel.changePIN(uid, pin)
//            Toast.makeText(
//                requireContext(),
//                "Your phone number is verified successfully",
//                Toast.LENGTH_SHORT
//            ).show()
//            Navigation.findNavController(requireView()).popBackStack()
//            Navigation.findNavController(requireView()).popBackStack()
//            Navigation.findNavController(requireView()).navigate(R.id.nav_account)
            authViewModel.signUp(name, phone, aadhar, password, userType)
            authViewModel.response.observe(viewLifecycleOwner) {
                when(it) {
                    is Success -> {
                        Toast.makeText(requireContext(), "Welcome to Celer", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), SplashScreen::class.java))
                        requireActivity().finish()
                        whiteView.visibility = View.GONE
                        otpLoader.visibility = View.GONE
                    }
                    is Failure -> {
                        Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                        whiteView.visibility = View.GONE
                        otpLoader.visibility = View.GONE
                        submitBtn.isClickable = true
                    }

                    else -> {}
                }
            }
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
            submitBtn.isClickable = true
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
                timerText.setTextColor(Color.BLACK)
                timerText.paintFlags = timerText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                timerText.text = "Resend OTP"
            }
        }.start()
    }

    @SuppressLint("ResourceAsColor")
    private fun resendOTP() {
        timerText.setTextColor(R.color.material_black)
        timerText.paintFlags = timerText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
        otpSend = otpService.generateOtp()
        otpService.sendOtpMessage(phone, otpSend, this)
        box1.text = null
        box2.text = null
        box3.text = null
        box4.text = null

    }


    private fun numberOtpMove() {
        box1.addTextChangedListener(createOtpTextWatcher(box2, null))
        box2.addTextChangedListener(createOtpTextWatcher(box3, box1))
        box3.addTextChangedListener(createOtpTextWatcher(box4, box2))
        box4.addTextChangedListener(createOtpTextWatcher(null, box3))

        addDeleteKeyListener(box2, box1)
        addDeleteKeyListener(box3, box2)
        addDeleteKeyListener(box4, box3)
    }


    private fun createOtpTextWatcher(
        nextBox: EditText?,
        previousBox: EditText?
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty() && nextBox != null) {
                    nextBox.requestFocus()
                } else if (before > 0 && previousBox != null) {
                    previousBox.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }


    private fun addDeleteKeyListener(currentBox: EditText, previousBox: EditText?) {
        currentBox.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && currentBox.text.isNullOrEmpty()) {
                previousBox?.requestFocus()
                true
            } else {
                false
            }
        }
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