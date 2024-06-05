package com.te.pewala.main_files.fragments

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
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.RedeemAdapter
import com.te.pewala.main_files.items.RedeemItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot

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
        authViewModel.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                dbViewModel.fetchAccountDetails(user.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) {
                    name = it.getString("name").toString()
                    phone = it.getString("phone").toString()
                    dbViewModel.fetchRedeemRequest(user.uid)
                    dbViewModel.redeemRequestDetails.observe(viewLifecycleOwner) { list ->
                        if(list.isNotEmpty()) fetchData(list)
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
        }
    }

}