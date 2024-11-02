package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import com.te.pewala.BuildConfig
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.R
import com.te.pewala.db.AESCrypt
import com.te.pewala.db.LocalStorage
import com.te.pewala.server.TwoFactorApiClient
import com.te.pewala.server.TwoFactorApiInterface
import com.te.pewala.server.MessageResponse
import com.te.pewala.server.otp.Msg91ApiClient
import com.te.pewala.server.otp.Msg91Response
import com.te.pewala.server.otp.OTPCallback
import com.te.pewala.server.otp.OTPService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagePIN : Fragment(), OTPCallback {

    private val localStorage = LocalStorage()
    private lateinit var sessionId: String
    private lateinit var phone: String
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var currentPINEditText: AppCompatEditText
    private lateinit var newPINEditText: AppCompatEditText
    private lateinit var confirmPINEditText: AppCompatEditText
    private lateinit var currentPINLayout: TextInputLayout
    private lateinit var mainLayout: RelativeLayout
    private lateinit var whiteView: View
    private lateinit var loaderPIN: LottieAnimationView
    private lateinit var uid: String
    private lateinit var submitBtn: CardView
    private lateinit var pin: String
    private var prevPIN = ""
    private lateinit var toastText: String
    private lateinit var temp: String
    private lateinit var backBtn: ImageView
    private val TWO_FACTOR_API_KEY = "09e68d45-c8eb-11ed-81b6-0200cd936042"
    private val aesCrypt = AESCrypt()
    val key = ByteArray(32)
    val otpService = OTPService()
    private lateinit var otp: String

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_p_i_n, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        currentPINEditText = view.findViewById(R.id.current_PIN)
        newPINEditText = view.findViewById(R.id.new_PIN)
        confirmPINEditText = view.findViewById(R.id.confirm_new_PIN)
        whiteView = view.findViewById(R.id.whiteView_PIN)
        loaderPIN = view.findViewById(R.id.loader_PIN)
        mainLayout = view.findViewById(R.id.main_layout_PIN)
        submitBtn = view.findViewById(R.id.submit_btn_PIN)
        currentPINLayout = view.findViewById(R.id.current_PIN_layout)
        backBtn = view.findViewById(R.id.back_btn_PIN)

        key.fill(1)

        submitBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderPIN.visibility = View.VISIBLE
            submit()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        loadData()

        return view
    }

    private fun submit() {
        whiteView.visibility = View.VISIBLE
        loaderPIN.visibility = View.VISIBLE
        currentPINLayout.isPasswordVisibilityToggleEnabled = true
        val currentPIN = currentPINEditText.text.toString()
        val newPIN = newPINEditText.text.toString()
        val confirmPIN = confirmPINEditText.text.toString()

        var allRight = true

        if (currentPINLayout.isVisible and (currentPIN.length != 4)) {
            currentPINLayout.isPasswordVisibilityToggleEnabled = false
            currentPINEditText.error = "Enter valid 4 digit PIN"
            toastText = "Enter valid 4 digit PIN"
            allRight = false
        }
        if (currentPINLayout.isVisible and (currentPIN != prevPIN)) {
            currentPINLayout.isPasswordVisibilityToggleEnabled = false
            currentPINEditText.error = "you entered wrong PIN"
            toastText = "you entered a wrong PIN"
            allRight = false
        }
        if (newPIN.length != 4) {
            newPINEditText.error = "Enter valid 4 digit PIN"
            toastText = "Enter valid 4 digit PIN"
            allRight = false
        }
        if (newPIN == prevPIN) {
            toastText = "You can't set previous PIN as new PIN, try again"
            allRight = false
        }
        if (confirmPIN != newPIN) {
            confirmPINEditText.error = "PIN not matched, re-enter PIN"
            toastText = "PIN not matched, re-enter PIN"
            allRight = false
        }

        if (!allRight) {
            whiteView.visibility = View.GONE
            loaderPIN.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                toastText,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            pin = confirmPIN
            otp = otpService.generateOtp()
            otpService.sendOtpMessage(phone, otp, this)
        }
    }

