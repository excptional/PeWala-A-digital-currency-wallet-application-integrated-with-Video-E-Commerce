package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.R
import com.te.celer.db.DBViewModel
import com.te.celer.db.LocalStorage
import com.te.celer.main_files.adapters.PendingPaymentsAdapter
import com.te.celer.main_files.models.PendingPaymentsItems

class Pending : Fragment() {

    private lateinit var pendingPaymentsAdapter: PendingPaymentsAdapter
    private var pendingPaymentsItemsArray = arrayListOf<PendingPaymentsItems>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var notFoundText: TextView
    private lateinit var shimmerPendingPayments: ShimmerFrameLayout
    private val dbViewModel: DBViewModel by viewModels()
    private lateinit var uid: String
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_pending)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_pending)
        notFoundText = view.findViewById(R.id.nothingFound_pending)
        shimmerPendingPayments = view.findViewById(R.id.pending_payment_shimmer)

        pendingPaymentsAdapter = PendingPaymentsAdapter(pendingPaymentsItemsArray)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = pendingPaymentsAdapter

        refreshLayout.setOnRefreshListener {
            dbViewModel.fetchSellerDuePayment(uid)
            dbViewModel.pendingPaymentsData.observe(viewLifecycleOwner) {
                fetchPendingPaymentList(it)
            }
        }

        loadData()

        return view
    }

    private fun fetchPendingPaymentList(list: MutableList<DocumentSnapshot>?) {
        pendingPaymentsItemsArray = arrayListOf()
        if (list!!.size == 0) {
            notFoundText.visibility = View.VISIBLE
            shimmerPendingPayments.visibility = View.GONE
            recyclerView.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists() && i.getString("payment_status") == "Pending") {
                    val data = PendingPaymentsItems(
                        i.getString("amount"),
                        i.getString("order_id"),
                        i.getString("time"),
                        i.getString("payment_status")
                    )
                    pendingPaymentsItemsArray.add(data)
                }
            }
            pendingPaymentsAdapter.updatePendingPaymentsItems(pendingPaymentsItemsArray)
            shimmerPendingPayments.clearAnimation()
            shimmerPendingPayments.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            notFoundText.visibility = View.GONE
            refreshLayout.isRefreshing = false
        }
    }


    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!
        dbViewModel.fetchSellerDuePayment(uid)
        dbViewModel.pendingPaymentsData.observe(viewLifecycleOwner) {
            fetchPendingPaymentList(it)
        }
    }

}