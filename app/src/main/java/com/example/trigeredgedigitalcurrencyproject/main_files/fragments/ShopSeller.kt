package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.main_files.adapters.ProductsAdapter
import com.example.trigeredgedigitalcurrencyproject.main_files.adapters.SellerProductsAdapter
import com.example.trigeredgedigitalcurrencyproject.main_files.adapters.SellerReceivedOrdersAdapter
import com.example.trigeredgedigitalcurrencyproject.main_files.items.ProductsItems
import com.example.trigeredgedigitalcurrencyproject.main_files.items.SellerProductsItems
import com.example.trigeredgedigitalcurrencyproject.main_files.items.SellerReceivedOrdersItems
import com.google.firebase.firestore.DocumentSnapshot

class ShopSeller : Fragment() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var ordersAdapter: SellerReceivedOrdersAdapter
    private var ordersItemsArray = arrayListOf<SellerReceivedOrdersItems>()
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersLayout: LinearLayout
    private lateinit var productsAdapter: SellerProductsAdapter
    private var productsItemsArray = arrayListOf<SellerProductsItems>()
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productsLayout: LinearLayout
    private lateinit var loader: LottieAnimationView
    private lateinit var noOrders: TextView
    private lateinit var mainLayout: ScrollView
    private lateinit var sellerUid: String
    private lateinit var contentOrdersLayout: LinearLayout
    private lateinit var arrowPendingOrder: ImageView
    private lateinit var addProduct: CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_seller, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_seller_shop)
        productsRecyclerView = view.findViewById(R.id.seller_products_recyclerView_seller_shop)
        ordersRecyclerView = view.findViewById(R.id.pendingOrdersRecyclerView_seller_shop)
        noOrders = view.findViewById(R.id.noOrders_seller_shop)
        loader = view.findViewById(R.id.progressbar_seller_shop)
        productsLayout = view.findViewById(R.id.products_layout__seller_shop)
        ordersLayout = view.findViewById(R.id.pendingOrdersLayout_seller_shop)
        mainLayout = view.findViewById(R.id.main_layout_seller_shop)
        contentOrdersLayout = view.findViewById(R.id.contentOrderLayout_seller_shop)
        arrowPendingOrder = view.findViewById(R.id.arrow_order_seller_shop)
        addProduct = view.findViewById(R.id.addProduct_seller_shop)

        productsAdapter = SellerProductsAdapter(productsItemsArray)
        productsRecyclerView.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        productsRecyclerView.setHasFixedSize(true)
        productsRecyclerView.setItemViewCacheSize(20)
        productsRecyclerView.adapter = productsAdapter

        ordersAdapter = SellerReceivedOrdersAdapter(ordersItemsArray)
        ordersRecyclerView.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        ordersRecyclerView.setHasFixedSize(true)
        ordersRecyclerView.setItemViewCacheSize(20)
        ordersRecyclerView.adapter = ordersAdapter

        loadData()

        swipeRefreshLayout.setOnRefreshListener {
            loader.visibility = View.VISIBLE
            mainLayout.visibility = View.GONE
            loadData()
        }

        ordersLayout.setOnClickListener {
            if (!contentOrdersLayout.isVisible) {
                arrowPendingOrder.setImageResource(R.drawable.up_icon)
                contentOrdersLayout.visibility = View.VISIBLE

            } else {
                arrowPendingOrder.setImageResource(R.drawable.down_icon)
                contentOrdersLayout.visibility = View.GONE
            }
        }

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        addProduct.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_add_product, null, navBuilder.build())
        }

        return view
    }

    private fun fetchProductsList(list: MutableList<DocumentSnapshot>?) {
        productsItemsArray = arrayListOf()
        if (list!!.size == 0) {
            mainLayout.visibility = View.VISIBLE
            productsLayout.visibility = View.GONE
            loader.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        } else {
            for (i in list) {
                if (i.exists()) {
                    val product= SellerProductsItems(
                        i.getString("Product Name"),
                        i.getString("Brand Name"),
                        i.getString("Product Image"),
                        i.getString("Category"),
                        i.getString("Product Price"),
                        i.getString("Quantity"),
                        i.getString("Description"),
                        i.getString("Tags"),
                        i.getString("Ratings"),
                        i.getString("Raters"),
                        i.getString("Seller Name"),
                        i.getString("Seller Image"),
                        i.getString("Seller UID"),
                        i.getString("Product ID")
                    )
                    productsItemsArray.add(product)
                }
            }
            productsAdapter.updateSellerProducts(productsItemsArray)
            productsRecyclerView.visibility = View.VISIBLE
            mainLayout.visibility = View.VISIBLE
            loader.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun fetchOrdersList(list: MutableList<DocumentSnapshot>?) {
        ordersItemsArray = arrayListOf()
        if (list!!.size == 0) {
            noOrders.visibility = View.VISIBLE
            ordersRecyclerView.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists()) {
                    val order = SellerReceivedOrdersItems(
                        i.getString("Buyer Name"),
                        i.getString("Buyer UID"),
                        i.getString("Seller UID"),
                        i.getString("Buyer Address"),
                        i.getString("Order Time"),
                        i.getString("Delivery Date"),
                        i.getString("Status"),
                        i.getString("Product Name"),
                        i.getString("Brand Name"),
                        i.getString("Product Image Url"),
                        i.getString("Category"),
                        i.getString("Payable Amount"),
                        i.getString("Quantity"),
                        i.getString("Order ID")
                    )
                    ordersItemsArray.add(order)
                }
            }
            ordersAdapter.updateSellerReceivedOrders(ordersItemsArray)
            ordersRecyclerView.visibility = View.VISIBLE
            noOrders.visibility = View.GONE
        }
    }

    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                sellerUid = it.uid
                dbViewModel.fetchSellerProducts(it.uid)
                dbViewModel.sellerProductsData.observe(viewLifecycleOwner) { list1 ->
                    fetchProductsList(list1)
                }
                dbViewModel.fetchReceivedOrders(it.uid)
                dbViewModel.sellerReceivedOrdersData.observe(viewLifecycleOwner) { list2 ->
                    fetchOrdersList(list2)
                }
            }
        }
    }

}