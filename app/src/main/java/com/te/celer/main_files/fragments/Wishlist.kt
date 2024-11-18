package com.te.celer.main_files.fragments

//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.inputmethod.EditorInfo
//import android.view.inputmethod.InputMethodManager
//import android.widget.LinearLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//import com.te.pewala.R
//import com.te.pewala.R.db.AuthViewModel
//import com.te.pewala.R.db.DBViewModel
//import com.te.pewala.R.main_files.adapters.ProductsAdapter
//import com.te.pewala.R.main_files.items.ProductsItems
//import com.facebook.shimmer.ShimmerFrameLayout
//import com.google.android.material.textfield.TextInputEditText
//import com.google.firebase.firestore.DocumentSnapshot
//
//class Wishlist : Fragment() {
//
//    private lateinit var productsAdapter: ProductsAdapter
//    private var productsItemsArray = arrayListOf<ProductsItems>()
//    private var dbViewModel: DBViewModel? = null
//    private var authViewModel: AuthViewModel? = null
//    private lateinit var productsRecyclerView: RecyclerView
//    private lateinit var shimmerContainerProducts: ShimmerFrameLayout
//    private lateinit var notFoundText: TextView
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
//    private lateinit var searchET: TextInputEditText
//    private lateinit var searchView: LinearLayout
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_wishlist, container, false)
//
//        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
//        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_wishlist)
//        productsRecyclerView = view.findViewById(R.id.recyclerView_wishlist)
//        notFoundText = view.findViewById(R.id.notFound_wishlist)
//        searchET = view.findViewById(R.id.search_edit_text_wishlist)
//        searchView = view.findViewById(R.id.searchView_wishlist)
//
//        shimmerContainerProducts = view.findViewById(R.id.shimmer_view_wishlist)
//        shimmerContainerProducts.startShimmer()
//        shimmerContainerProducts.visibility = View.VISIBLE
//        productsRecyclerView.visibility = View.GONE
//
//        productsAdapter = ProductsAdapter(productsItemsArray)
//        productsRecyclerView.layoutManager = GridLayoutManager(view.context, 2)
//        productsRecyclerView.setHasFixedSize(true)
//        productsRecyclerView.setItemViewCacheSize(20)
//        productsRecyclerView.adapter = productsAdapter
//
//        swipeRefreshLayout.setOnRefreshListener {
//            shimmerContainerProducts.startShimmer()
//            shimmerContainerProducts.visibility = View.VISIBLE
//            productsRecyclerView.visibility = View.GONE
//            loadData()
//            searchET.text = null
//        }
//
//        loadData()
//
//        searchET.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                loadData()
//                dbViewModel!!.wishlistData.observe(viewLifecycleOwner) {
//                    fetchSearchedList(it, s.toString().toLowerCase())
//                    swipeRefreshLayout.isRefreshing = false
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                loadData()
//                dbViewModel!!.wishlistData.observe(viewLifecycleOwner) {
//                    fetchSearchedList(it, s.toString().toLowerCase())
//                    swipeRefreshLayout.isRefreshing = false
//                }
//            }
//        })
//
//        searchET.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(searchET.windowToken, 0)
//                searchET.clearFocus()
//                true // Return true to indicate that the action has been handled
//            } else {
//                false // Return false if the action is not handled
//            }
//        }
//
//        return view
//    }
//
//    private fun fetchProductsList(list: MutableList<DocumentSnapshot>?) {
//        productsItemsArray = arrayListOf()
//        if (list!!.size == 0) {
//            notFoundText.visibility = View.VISIBLE
//            shimmerContainerProducts.visibility = View.GONE
//            productsRecyclerView.visibility = View.GONE
//        } else {
//            for (i in list) {
//                if (i.exists()) {
//                    val acc = ProductsItems(
//                        i.getString("Product Name"),
//                        i.getString("Brand Name"),
//                        i.getString("Product Image"),
//                        i.getString("Category"),
//                        i.getString("Product Price"),
//                        i.getString("Quantity"),
//                        i.getString("Description"),
//                        i.getString("Tags"),
//                        i.getString("Ratings"),
//                        i.getString("Raters"),
//                        i.getString("Seller Name"),
//                        i.getString("Seller Image"),
//                        i.getString("Seller UID"),
//                        i.getString("Product ID")
//                    )
//                    productsItemsArray.add(acc)
//                }
//            }
//            productsAdapter.updateProducts(productsItemsArray)
//            shimmerContainerProducts.clearAnimation()
//            shimmerContainerProducts.visibility = View.GONE
//            productsRecyclerView.visibility = View.VISIBLE
//            searchView.visibility = View.VISIBLE
//            notFoundText.visibility = View.GONE
//            swipeRefreshLayout.isRefreshing = false
//        }
//    }
//
//    private fun fetchSearchedList(list: MutableList<DocumentSnapshot>?, s: String) {
//        productsItemsArray = arrayListOf()
//        if (list!!.size == 0) {
//            notFoundText.visibility = View.VISIBLE
//            shimmerContainerProducts.visibility = View.GONE
//            productsRecyclerView.visibility = View.GONE
//        } else {
//            for (i in list) {
//                if (i.exists() and i.getString("Tags")!!.contains(s)) {
//                    val acc = ProductsItems(
//                        i.getString("Product Name"),
//                        i.getString("Brand Name"),
//                        i.getString("Product Image"),
//                        i.getString("Category"),
//                        i.getString("Product Price"),
//                        i.getString("Quantity"),
//                        i.getString("Description"),
//                        i.getString("Tags"),
//                        i.getString("Ratings"),
//                        i.getString("Raters"),
//                        i.getString("Seller Name"),
//                        i.getString("Seller Image"),
//                        i.getString("Seller UID"),
//                        i.getString("Product ID")
//                    )
//                    productsItemsArray.add(acc)
//                }
//            }
//            if(productsItemsArray.isEmpty()) {
//                notFoundText.visibility = View.VISIBLE
//                shimmerContainerProducts.visibility = View.GONE
//                productsRecyclerView.visibility = View.GONE
//            } else {
//                productsAdapter.updateProducts(productsItemsArray)
//                shimmerContainerProducts.clearAnimation()
//                shimmerContainerProducts.visibility = View.GONE
//                productsRecyclerView.visibility = View.VISIBLE
//                searchView.visibility = View.VISIBLE
//                notFoundText.visibility = View.GONE
//                swipeRefreshLayout.isRefreshing = false
//            }
//        }
//    }
//
//    private fun loadData() {
//        authViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
//            if (user != null) {
//                dbViewModel!!.fetchWishlistItems(user.uid)
//                dbViewModel!!.wishlistData.observe(viewLifecycleOwner) {
//                    fetchProductsList(it)
//                    swipeRefreshLayout.isRefreshing = false
//                }
//            }
//        }
//    }
//}

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.ProductsAdapter
import com.te.celer.main_files.models.ProductsItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.LocalStorage

