package com.te.celer.main_files.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.R
import com.te.celer.db.DBViewModel
import com.te.celer.main_files.adapters.CartAdapter
import com.te.celer.main_files.models.CartItems
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.LocalStorage

class Cart : Fragment() {

    private val localStorage = LocalStorage()
    private lateinit var cartAdapter: CartAdapter
    private var cartItemsArray = arrayListOf<CartItems>()
    private lateinit var selectedCartItems: ArrayList<DocumentSnapshot>
    private val dbViewModel: DBViewModel by viewModels()
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var shimmerContainerProduct: ShimmerFrameLayout
    private lateinit var notFoundText: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mainLayout: RelativeLayout
    private lateinit var backBtn: ImageView
    private lateinit var uid: String
    private lateinit var placeOrder: CardView
    private lateinit var btnLayout: LinearLayout
    private lateinit var loader: LottieAnimationView
    private lateinit var whiteView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_cart)
        cartRecyclerView = view.findViewById(R.id.recyclerView_cart)
        notFoundText = view.findViewById(R.id.notFound_cart)
        mainLayout = view.findViewById(R.id.main_layout_cart)
        backBtn = view.findViewById(R.id.back_btn_cart)
        placeOrder = view.findViewById(R.id.place_order_cart)
        btnLayout = view.findViewById(R.id.btn_layout_cart)
        loader = view.findViewById(R.id.loader_cart)
        whiteView = view.findViewById(R.id.whiteView_cart)

        shimmerContainerProduct = view.findViewById(R.id.shimmer_view_cart)
        shimmerContainerProduct.startShimmer()
        shimmerContainerProduct.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        cartAdapter = CartAdapter(requireContext(), this, cartItemsArray)
        cartRecyclerView.layoutManager = LinearLayoutManager(view.context)
//        cartRecyclerView.setHasFixedSize(true)
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

        placeOrder.setOnClickListener {
            placeOrder.isClickable = false
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            cartItemsArray.let {
                if(!isSelectedAny(selectedCartItems)) {
                    Toast.makeText(requireContext(), "Select items to proceed", Toast.LENGTH_SHORT).show()
                } else {
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("cartItems", cartItemsArray)
                    Navigation.findNavController(view).navigate(R.id.nav_final_order_place, bundle)
                }
                whiteView.visibility = View.GONE
                loader.visibility = View.GONE
                placeOrder.isClickable = true
            }
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
            btnLayout.visibility = View.GONE
        } else {
            for (i in list) {
                if (i.exists()) {
                    val acc = CartItems(
                        i.getString("product_name"),
                        i.getString("brand_name"),
                        i.getString("product_image_url"),
                        i.getString("category"),
                        i.getString("product_price"),
                        i.getString("quantity"),
                        i.getString("description"),
                        i.getString("seller_name"),
                        i.getString("seller_image_url"),
                        i.getString("seller_uid"),
                        uid,
                        i.getString("ratings"),
                        i.getString("product_id"),
                        i.get("selected") as Boolean
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
            btnLayout.visibility = View.VISIBLE
        }
    }

    private fun isSelectedAny(list: MutableList<DocumentSnapshot>?): Boolean {
        for (item in list!!) {
            if(item.get("selected") as Boolean) {
                return true
            }
        }
        return false
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!
        dbViewModel.fetchCartItems(uid)
        dbViewModel.selectedCartData.observe(viewLifecycleOwner) { list ->
            selectedCartItems = list
        }
        dbViewModel.cartData.observe(viewLifecycleOwner) {
            fetchCartList(it)
            swipeRefreshLayout.isRefreshing = false
        }
        dbViewModel.getSelectedCartItems(uid)
    }

}