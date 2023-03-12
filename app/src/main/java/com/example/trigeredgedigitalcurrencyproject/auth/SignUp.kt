package com.example.trigeredgedigitalcurrencyproject.auth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.Response
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUp : Fragment() {

    private lateinit var phEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var aadharEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var signInBtn: CardView
    private lateinit var signUpBtn: CardView
    private lateinit var whiteView: View
    private lateinit var signUpLoader: LottieAnimationView
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private lateinit var authViewModel: AuthViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        phEditText = view.findViewById(R.id.ph_signUp)
        passwordEditText = view.findViewById(R.id.password_signUp)
        nameEditText = view.findViewById(R.id.name_signUp)
        aadharEditText = view.findViewById(R.id.aadhar_signUp)
        passwordLayout = view.findViewById(R.id.passwordLayout_signUp)
        signUpBtn = view.findViewById(R.id.signUpButton_signUp)
        signInBtn = view.findViewById(R.id.signInButton_signUp)
        whiteView = view.findViewById(R.id.whiteView_signUp)
        signUpLoader = view.findViewById(R.id.loader_signUp)

        signInBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_sign_in)
        }

        signUpBtn.setOnClickListener {
            signUp(view)
        }
        return view
    }

    private fun signUp(view: View) {

        whiteView.visibility = View.VISIBLE
        signUpLoader.visibility = View.VISIBLE
        passwordLayout.isPasswordVisibilityToggleEnabled = true

        val phNum = phEditText.text.toString()
        val name = nameEditText.text.toString()
        val aadhar = aadharEditText.text.toString()
        val password = passwordEditText.text.toString()
        var isAlright = true

        if(name.isEmpty()) {
            nameEditText.error = "Enter name first"
            isAlright = false
        }
        if(!phNum.matches(validPhoneNumberPattern.toRegex())) {
            phEditText.error = "Enter valid 10 digits phone number"
            isAlright = false
        }
        if(aadhar.length != 12) {
            aadharEditText.error = "Enter name first"
            isAlright = false
        }
        if(password.length < 6) {
            passwordEditText.error = "Enter valid password"
            passwordLayout.isPasswordVisibilityToggleEnabled = false
            isAlright = false
        }

        if(!isAlright) {
            whiteView.visibility = View.GONE
            signUpLoader.visibility = View.GONE
            Toast.makeText(requireContext(), "Enter valid details", Toast.LENGTH_SHORT).show()
        } else {
            authViewModel.signUp(name, phNum, aadhar, password)
            authViewModel.response.observe(viewLifecycleOwner) {
                when(it) {
                    is Response.Success -> {
                        Navigation.findNavController(view).navigate(R.id.nav_sign_in)
                        whiteView.visibility = View.GONE
                        signUpLoader.visibility = View.GONE
                    }
                    is Response.Failure -> {
                        Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                        whiteView.visibility = View.GONE
                        signUpLoader.visibility = View.GONE
                    }
                }
            }
        }
    }

}