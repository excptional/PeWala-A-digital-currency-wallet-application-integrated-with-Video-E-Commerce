package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.main_files.adapters.PendingRequestAdapter
import com.example.trigeredgedigitalcurrencyproject.main_files.adapters.RedeemAdapter
import com.example.trigeredgedigitalcurrencyproject.main_files.items.RedeemItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot

class Wallet : Fragment() {

    private var redeemItemsArray = arrayListOf<RedeemItems>()
    private lateinit var redeemAdapter: PendingRequestAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var backBtn: ImageButton
    private lateinit var addMoney: CardView
    private lateinit var withdrawMoney: CardView
    private lateinit var walletBalance: TextView
    private lateinit var whiteView: View
    private lateinit var loaderWallet: LottieAnimationView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var titlePendingRequest: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        loadData()

        backBtn = view.findViewById(R.id.back_btn_wallet)
        addMoney = view.findViewById(R.id.add_money_btn_wallet)
        withdrawMoney = view.findViewById(R.id.withdraw_money_btn_wallet)
        walletBalance = view.findViewById(R.id.balance_wallet)
        whiteView = view.findViewById(R.id.whiteView_wallet)
        loaderWallet = view.findViewById(R.id.loader_wallet)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_wallet)
        mainLayout = view.findViewById(R.id.main_layout_wallet)
        recyclerview = view.findViewById(R.id.pending_request_recyclerview_wallet)
        titlePendingRequest = view.findViewById(R.id.title_pending_request_wallet)

        redeemAdapter = PendingRequestAdapter(requireContext(), redeemItemsArray)
        recyclerview.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = redeemAdapter

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        refreshLayout.setOnRefreshListener {
            loadData()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        addMoney.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_add, null, navBuilder.build())
        }

        withdrawMoney.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_redeem, null, navBuilder.build())
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                dbViewModel.fetchAccountDetails(user.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list1 ->
                    if (list1.isNotEmpty()) {
                        walletBalance.text = "₹${list1[5]}"
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderWallet.visibility = View.GONE
                        refreshLayout.isRefreshing = false
                        name = list1[0]
                        phone = list1[1]
                        dbViewModel.fetchRedeemRequest(user.uid)
                        dbViewModel.redeemRequestDetails.observe(viewLifecycleOwner) { list2 ->
                            if(list2.isNotEmpty()) fetchData(list2)
                            else {
                                recyclerview.visibility = View.GONE
                                refreshLayout.isRefreshing = false
                                titlePendingRequest.visibility = View.GONE
                            }
                        }
                    } else {
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderWallet.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        redeemItemsArray = arrayListOf()
        for (i in list) {
            if (i.exists()) {
                val redeemData = RedeemItems(
                    name,
                    phone,
                    i.getString("Status"),
                    i.getString("Request send"),
                    i.getString("Amount")
                )
                redeemItemsArray.add(redeemData)
            }
        }
        redeemAdapter.updateRedeemItems(redeemItemsArray)
        recyclerview.visibility = View.VISIBLE
        titlePendingRequest.visibility =View.VISIBLE
        refreshLayout.isRefreshing = false
    }

}