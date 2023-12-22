package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.main_files.adapters.TransactionHistoryAdapter
import com.example.trigeredgedigitalcurrencyproject.main_files.items.TransactionHistoryItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class History : Fragment() {

    private var transactionHistoryItemsArray = arrayListOf<TransactionHistoryItems>()
    private lateinit var transactionHistoryAdapter: TransactionHistoryAdapter
    private var transactionHistoryItems = arrayListOf<TransactionHistoryItems>()
    private lateinit var historyShimmer: ShimmerFrameLayout
    private lateinit var recyclerview: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var nothingFoundText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var uid: String
    private lateinit var phone: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        loadData()

        historyShimmer = view.findViewById(R.id.history_shimmer)
        recyclerview = view.findViewById(R.id.recyclerView_history)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_history)
        mainLayout = view.findViewById(R.id.mainLayout_history)
        nothingFoundText = view.findViewById(R.id.nothingFound_history)

        historyShimmer.startShimmer()
        historyShimmer.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        transactionHistoryAdapter =
            TransactionHistoryAdapter(requireContext(), this, viewLifecycleOwner, transactionHistoryItems)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = transactionHistoryAdapter

        getData()

        refreshLayout.setOnRefreshListener {
            historyShimmer.startShimmer()
            historyShimmer.visibility = View.VISIBLE
            mainLayout.visibility = View.GONE
            getData()
        }

        return view
    }

    private fun getData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                dbViewModel.fetchTransactionDetails(it.uid)
                dbViewModel.transactionDetails.observe(viewLifecycleOwner) { list ->
                    if (list.isNotEmpty()) {
                        nothingFoundText.visibility = View.GONE
                        fetchData(list)
                    } else {
                        historyShimmer.visibility = View.GONE
                        mainLayout.visibility = View.GONE
                        nothingFoundText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        transactionHistoryItemsArray = arrayListOf()
        for (i in list) {
            if (i.exists()) {
                if(i.getString("Operator Id")!!.isEmpty()) {
                    val transactionData = TransactionHistoryItems(
                        uid,
                        i.getString("TId"),
                        i.getString("Operation"),
                        i.getString("Time"),
                        i.getString("Amount")
                    )
                    transactionHistoryItemsArray.add(transactionData)
                } else {
                    val transactionData = TransactionHistoryItems(
                        i.getString("Operator Id"),
                        i.getString("TId"),
                        i.getString("Operation"),
                        i.getString("Time"),
                        i.getString("Amount")
                    )
                    transactionHistoryItemsArray.add(transactionData)
                }
            }
        }
        transactionHistoryAdapter.updateTransactionHistory(transactionHistoryItemsArray)
        historyShimmer.clearAnimation()
        historyShimmer.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
        refreshLayout.isRefreshing = false
    }

    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                uid = it.uid;
//                dbViewModel.fetchAccountDetails(it.uid)
//                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
//                    name = list.getString("Name").toString()
//                    phone = list.getString("Phone").toString()
//                }
            }
        }
    }
}