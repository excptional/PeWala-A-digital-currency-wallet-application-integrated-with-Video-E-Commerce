package com.te.pewala.auth

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.te.pewala.R

class Discovery : Fragment() {

    private lateinit var signInBtn: CardView
    private lateinit var createAccBtn: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discovery, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        signInBtn = view.findViewById(R.id.btn_signIn_discovery)
        createAccBtn = view.findViewById(R.id.btn_create_account_discovery)

        signInBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_sign_in)
        }

        createAccBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("LastFragment", "Discovery")
            Navigation.findNavController(view).navigate(R.id.nav_register, bundle)
        }

        return view;
    }

}