package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.trigeredgedigitalcurrencyproject.R

class Shop : Fragment() {

    private lateinit var addProduct: CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        addProduct = view.findViewById(R.id.addProduct)

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        addProduct.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_add_product, null, navBuilder.build())
        }

        return view
    }

}