package com.te.pewala.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.te.pewala.R

class UserType : Fragment() {

    private lateinit var sellerCard: CardView
    private lateinit var buyerCard: CardView
    private lateinit var backBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_type, container, false)

        sellerCard = view.findViewById(R.id.seller_user_type)
        buyerCard = view.findViewById(R.id.buyer_user_type)
        backBtn = view.findViewById(R.id.back_btn_user_type)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        sellerCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userType", "Seller")
            Navigation.findNavController(view).popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_sign_up, bundle)
        }

        buyerCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userType", "Buyer")
            Navigation.findNavController(view).popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_sign_up, bundle)
        }

        return view
    }

}