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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.SellerProductsAdapter
import com.te.pewala.main_files.models.SellerProductsItems

class SellerProducts : Fragment() {

    private lateinit var backBtn: ImageView
    private lateinit var notFoundText: TextView
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var sellerProductsAdapter: SellerProductsAdapter
    private var sellerProductsItemsArray = arrayListOf<SellerProductsItems>()
    private lateinit var uid: String
    private lateinit var refreshLayout: SwipeRefreshLayout
    private val dbViewModel: DBViewModel by viewModels()
    
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_seller_products, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        uid = requireArguments().getString("uid")!!

        notFoundText = view.findViewById(R.id.nothing_found_seller_products)
        shimmer = view.findViewById(R.id.shimmer_seller_products)
        recyclerView = view.findViewById(R.id.recyclerview_seller_products)
        backBtn = view.findViewById(R.id.back_btn_seller_products)
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout_seller_products)

        shimmer.startShimmer()
        shimmer.animate()

        sellerProductsAdapter =
            SellerProductsAdapter(sellerProductsItemsArray)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.adapter = sellerProductsAdapter

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        refreshLayout.setOnRefreshListener {
            dbViewModel.fetchSellerProducts(uid)
            dbViewModel.sellerProductsData.observe(viewLifecycleOwner) { list ->
                fetchData(list)
            }
        }

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>?) {
        sellerProductsItemsArray = arrayListOf()
        if (list.isNullOrEmpty()) {
            notFoundText.visibility = View.VISIBLE
        } else {
            for(item in list) {
                val product = SellerProductsItems(
                    item.getString("product_name"),
                    item.getString("brand_name"),
                    item.getString("product_image_url"),
                    item.getString("category"),
                    item.getString("product_price"),
                    item.getString("stocks"),
                    item.getString("description"),
                    item.getString("tags"),
                    item.getString("ratings"),
                    item.getString("raters"),
                    item.getString("seller_name"),
                    item.getString("seller_image"),
                    item.getString("seller_uid"),
                    item.getString("product_id")
                )
                sellerProductsItemsArray.add(product)
            }
            sellerProductsAdapter.updateSellerProducts(sellerProductsItemsArray)
            notFoundText.visibility = View.GONE
            shimmer.visibility = View.GONE
            refreshLayout.isRefreshing = false
        }
    }

    private fun loadData() {
        if (!uid.isNullOrEmpty()) {
            dbViewModel.fetchSellerProducts(uid)
            dbViewModel.sellerProductsData.observe(viewLifecycleOwner) { list ->
                fetchData(list)
            }
        }
    }
    
}