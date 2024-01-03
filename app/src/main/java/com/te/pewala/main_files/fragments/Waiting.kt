package com.te.pewala.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.te.pewala.R

class Waiting : Fragment() {

    private lateinit var backBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_waiting, container, false)

        backBtn = view.findViewById(R.id.back_btn_waiting)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

}