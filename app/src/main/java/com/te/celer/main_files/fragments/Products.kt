package com.te.celer.main_files.fragments

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
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.ProductsAdapter
import com.te.celer.main_files.models.ProductsItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot

class Products : Fragment() {

    private lateinit var productsAdapter: ProductsAdapter
    private var productsItemsArray = arrayListOf<ProductsItems>()
    private var dbViewModel: DBViewModel? = null
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var shimmerContainerProducts: ShimmerFrameLayout
    private lateinit var notFoundText: TextView
    private lateinit var categoryText: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: LinearLayout
    private lateinit var searchET: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        val category = requireArguments().getString("Category").toString()

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_products)
        productsRecyclerView = view.findViewById(R.id.recyclerView_products)
        notFoundText = view.findViewById(R.id.notFound_products)
        categoryText = view.findViewById(R.id.category_products)
        searchET = view.findViewById(R.id.search_edit_text_products)
        searchView = view.findViewById(R.id.searchView_products)

        categoryText.text = category
        categoryText.visibility = View.GONE
        searchView.visibility = View.GONE

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
            searchET.text = null
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

        searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                dbViewModel!!.fetchProducts(category)
                dbViewModel!!.productsData.observe(viewLifecycleOwner) {
                    fetchSearchedList(it, s.toString().toLowerCase())
                    swipeRefreshLayout.isRefreshing = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dbViewModel!!.fetchProducts(category)
                dbViewModel!!.productsData.observe(viewLifecycleOwner) {
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
            categoryText.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists()) {
                    val product = ProductsItems(
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
                    productsItemsArray.add(product)
                }
            }
            productsAdapter.updateProducts(productsItemsArray)
            shimmerContainerProducts.clearAnimation()
            shimmerContainerProducts.visibility = View.GONE
            productsRecyclerView.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            notFoundText.visibility = View.GONE
            categoryText.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun fetchSearchedList(list: MutableList<DocumentSnapshot>?, s: String) {
        productsItemsArray = arrayListOf()
        if (list!!.size == 0) {
            notFoundText.visibility = View.VISIBLE
            shimmerContainerProducts.visibility = View.GONE
            productsRecyclerView.visibility = View.GONE
            categoryText.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists() and i.getString("tags")!!.contains(s)) {
                    val product = ProductsItems(
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
                    productsItemsArray.add(product)
                }
            }
            productsAdapter.updateProducts(productsItemsArray)
            shimmerContainerProducts.clearAnimation()
            shimmerContainerProducts.visibility = View.GONE
            productsRecyclerView.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            notFoundText.visibility = View.GONE
            categoryText.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }

}