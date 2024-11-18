package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.SellerProductsAdapter
import com.te.celer.main_files.adapters.SellerReceivedOrdersAdapter
import com.te.celer.main_files.models.SellerProductsItems
import com.te.celer.main_files.models.SellerReceivedOrdersItems
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.AESCrypt
import com.te.celer.db.LocalStorage
import com.te.celer.main_files.adapters.ConversationAdapter
import com.te.celer.main_files.models.ConversationItems

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
    private lateinit var conversationRecyclerView: RecyclerView
    private lateinit var conversationBox: CardView
    private lateinit var backBtn: ImageView
    private lateinit var conversationAdapter: ConversationAdapter
    private var conversationItemsArray = arrayListOf<ConversationItems>()
    val aesCrypt = AESCrypt()
    val key = ByteArray(32)
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_seller, container, false)

        requireActivity().window.statusBarColor = Color.parseColor("#F7F9FD")

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
        conversationBox = view.findViewById(R.id.chat_box_seller_shop)
        conversationRecyclerView = view.findViewById(R.id.chat_recyclerview_seller_shop)
        backBtn = view.findViewById(R.id.back_btn_seller_shop)

        key.fill(1)

        productsAdapter = SellerProductsAdapter(productsItemsArray)
        productsRecyclerView.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
//        productsRecyclerView.setHasFixedSize(true)
        productsRecyclerView.setItemViewCacheSize(20)
        productsRecyclerView.adapter = productsAdapter

        ordersAdapter = SellerReceivedOrdersAdapter(ordersItemsArray)
        ordersRecyclerView.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
//        ordersRecyclerView.setHasFixedSize(true)
        ordersRecyclerView.setItemViewCacheSize(20)
        ordersRecyclerView.adapter = ordersAdapter

        conversationAdapter =
            ConversationAdapter(requireContext(), conversationItemsArray, key)
        conversationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        conversationRecyclerView.setHasFixedSize(true)
        conversationRecyclerView.setItemViewCacheSize(20)
        conversationRecyclerView.adapter = conversationAdapter

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

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
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
            productsAdapter.updateSellerProducts(productsItemsArray)
            productsRecyclerView.visibility = View.VISIBLE
            mainLayout.visibility = View.VISIBLE
            loader.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun fetchOrdersList(list: MutableList<DocumentSnapshot>?) {
        ordersItemsArray = arrayListOf()
        if (list.isNullOrEmpty()) {
            noOrders.visibility = View.VISIBLE
            ordersRecyclerView.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists()) {
                    val order = SellerReceivedOrdersItems(
                        i.getString("buyer_name"),
                        i.getString("buyer_uid"),
                        i.getString("seller_uid"),
                        i.getString("buyer_address"),
                        i.getString("order_time"),
                        i.getString("delivery_date"),
                        i.getString("status"),
                        i.getString("product_name"),
                        i.getString("brand_name"),
                        i.getString("product_image_url"),
                        i.getString("category"),
                        i.getString("payable_amount"),
                        i.getString("quantity"),
                        i.getString("order_id")
                    )
                    ordersItemsArray.add(order)
                }
            }
            ordersAdapter.updateSellerReceivedOrders(ordersItemsArray)
            ordersRecyclerView.visibility = View.VISIBLE
            noOrders.visibility = View.GONE
        }
    }

    private fun fetchConversations(list: MutableList<DocumentSnapshot>) {
        if (list.isNotEmpty()) {
            conversationBox.visibility = View.VISIBLE
            conversationItemsArray = arrayListOf()
            val conversationData = ConversationItems(
                list[0].getString("name"),
                list[0].getString("image_url"),
                list[0].getString("last_message"),
                sellerUid,
                list[0].getString("uid")
            )
            conversationItemsArray.add(conversationData)

            if (list.size > 1) {
                val conversationData = ConversationItems(
                    list[1].getString("name"),
                    list[1].getString("image_url"),
                    list[1].getString("last_message"),
                    sellerUid,
                    list[1].getString("uid")
                )
                conversationItemsArray.add(conversationData)
            }
            conversationAdapter.updateConversations(conversationItemsArray)
        }
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        sellerUid = userdata!!["uid"]!!
        dbViewModel.fetchSellerProducts(sellerUid)
        dbViewModel.sellerProductsData.observe(viewLifecycleOwner) { list1 ->
            fetchProductsList(list1)
        }
        dbViewModel.fetchReceivedOrders(sellerUid)
        dbViewModel.sellerReceivedOrdersData.observe(viewLifecycleOwner) { list2 ->
            fetchOrdersList(list2)
        }
        dbViewModel.getConversations(sellerUid)
        dbViewModel.conversations.observe(viewLifecycleOwner) { list3 ->
            fetchConversations(list3)
        }
    }

}