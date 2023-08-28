package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trigeredgedigitalcurrencyproject.R
class SuccessfullyOrderPlaced : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_successfully_order_placed, container, false)

        return view
    }
}