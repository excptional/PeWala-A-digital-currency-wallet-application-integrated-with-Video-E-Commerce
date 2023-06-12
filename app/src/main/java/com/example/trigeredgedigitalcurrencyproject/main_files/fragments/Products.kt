package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.db.Response
import com.example.trigeredgedigitalcurrencyproject.main_files.adapters.ProductsAdapter
import com.example.trigeredgedigitalcurrencyproject.main_files.items.ProductsItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot

class Products : Fragment() {

    private lateinit var productsAdapter: ProductsAdapter
    private var productsItemsArray = arrayListOf<ProductsItems>()
    private var dbViewModel: DBViewModel? = null
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var shimmerContainerProducts: ShimmerFrameLayout
    private lateinit var notFoundText: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        val category = requireArguments().getString("Category").toString()

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_products)
        productsRecyclerView = view.findViewById(R.id.recyclerView_products)
        notFoundText = view.findViewById(R.id.notFound_products)

        shimmerContainerProducts = view.findViewById(R.id.shimmer_view_products)
        shimmerContainerProducts.startShimmer()
        shimmerContainerProducts.visibility = View.VISIBLE
        productsRecyclerView.visibility = View.GONE

        productsAdapter = ProductsAdapter(productsItemsArray)
        productsRecyclerView.layoutManager = GridLayoutManager(view.context, 2)
        productsRecyclerView.setHasFixedSize(true)
        productsRecyclerView.setItemViewCacheSize(20)
        productsRecyclerView.adapter = productsAdapter

        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerProducts.startShimmer()
            shimmerContainerProducts.visibility = View.VISIBLE
            productsRecyclerView.visibility = View.GONE
            dbViewModel!!.fetchProducts(category)
            dbViewModel!!.productsData.observe(viewLifecycleOwner) {
                fetchProductsList(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        dbViewModel!!.fetchProducts(category)
        dbViewModel!!.productsData.observe(viewLifecycleOwner) {
            fetchProductsList(it)
            swipeRefreshLayout.isRefreshing = false
        }

//        dbViewModel!!.dbResponse.observe(viewLifecycleOwner) {
//            when (it) {
//                is Response.Success -> {
//                    dbViewModel!!.productsData.observe(viewLifecycleOwner) { list ->
//                        fetchProductsList(list)
//                        swipeRefreshLayout.isRefreshing = false
//                    }
//                }
//                is Response.Failure -> {
//                    Toast.makeText(requireActivity(), it.errorMassage, Toast.LENGTH_SHORT).show()
//                    productsRecyclerView.visibility = View.VISIBLE
//                    shimmerContainerProducts.clearAnimation()
//                    shimmerContainerProducts.visibility = View.GONE
//                }
//            }
//        }

        return view
    }

    private fun fetchProductsList(list: MutableList<DocumentSnapshot>?) {
        productsItemsArray = arrayListOf()
        if (list!!.size == 0) {
            notFoundText.visibility = View.VISIBLE
            shimmerContainerProducts.visibility = View.GONE
            productsRecyclerView.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists()) {
                    dbViewModel!!.fetchAccountDetails(i.getString("Seller ID").toString())
                    dbViewModel!!.accDetails.observe(viewLifecycleOwner) {
                        if (list.isNotEmpty()) {
                            val acc = ProductsItems(
                                i.getString("Product Name"),
                                i.getString("Brand Name"),
                                i.getString("Product Image"),
                                i.getString("Product Price"),
                                i.getString("Quantity"),
                                i.getString("Description"),
                                i.getString("tags"),
                                i.getString("Ratings"),
                                i.getString("Raters"),
                                it[0],
                                it[3],
                                i.getString("Product ID")
                            )
                            productsItemsArray.add(acc)
                            productsAdapter.updateProducts(productsItemsArray)
                            shimmerContainerProducts.clearAnimation()
                            shimmerContainerProducts.visibility = View.GONE
                            productsRecyclerView.visibility = View.VISIBLE
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }

        }
    }

}