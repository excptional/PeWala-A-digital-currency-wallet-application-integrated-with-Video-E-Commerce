package com.te.celer.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.te.celer.R

class Waiting : Fragment() {

    private lateinit var backBtn: ImageView

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