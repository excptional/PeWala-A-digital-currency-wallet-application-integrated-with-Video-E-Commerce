package com.example.trigeredgedigitalcurrencyproject.main_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.main_files.slider_files.SliderAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.smarteist.autoimageslider.SliderView


class Home : Fragment() {

    private lateinit var imageUrl: ArrayList<String>
    private lateinit var sliderView: SliderView
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var send: LinearLayout
    private lateinit var add: LinearLayout
    private lateinit var receive: LinearLayout
    private lateinit var redeem: LinearLayout
    private lateinit var viewWallet: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewWallet = view.findViewById(R.id.view_wallet)
        send = view.findViewById(R.id.send_money)
        add = view.findViewById(R.id.add_money)
        receive = view.findViewById(R.id.receive_money)
        redeem = view.findViewById(R.id.redeem_money)

        sliderView = view.findViewById(R.id.slider)

        imageUrl = ArrayList()
        imageUrl =
            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/13416072_5243336.png?alt=media&token=5190cbdc-9b47-4b21-b443-c6905da38d96") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/20824349_6342757.png?alt=media&token=94e820f8-2cfa-4848-b543-8b1c512e1738") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/9457133_4137379.png?alt=media&token=e14c5339-d2a9-47c8-a04b-4c7222c01ffa") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/20827766_Hand%20holding%20phone%20with%20digital%20wallet%20service%20and%20sending%20money.png?alt=media&token=bb05263a-4cf6-4848-b946-e59db5983ea8") as ArrayList<String>

        sliderAdapter = SliderAdapter(imageUrl)
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)


        add.setOnClickListener {
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_add, null, navBuilder.build())
        }

        send.setOnClickListener {
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_send, null, navBuilder.build())
        }

        receive.setOnClickListener {
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_receive, null, navBuilder.build())
        }

        redeem.setOnClickListener {
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_redeem, null, navBuilder.build())
        }

        viewWallet.setOnClickListener {
            requireFragmentManager().popBackStack()
            Navigation.findNavController(view).navigate(R.id.nav_wallet, null, navBuilder.build())
        }


        return view
    }

}