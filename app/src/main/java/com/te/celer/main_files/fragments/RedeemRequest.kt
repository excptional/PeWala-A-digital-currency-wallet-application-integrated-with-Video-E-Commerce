package com.te.celer.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.RedeemAdapter
import com.te.celer.main_files.models.RedeemItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.LocalStorage

class RedeemRequest : Fragment() {

    private var redeemItemsArray = arrayListOf<RedeemItems>()
    private lateinit var redeemAdapter: RedeemAdapter
    private lateinit var redeemShimmer: ShimmerFrameLayout
    private lateinit var recyclerview: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var nothingFoundText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var name: String
    private lateinit var phone: String
    private val localStorage = LocalStorage()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  view = inflater.inflate(R.layout.fragment_redeem_request, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        redeemShimmer = view.findViewById(R.id.redeem_request_shimmer)
        recyclerview = view.findViewById(R.id.recyclerView_redeem_request)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_redeem_request)
        mainLayout = view.findViewById(R.id.mainLayout_redeem_request)
        nothingFoundText = view.findViewById(R.id.nothingFound_redeem_request)

        loadData()

        redeemShimmer.startShimmer()
        redeemShimmer.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        redeemAdapter = RedeemAdapter(requireContext(), redeemItemsArray)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
//        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = redeemAdapter

        refreshLayout.setOnRefreshListener {
            redeemShimmer.startShimmer()
            redeemShimmer.visibility = View.VISIBLE
            mainLayout.visibility = View.GONE
            loadData()
        }

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        redeemItemsArray = arrayListOf()
        for (i in list) {
            if (i.exists()) {
                val redeemData = RedeemItems(
                    name,
                    phone,
                    i.getString("status"),
                    i.getString("request_send_time"),
                    i.getString("amount")
                )
                redeemItemsArray.add(redeemData)
            }
        }
        redeemAdapter.updateRedeemItems(redeemItemsArray)
        redeemShimmer.clearAnimation()
        redeemShimmer.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
        nothingFoundText.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        name = userdata!!["name"]!!
        phone = userdata["phone"]!!
        dbViewModel.fetchRedeemRequest(userdata["uid"]!!)
        dbViewModel.redeemRequestDetails.observe(viewLifecycleOwner) { list ->
            if(!list.isNullOrEmpty()) fetchData(list)
            else {
                nothingFoundText.visibility = View.VISIBLE
                mainLayout.visibility = View.GONE
                redeemShimmer.clearAnimation()
                redeemShimmer.visibility = View.GONE
                refreshLayout.isRefreshing = false
            }
        }
    }

}