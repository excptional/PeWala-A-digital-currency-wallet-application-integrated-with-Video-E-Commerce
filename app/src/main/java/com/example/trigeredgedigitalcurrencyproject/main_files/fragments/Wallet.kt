package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel

class Wallet : Fragment() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var backBtn: ImageButton
    private lateinit var addMoney: CardView
    private lateinit var walletBalance: TextView
    private lateinit var whiteView: View
    private lateinit var loaderWallet: LottieAnimationView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var mainLayout: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        loadData(view)

        backBtn = view.findViewById(R.id.back_btn_wallet)
        addMoney = view.findViewById(R.id.add_money_btn_wallet)
        walletBalance = view.findViewById(R.id.balance_wallet)
        whiteView = view.findViewById(R.id.whiteView_wallet)
        loaderWallet = view.findViewById(R.id.loader_wallet)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_wallet)
        mainLayout = view.findViewById(R.id.main_layout_wallet)

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        refreshLayout.setOnRefreshListener {
            loadData(view)
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        addMoney.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_add, null, navBuilder.build())
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(view: View) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                dbViewModel.fetchAccountDetails(it)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if (list.isNotEmpty()) {
                        walletBalance.text = "₹${list[5]}"
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderWallet.visibility = View.GONE
                        refreshLayout.isRefreshing = false
                    } else {
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderWallet.visibility = View.GONE
                    }
                }
            }
        }
    }

}