class Wishlist : Fragment() {

    private lateinit var productsAdapter: ProductsAdapter
    private var productsItemsArray = arrayListOf<ProductsItems>()
    private var dbViewModel: DBViewModel? = null
    private var authViewModel: AuthViewModel? = null
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var shimmerContainerProducts: ShimmerFrameLayout
    private lateinit var notFoundText: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchET: TextInputEditText
    private lateinit var searchView: LinearLayout
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wishlist, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_wishlist)
        productsRecyclerView = view.findViewById(R.id.recyclerView_wishlist)
        notFoundText = view.findViewById(R.id.notFound_wishlist)
        searchET = view.findViewById(R.id.search_edit_text_wishlist)
        searchView = view.findViewById(R.id.searchView_wishlist)

        shimmerContainerProducts = view.findViewById(R.id.shimmer_view_wishlist)
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
            loadData()
            searchET.text = null
        }

        loadData()

        searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loadData()
                dbViewModel!!.wishlistData.observe(viewLifecycleOwner) {
                    fetchSearchedList(it, s.toString().toLowerCase())
                    swipeRefreshLayout.isRefreshing = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadData()
                dbViewModel!!.wishlistData.observe(viewLifecycleOwner) {
                    fetchSearchedList(it, s.toString().toLowerCase())
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })

        searchET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchET.windowToken, 0)
                searchET.clearFocus()
                true // Return true to indicate that the action has been handled
            } else {
                false // Return false if the action is not handled
            }
        }

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
                    val acc = ProductsItems(
                        i.getString("product_name"),
                        i.getString("brand_name"),
                        i.getString("product_image_url"),
                        i.getString("category"),
                        i.getString("product_price"),
                        i.getString("stocks"),
                        i.getString("description"),
                        i.getString("tags"),
                        i.getString("ratings"),
                        i.getString("raters"),
                        i.getString("seller_name"),
                        i.getString("seller_image"),
                        i.getString("seller_uid"),
                        i.getString("product_id")
                    )
                    productsItemsArray.add(acc)
                }
            }
            productsAdapter.updateProducts(productsItemsArray)
            shimmerContainerProducts.clearAnimation()
            shimmerContainerProducts.visibility = View.GONE
            productsRecyclerView.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            notFoundText.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun fetchSearchedList(list: MutableList<DocumentSnapshot>?, s: String) {
        productsItemsArray = arrayListOf()
        if (list!!.size == 0) {
            notFoundText.visibility = View.VISIBLE
            shimmerContainerProducts.visibility = View.GONE
            productsRecyclerView.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists() and i.getString("tags")!!.toLowerCase().contains(s.toLowerCase())) {
                    val acc = ProductsItems(
                        i.getString("product_name"),
                        i.getString("brand_name"),
                        i.getString("product_image_url"),
                        i.getString("category"),
                        i.getString("product_price"),
                        i.getString("stocks"),
                        i.getString("description"),
                        i.getString("tags"),
                        i.getString("ratings"),
                        i.getString("raters"),
                        i.getString("seller_name"),
                        i.getString("seller_image"),
                        i.getString("seller_uid"),
                        i.getString("product_id")
                    )
                    productsItemsArray.add(acc)
                }
            }
            if(productsItemsArray.isEmpty()) {
                notFoundText.visibility = View.VISIBLE
                shimmerContainerProducts.visibility = View.GONE
                productsRecyclerView.visibility = View.GONE
            } else {
                productsAdapter.updateProducts(productsItemsArray)
                shimmerContainerProducts.clearAnimation()
                shimmerContainerProducts.visibility = View.GONE
                productsRecyclerView.visibility = View.VISIBLE
                searchView.visibility = View.VISIBLE
                notFoundText.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        dbViewModel!!.fetchWishlistItems(userdata!!["uid"]!!)
        dbViewModel!!.wishlistData.observe(viewLifecycleOwner) {
            fetchProductsList(it)
            swipeRefreshLayout.isRefreshing = false
        }
    }
}