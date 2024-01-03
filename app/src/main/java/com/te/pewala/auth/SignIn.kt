package com.te.pewala.auth

import android.annotation.SuppressLint
import android.content.Intent
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
import com.te.pewala.main_files.MainActivity
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.Response
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.te.pewala.R

class SignIn : Fragment() {

    private lateinit var phEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var signInBtn: CardView
    private lateinit var signUpBtn: CardView
    private lateinit var whiteView: View
    private lateinit var signInLoader: LottieAnimationView
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private lateinit var authViewModel: AuthViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        signInBtn = view.findViewById(R.id.signInButton_signIn)
        signUpBtn = view.findViewById(R.id.signUpButton_signIn)
        phEditText = view.findViewById(R.id.ph_signIn)
        passwordEditText = view.findViewById(R.id.password_signIn)
        passwordLayout = view.findViewById(R.id.passwordLayout_signIn)
        whiteView = view.findViewById(R.id.whiteView_signIn)
        signInLoader = view.findViewById(R.id.loader_signIn)

        signInBtn.setOnClickListener {
            signInBtn.isClickable = false
            signIn(view)
        }

        signUpBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_user_type)
        }

        return view
    }

    private fun signIn(view: View) {
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
                        startActivity(Intent(requireContext(), MainActivity::class.java))
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