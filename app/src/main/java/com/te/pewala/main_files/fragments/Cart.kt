package com.te.pewala.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.main_files.adapters.CartAdapter
import com.te.pewala.main_files.items.CartItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot

class Cart : Fragment() {

    private lateinit var cartAdapter: CartAdapter
    private var cartItemsArray = arrayListOf<CartItems>()
    private var dbViewModel: DBViewModel? = null
    private var authViewModel: AuthViewModel? = null
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var shimmerContainerProduct: ShimmerFrameLayout
    private lateinit var notFoundText: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var backBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_cart)
        cartRecyclerView = view.findViewById(R.id.recyclerView_cart)
        notFoundText = view.findViewById(R.id.notFound_cart)
        mainLayout = view.findViewById(R.id.main_layout_cart)
        backBtn = view.findViewById(R.id.back_btn_cart)

        shimmerContainerProduct = view.findViewById(R.id.shimmer_view_cart)
        shimmerContainerProduct.startShimmer()
        shimmerContainerProduct.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        cartAdapter = CartAdapter(requireContext(), this, viewLifecycleOwner, cartItemsArray)
        cartRecyclerView.layoutManager = LinearLayoutManager(view.context)
        cartRecyclerView.setHasFixedSize(true)
        cartRecyclerView.setItemViewCacheSize(20)
        cartRecyclerView.adapter = cartAdapter

        loadData()

        swipeRefreshLayout.setOnRefreshListener {
            shimmerContainerProduct.startShimmer()
            shimmerContainerProduct.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
            loadData()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun fetchCartList(list: MutableList<DocumentSnapshot>?) {
        cartItemsArray = arrayListOf()
        if (list!!.size == 0) {
            notFoundText.visibility = View.VISIBLE
            shimmerContainerProduct.visibility = View.GONE
            mainLayout.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists()) {
                    val acc = CartItems(
                        i.getString("Product Name"),
                        i.getString("Brand Name"),
                        i.getString("Product Image"),
                        i.getString("Category"),
                        i.getString("Product Price"),
                        i.getString("Quantity"),
                        i.getString("Description"),
                        i.getString("Seller Name"),
                        i.getString("Seller Image"),
                        i.getString("Seller UID"),
                        i.getString("Ratings"),
                        i.getString("Product ID")
                    )
                    cartItemsArray.add(acc)
                }
            }
            cartAdapter.updateCart(cartItemsArray)
            shimmerContainerProduct.clearAnimation()
            shimmerContainerProduct.visibility = View.GONE
            mainLayout.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.VISIBLE
            notFoundText.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadData() {
        authViewModel!!.userdata.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                dbViewModel!!.fetchCartItems(user.uid)
                dbViewModel!!.cartData.observe(viewLifecycleOwner) {
                    fetchCartList(it)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

}