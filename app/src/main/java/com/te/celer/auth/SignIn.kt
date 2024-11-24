package com.te.celer.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.main_files.MainActivity
import com.te.celer.db.AuthViewModel
import com.te.celer.db.Response
import com.google.android.material.textfield.TextInputLayout
import com.te.celer.R
import com.te.celer.main_files.SplashScreen

class SignIn : Fragment() {

    private lateinit var phEditText: AppCompatEditText
    private lateinit var passwordEditText: AppCompatEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var signInBtn: CardView
    private lateinit var registerText: TextView
    private lateinit var whiteView: View
    private lateinit var signInLoader: LottieAnimationView
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private lateinit var authViewModel: AuthViewModel
    private lateinit var backBtn: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        requireActivity().window.statusBarColor = Color.parseColor("#F7F9FD")

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        signInBtn = view.findViewById(R.id.signInButton_signIn)
        registerText = view.findViewById(R.id.registerText_signIn)
        phEditText = view.findViewById(R.id.ph_signIn)
        passwordEditText = view.findViewById(R.id.password_signIn)
        passwordLayout = view.findViewById(R.id.passwordLayout_signIn)
        whiteView = view.findViewById(R.id.whiteView_signIn)
        signInLoader = view.findViewById(R.id.loader_signIn)
        backBtn = view.findViewById(R.id.back_btn_signIn)

        signInBtn.setOnClickListener {
            signInBtn.isClickable = false
            signIn()
        }

        registerText.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("LastFragment", "SignIn")
            Navigation.findNavController(view).navigate(R.id.nav_register, bundle)
        }

        phEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (phEditText.text!!.isEmpty()) {
                    phEditText.hint = "Enter your 10 digits Phone Number"
                }
            } else {
                phEditText.hint = null
            }
        }

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (passwordEditText.text!!.isEmpty()) {
                    passwordEditText.hint = "Enter your password"
                }
            } else {
                passwordEditText.hint = null
            }
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signInBtn.isClickable = false
                signIn()
                true
            } else false
        }

        return view
    }

    private fun signIn() {
        whiteView.visibility = View.VISIBLE
        signInLoader.visibility = View.VISIBLE
        passwordLayout.isPasswordVisibilityToggleEnabled = true

        val phNum = phEditText.text.toString()
        val password = passwordEditText.text.toString()
        var isAlright = true

        if (!phNum.matches(validPhoneNumberPattern.toRegex())) {
            phEditText.error = "Enter valid 10 digits phone number"
            isAlright = false
        }
        if (password.length < 6) {
            passwordEditText.error = "Enter valid password"
            passwordLayout.isPasswordVisibilityToggleEnabled = false
            isAlright = false
        }
        if (!isAlright) {
            whiteView.visibility = View.GONE
            signInLoader.visibility = View.GONE
        } else {
            authViewModel.login(phNum, password)
            authViewModel.response.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        startActivity(Intent(requireContext(), SplashScreen::class.java))
                        requireActivity().finish()
                        whiteView.visibility = View.GONE
                        signInLoader.visibility = View.GONE
                    }
                    is Response.Failure -> {
                        Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                        whiteView.visibility = View.GONE
                        signInLoader.visibility = View.GONE
                        signInBtn.isClickable = true
                    }
                }
            }
        }
    }

}