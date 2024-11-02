package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.TransactionHistoryAdapter
import com.te.pewala.main_files.models.TransactionHistoryItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.db.LocalStorage

class History_ : Fragment() {

    private val localStorage = LocalStorage()
    private lateinit var transactionHistoryAdapter: TransactionHistoryAdapter
    private var transactionHistoryItems = arrayListOf<TransactionHistoryItems>()
    private lateinit var historyShimmer: ShimmerFrameLayout
    private lateinit var recyclerview: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var nothingFoundText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var backBtn: ImageView
    private lateinit var uid: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        historyShimmer = view.findViewById(R.id.history_shimmer)
        recyclerview = view.findViewById(R.id.recyclerView_history_)
        refreshLayout = view.findViewById(R.id.swipe_refresh_layout_history_)
        mainLayout = view.findViewById(R.id.mainLayout_history_)
        nothingFoundText = view.findViewById(R.id.nothingFound_history_)
        backBtn = view.findViewById(R.id.back_btn_history_)

        transactionHistoryAdapter =
            TransactionHistoryAdapter(requireContext(), transactionHistoryItems)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = transactionHistoryAdapter

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        historyShimmer.startShimmer()
        historyShimmer.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        refreshLayout.setOnRefreshListener {
            historyShimmer.startShimmer()
            historyShimmer.visibility = View.VISIBLE
            mainLayout.visibility = View.GONE
            getData()
        }

        return view
    }

    private fun getData() {
        dbViewModel.fetchTransactionDetails(uid)
        dbViewModel.transactionDetails.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                nothingFoundText.visibility = View.GONE
                fetchData(list)
            } else {
                historyShimmer.visibility = View.GONE
                mainLayout.visibility = View.GONE
                nothingFoundText.visibility = View.VISIBLE
                refreshLayout.isRefreshing = false
            }
        }
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        transactionHistoryItems = arrayListOf()
        for (i in list) {
            if (i.exists()) {
                val transactionData = TransactionHistoryItems(
                    i.getString("amount"),
                    i.getString("operation"),
                    i.getString("tid"),
                    i.getString("time"),
                    i.getString("operator_name"),
                    i.getString("operator_phone")
                )
                transactionHistoryItems.add(transactionData)

            }
        }
        transactionHistoryAdapter.updateTransactionHistory(transactionHistoryItems)
        historyShimmer.clearAnimation()
        historyShimmer.visibility = View.GONE
        mainLayout.visibility = View.VISIBLE
        refreshLayout.isRefreshing = false
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(),"user_data")
        uid = userdata!!["uid"]!!
        getData()
    }
}