//    private fun sendOtpMessage(phoneNumber: String, otp: String) {
//        val message = "Hello User, Your OTP is $otp, Regards OnlineTestPanel"
//
//        val call = Msg91ApiClient.apiService.sendOtp(
//            authKey = BuildConfig.MSG91_AUTH_KEY,
//            senderId = BuildConfig.MSG91_SENDER_ID,
//            templateId = BuildConfig.MSG91_TEMPLATE_ID,
//            route = BuildConfig.MSG91_ROUTE,
//            mobileNumber = phoneNumber,
//            message = message
//        )
//
//        call.enqueue(object : Callback<Msg91Response> {
//            override fun onResponse(call: Call<Msg91Response>, response: Response<Msg91Response>) {
//                if (response.isSuccessful && response.body() != null) {
//                    println("OTP sent successfully: ${response.body()?.message}")
//                    val bundle = Bundle()
////                bundle.putString("sessionId", sessionId)
////                bundle.putString("api_key", TWO_FACTOR_API_KEY)
//                    bundle.putString("phone", phone)
//                    bundle.putString("uid", uid)
//                    bundle.putString("pin", pin)
//                    bundle.putString("temp", temp)
//                    bundle.putString("otp", otp)
//                    Navigation.findNavController(requireView()).popBackStack()
//                    Navigation.findNavController(requireView())
//                        .navigate(R.id.nav_phone_verification, bundle)
//                } else {
//                    println("Failed to send OTP. Response: ${response.errorBody()?.string()}")
//                    whiteView.visibility = View.GONE
//                    loaderPIN.visibility = View.GONE
//                }
//            }
//
//            override fun onFailure(call: Call<Msg91Response>, t: Throwable) {
//                println("Error sending OTP: ${t.message}")
//                whiteView.visibility = View.GONE
//                loaderPIN.visibility = View.GONE
//            }
//        })
//    }


//    private fun sendOTP() {
//        val apiService: TwoFactorApiInterface =
//            TwoFactorApiClient().getClient()!!.create(TwoFactorApiInterface::class.java)
//
//        val call: Call<MessageResponse?>? =
//            apiService.sentOTP(TWO_FACTOR_API_KEY, phone)
//        call!!.enqueue(object : Callback<MessageResponse?> {
//
//            override fun onFailure(call: Call<MessageResponse?>, t: Throwable) {
//                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
//            }
//
//            override fun onResponse(
//                call: Call<MessageResponse?>,
//                response: Response<MessageResponse?>
//            ) {
//                sessionId = response.body()!!.getDetails()!!
//                val bundle = Bundle()
//                bundle.putString("sessionId", sessionId)
//                bundle.putString("api_key", TWO_FACTOR_API_KEY)
//                bundle.putString("phone", phone)
//                bundle.putString("uid", uid)
//                bundle.putString("pin", pin)
//                bundle.putString("temp", temp)
//                Navigation.findNavController(requireView()).popBackStack()
//                Navigation.findNavController(requireView()).navigate(R.id.nav_phone_verification, bundle)
//            }
//        })
//    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!
        phone = userdata["phone"]!!
        if (userdata["pin"].isNullOrEmpty()) {
            currentPINLayout.visibility = View.GONE
            temp = "New PIN generated successfully"
        } else {
            currentPINLayout.visibility = View.VISIBLE
            prevPIN = aesCrypt.decrypt(userdata["pin"]!!, key)!!
            temp = "PIN changed successfully"
        }
        mainLayout.visibility = View.VISIBLE
        whiteView.visibility = View.GONE
        loaderPIN.visibility = View.GONE
    }

    override fun onOtpSentSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        val bundle = Bundle()
        bundle.putString("phone", phone)
        bundle.putString("uid", uid)
        bundle.putString("pin", pin)
        bundle.putString("temp", temp)
        bundle.putString("otp", otp)
        Navigation.findNavController(requireView()).popBackStack()
        Navigation.findNavController(requireView())
            .navigate(R.id.nav_phone_verification, bundle)
    }

    override fun onOtpSentFailure(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        whiteView.visibility = View.GONE
        loaderPIN.visibility = View.GONE
    }

}