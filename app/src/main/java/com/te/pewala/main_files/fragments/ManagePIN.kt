package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.R
import com.te.pewala.db.AESCrypt
import com.te.pewala.db.ApiClient
import com.te.pewala.db.ApiInterface
import com.te.pewala.db.MessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagePIN : Fragment() {

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
    private lateinit var myUser: FirebaseUser
    private lateinit var submitBtn: CardView
    private lateinit var pin: String
    private var prevPIN  = ""
    private lateinit var toastText: String
    private lateinit var temp: String
    private lateinit var backBtn: ImageView
//    private val apiKey = "09e68d45-c8eb-11ed-81b6-0200cd936042"
    private val apiKey = "2898c4b2-22b2-11ef-8b60-0200cd936042"
    private val aesCrypt = AESCrypt()
    val key = ByteArray(32)

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

        loadData()

        submitBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderPIN.visibility = View.VISIBLE
            submit()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun submit() {

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
            sendOTP()
        }
    }


    private fun sendOTP() {
        val apiService: ApiInterface =
            ApiClient().getClient()!!.create(ApiInterface::class.java)

        val call: Call<MessageResponse?>? =
            apiService.sentOTP(apiKey, phone)
        call!!.enqueue(object : Callback<MessageResponse?> {

            override fun onFailure(call: Call<MessageResponse?>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<MessageResponse?>,
                response: Response<MessageResponse?>
            ) {
                sessionId = response.body()!!.getDetails()!!
                val bundle = Bundle()
                bundle.putString("sessionId", sessionId)
                bundle.putString("api_key", apiKey)
                bundle.putString("phone", phone)
                bundle.putString("uid", myUser.uid)
                bundle.putString("pin", pin)
                bundle.putString("temp", temp)
                Navigation.findNavController(requireView()).popBackStack()
                Navigation.findNavController(requireView()).navigate(R.id.nav_phone_verification, bundle)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                myUser = it
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.exists()) {
                        phone = list.getString("phone").toString()
                        if (list.getString("pin").isNullOrEmpty()) {
                            currentPINLayout.visibility = View.GONE
                            temp = "New PIN generated successfully"
                        } else {
                            currentPINLayout.visibility = View.VISIBLE
                            prevPIN = list.getString("pin")!!
//                            prevPIN = aesCrypt.decrypt(list.getString("pin").toString(), key).toString()
                            temp = "PIN changed successfully"
                        }
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderPIN.visibility = View.GONE
                    } else {
                        whiteView.visibility = View.GONE
                        loaderPIN.visibility = View.GONE
                        mainLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

}