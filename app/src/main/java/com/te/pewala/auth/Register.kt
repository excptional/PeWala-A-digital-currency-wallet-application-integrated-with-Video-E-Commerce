package com.te.pewala.auth

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.Response
import com.google.android.material.textfield.TextInputLayout
import com.te.pewala.R

class Register : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var phEditText: AppCompatEditText
    private lateinit var passwordEditText: AppCompatEditText
    private lateinit var nameEditText: AppCompatEditText
    private lateinit var aadharEditText: AppCompatEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var signInText: TextView
    private lateinit var registerBtn: CardView
    private lateinit var whiteView: View
    private lateinit var signUpLoader: LottieAnimationView
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private lateinit var authViewModel: AuthViewModel
    private var userType: String = ""
    private lateinit var spinner: Spinner
    private lateinit var backBtn: ImageView

    private var userList = arrayListOf(
        "Select User Type",
        "Buyer",
        "Seller"
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        requireActivity().window.statusBarColor = Color.parseColor("#F7F9FD")

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        phEditText = view.findViewById(R.id.ph_signUp)
        passwordEditText = view.findViewById(R.id.password_signUp)
        nameEditText = view.findViewById(R.id.name_signUp)
        aadharEditText = view.findViewById(R.id.aadhar_signUp)
        passwordLayout = view.findViewById(R.id.passwordLayout_signUp)
        registerBtn = view.findViewById(R.id.registerButton_signUp)
        signInText = view.findViewById(R.id.signInText_signUp)
        whiteView = view.findViewById(R.id.whiteView_signUp)
        signUpLoader = view.findViewById(R.id.loader_signUp)
        spinner = view.findViewById(R.id.userType_spinner_signUp)
        backBtn = view.findViewById(R.id.back_btn_signUp)

        nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (nameEditText.text!!.isEmpty()) {
                    nameEditText.hint = "Enter your name"
                }
            } else {
                nameEditText.hint = null
            }
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

        aadharEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (aadharEditText.text!!.isEmpty()) {
                    aadharEditText.hint = "Enter your Aadhar Number"
                }
            } else {
                aadharEditText.hint = null
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


        signInText.setOnClickListener {
            if(requireArguments().getString("LastFragment").equals("SignIn")) {
                Navigation.findNavController(view).popBackStack()
            }
            Navigation.findNavController(view).popBackStack()
            Navigation.findNavController(it).navigate(R.id.nav_sign_in)
        }

        registerBtn.setOnClickListener {
            registerBtn.isClickable = false
            signUp(view)
        }

        val userTypeAdapter = CustomSpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            userList
        )
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(spinner)
        {
            adapter = userTypeAdapter
            setSelection(0, true)
            onItemSelectedListener = this@Register
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.white)
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerBtn.isClickable = false
                signUp(view)
                true
            } else false
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

        if(userType.isNullOrEmpty()) {
            isAlright = false
            Toast.makeText(requireContext(), "Select usertype first", Toast.LENGTH_SHORT).show()
        }

        if(!isAlright) {
            whiteView.visibility = View.GONE
            signUpLoader.visibility = View.GONE
            Toast.makeText(requireContext(), "Enter valid details", Toast.LENGTH_SHORT).show()
        } else {
            authViewModel.signUp(name, phNum, aadhar, password, userType)
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
                        registerBtn.isClickable = true
                    }
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
            Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show()
            userType = ""
        } else {
            userType = userList[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}