package com.example.trigeredgedigitalcurrencyproject.main_files

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import io.github.g0dkar.qrcode.QRCode
import io.github.g0dkar.qrcode.render.Colors
import java.io.FileOutputStream

class Receive : Fragment() {

    private lateinit var dbViewModel: DBViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var qrImage: ImageView
    private lateinit var cardId: TextView
    private lateinit var whiteView: View
    private lateinit var loaderReceive: LottieAnimationView
    private lateinit var mainLayout: LinearLayout
    private lateinit var backBtn: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_receive, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        qrImage = view.findViewById(R.id.qr_image)
        cardId = view.findViewById(R.id.card_id_receive)
        mainLayout = view.findViewById(R.id.main_layout_receive)
        whiteView = view.findViewById(R.id.whiteView_receive)
        loaderReceive = view.findViewById(R.id.loader_receive)
        backBtn = view.findViewById(R.id.back_btn_receive)

        loadData(view)

        val backBuilder = NavOptions.Builder()
        backBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.slide_out)

        backBtn.setOnClickListener {
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_home, null, backBuilder.build())
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(view: View) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if(it != null) {
                dbViewModel.fetchAccountDetails(it)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if(list.isNotEmpty()) {
                        Glide.with(view).load(list[4]).into(qrImage)
                        cardId.text = "Your card id : ${list[2]}"
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderReceive.visibility = View.GONE
                    } else {
                        whiteView.visibility = View.GONE
                        loaderReceive.visibility = View.GONE
                        mainLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

}