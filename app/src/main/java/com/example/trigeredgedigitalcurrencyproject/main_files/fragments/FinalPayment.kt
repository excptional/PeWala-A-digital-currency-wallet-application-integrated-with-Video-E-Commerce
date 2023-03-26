package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView

class FinalPayment : Fragment() {

    private lateinit var whiteView: View
    private lateinit var loaderFinalPay: LottieAnimationView
    private lateinit var mainLayout: LinearLayout
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var walletId: TextView
    private lateinit var amount: TextInputEditText
    private lateinit var note: TextInputEditText
    private lateinit var payBtn: CardView
    private lateinit var profileImg: CircleImageView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_final_payment, container, false)

        name = view.findViewById(R.id.name_final_pay)
        phone = view.findViewById(R.id.phone_final_pay)
        walletId = view.findViewById(R.id.walletId_final_pay)
        amount = view.findViewById(R.id.amount_final_pay)
        note = view.findViewById(R.id.note_final_pay)
        payBtn = view.findViewById(R.id.pay_btn_final_pay)
        profileImg = view.findViewById(R.id.img_final_pay)
        whiteView = view.findViewById(R.id.whiteView_final_pay)
        loaderFinalPay = view.findViewById(R.id.loader_final_pay)
        mainLayout = view.findViewById(R.id.main_layout_final_pay)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        walletId.text = requireArguments().getString("walletId").toString()

        dbViewModel.getPayerDetails(requireArguments().getString("walletId").toString())

        dbViewModel.payerDetails.observe(viewLifecycleOwner) { list ->
            if(list.isNullOrEmpty()) {
                whiteView.visibility = View.GONE
                loaderFinalPay.visibility = View.GONE
                Toast.makeText(requireContext(), "Your given phone number is not registered in this app", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            } else {
                name.text = "Paying ${list[0]}"
                phone.text = "Phone : ${list[1]}"
                Glide.with(this).load(list[2]).into(profileImg)
                mainLayout.visibility = View.VISIBLE
                whiteView.visibility = View.GONE
                loaderFinalPay.visibility = View.GONE
            }
        }

        return view
    }

}