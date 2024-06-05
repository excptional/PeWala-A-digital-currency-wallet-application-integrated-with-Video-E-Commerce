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
import com.airbnb.lottie.LottieAnimationView
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.MyOrdersAdapter
import com.te.pewala.main_files.items.MyOrdersItems
import com.google.firebase.firestore.DocumentSnapshot

class Orders : Fragment() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var ordersAdapter: MyOrdersAdapter
    private var ordersItemsArray = arrayListOf<MyOrdersItems>()
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var backBtn: ImageView
    private lateinit var noOrders: TextView
    private lateinit var loader: LottieAnimationView
//    private lateinit var mainLayout: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_my_orders)
        ordersRecyclerView = view.findViewById(R.id.recyclerView_my_orders)
        backBtn = view.findViewById(R.id.back_btn_my_orders)
        noOrders = view.findViewById(R.id.nothingFound_my_orders)
        loader = view.findViewById(R.id.loader_my_orders)
//        mainLayout = view.findViewById(R.id.main_layout_my_orders)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        ordersAdapter = MyOrdersAdapter(ordersItemsArray)
        ordersRecyclerView.layoutManager = LinearLayoutManager(view.context)
        ordersRecyclerView.setHasFixedSize(true)
        ordersRecyclerView.setItemViewCacheSize(20)
        ordersRecyclerView.adapter = ordersAdapter

        loadData()

        swipeRefreshLayout.setOnRefreshListener {
            ordersRecyclerView.visibility = View.GONE
            loader.visibility = View.VISIBLE
            loadData()
        }
        
        return view
    }

    private fun fetchMyOrders(list: MutableList<DocumentSnapshot>?) {
        ordersItemsArray = arrayListOf()
        if (list!!.size == 0) {
            ordersRecyclerView.visibility = View.VISIBLE
            loader.visibility = View.GONE
            noOrders.visibility = View.VISIBLE
            ordersRecyclerView.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        } else {
            for (i in list) {
                if (i.exists()) {
                    val order = MyOrdersItems(
                        i.getString("buyer_name"),
                        i.getString("buyer_uid"),
                        i.getString("seller_uid"),
                        i.getString("buyer_address"),
                        i.getString("order_time"),
                        i.getString("delivery_date"),
                        i.getString("status"),
                        i.getString("product_id"),
                        i.getString("product_name"),
                        i.getString("brand_name"),
                        i.getString("product_image_url"),
                        i.getString("category"),
                        i.getString("payable_amount"),
                        i.getString("quantity"),
                        i.getString("order_id"),
                        i.getString("product_rating")
                    )
                    ordersItemsArray.add(order)
                }
            }
            ordersAdapter.updateMyOrders(ordersItemsArray)
            ordersRecyclerView.visibility = View.VISIBLE
            noOrders.visibility = View.GONE
            loader.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
//                sellerUid = it.uid
                dbViewModel.fetchMyOrders(it.uid)
                dbViewModel.myOrdersData.observe(viewLifecycleOwner) { list1 ->
                    fetchMyOrders(list1)
                }
            }
        }
    }

}