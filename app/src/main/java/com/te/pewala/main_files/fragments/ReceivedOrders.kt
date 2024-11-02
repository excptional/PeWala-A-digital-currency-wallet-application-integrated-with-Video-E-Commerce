package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.SellerReceivedOrdersAdapter
import com.te.pewala.main_files.models.SellerReceivedOrdersItems

class ReceivedOrders : Fragment() {

    private lateinit var backBtn: ImageView
    private lateinit var notFoundText: TextView
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var receivedOrdersAdapter: SellerReceivedOrdersAdapter
    private var receivedOrdersItemsArray = arrayListOf<SellerReceivedOrdersItems>()
    private lateinit var uid: String
    private val dbViewModel: DBViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_received_orders, container, false)
        
        requireActivity().window.statusBarColor = Color.WHITE

        uid = requireArguments().getString("uid")!!

        notFoundText = view.findViewById(R.id.nothing_found_received_orders)
        shimmer = view.findViewById(R.id.shimmer_received_orders)
        recyclerView = view.findViewById(R.id.recyclerview_received_orders)
        backBtn = view.findViewById(R.id.back_btn_received_orders)

        shimmer.startShimmer()
        shimmer.animate()

        receivedOrdersAdapter =
            SellerReceivedOrdersAdapter(receivedOrdersItemsArray)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setItemViewCacheSize(20)
        recyclerView.adapter = receivedOrdersAdapter

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        receivedOrdersItemsArray = arrayListOf()
        if (list.isNullOrEmpty()) {
            notFoundText.visibility = View.VISIBLE
        } else {
            for(item in list) {
                val order = SellerReceivedOrdersItems(
                    item.getString("buyer_name"),
                    item.getString("buyer_uid"),
                    item.getString("seller_uid"),
                    item.getString("buyer_address"),
                    item.getString("order_time"),
                    item.getString("delivery_date"),
                    item.getString("status"),
                    item.getString("product_name"),
                    item.getString("brand_name"),
                    item.getString("product_image_url"),
                    item.getString("category"),
                    item.getString("payable_amount"),
                    item.getString("quantity"),
                    item.getString("order_id"),
                    item.getString("confirmation_code")
                )
                receivedOrdersItemsArray.add(order)
            }
            receivedOrdersAdapter.updateSellerReceivedOrders(receivedOrdersItemsArray)
            notFoundText.visibility = View.GONE
        }
        shimmer.visibility = View.GONE
    }

    private fun loadData() {
        if (!uid.isNullOrEmpty()) {
            dbViewModel.fetchReceivedOrders(uid)
            dbViewModel.sellerReceivedOrdersData.observe(viewLifecycleOwner) { list ->
                fetchData(list)
            }
        }
    }

}