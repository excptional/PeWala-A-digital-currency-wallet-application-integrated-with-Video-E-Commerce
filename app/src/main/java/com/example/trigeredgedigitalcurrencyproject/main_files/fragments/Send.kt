package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.google.android.material.textfield.TextInputEditText

class Send : Fragment() {

    private lateinit var phoneEditText: TextInputEditText
    private lateinit var submitBtn: CardView
    private val validPhoneNumberPattern by lazy { "^(\\+\\d{1,3}[- ]?)?\\d{10}\$" }
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var loaderSend: LottieAnimationView
    private lateinit var phoneNum: String
    private lateinit var backBtn: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_send, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        loadData()

        phoneEditText = view.findViewById(R.id.phoneEdiText_send)
        submitBtn = view.findViewById(R.id.submit_btn_send)
        whiteView = view.findViewById(R.id.whiteView_send)
        loaderSend = view.findViewById(R.id.loader_send)
        backBtn = view.findViewById(R.id.back_btn_send)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        submitBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loaderSend.visibility =View.VISIBLE
            val phone = phoneEditText.text.toString()
            if((phone.length != 10) or !phone.matches(validPhoneNumberPattern.toRegex())) {
                whiteView.visibility = View.GONE
                loaderSend.visibility =View.GONE
                phoneEditText.error = "Enter 10 digits valid phone number"
                Toast.makeText(requireContext(), "Enter 10 digits valid phone number", Toast.LENGTH_SHORT).show()
            } else if(phone == phoneNum) {
                whiteView.visibility = View.GONE
                loaderSend.visibility =View.GONE
                Toast.makeText(requireContext(), "You can't send money to your own number, try with different number", Toast.LENGTH_SHORT).show()
            }
            else {
                val bundle = Bundle()
                bundle.putString("walletId", "$phone@digital")
                whiteView.visibility = View.GONE
                loaderSend.visibility =View.GONE
                Navigation.findNavController(view).navigate(R.id.nav_final_pay, bundle)
            }
        }

        return view
    }

    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                dbViewModel.fetchAccountDetails(it)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.isNotEmpty()) {
                        phoneNum = list[1]
                    }
                }
            }
        }
    }

}