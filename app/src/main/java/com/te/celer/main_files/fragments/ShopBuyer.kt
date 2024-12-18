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
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.R
import com.te.celer.db.AESCrypt
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.db.LocalStorage
import com.te.celer.main_files.adapters.ConversationAdapter
import com.te.celer.main_files.models.ConversationItems


class ShopBuyer : Fragment() {

    private lateinit var orders: CardView
    private lateinit var wishlist: CardView
    private lateinit var cart: CardView
    private lateinit var groceries: LinearLayout
    private lateinit var fashion: LinearLayout
    private lateinit var electronics: LinearLayout
    private lateinit var appliances: LinearLayout
    private lateinit var sports: LinearLayout
    private lateinit var furniture: LinearLayout
    private lateinit var books: LinearLayout
    private lateinit var personalcare: LinearLayout
    private lateinit var medicines: LinearLayout
    private lateinit var conversationRecyclerView: RecyclerView
    private lateinit var conversationBox: CardView
    private lateinit var backBtn: ImageView
    private lateinit var conversationAdapter: ConversationAdapter
    private var conversationItemsArray = arrayListOf<ConversationItems>()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var uid: String
    val aesCrypt = AESCrypt()
    val key = ByteArray(32)
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId", "ObsoleteSdkInt")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_buyer, container, false)

        orders = view.findViewById(R.id.orders_buyer_shop)
        cart = view.findViewById(R.id.cart_buyer_shop)
        wishlist = view.findViewById(R.id.wishlist_buyer_shop)
        groceries = view.findViewById(R.id.groceries)
        fashion = view.findViewById(R.id.fashion)
        electronics = view.findViewById(R.id.electronics)
        appliances = view.findViewById(R.id.appliances)
        sports = view.findViewById(R.id.sports)
        furniture = view.findViewById(R.id.furniture)
        books = view.findViewById(R.id.books)
        personalcare = view.findViewById(R.id.personal_care)
        medicines = view.findViewById(R.id.medicines)
        conversationBox = view.findViewById(R.id.chat_box_buyer_shop)
        conversationRecyclerView = view.findViewById(R.id.chat_recyclerview_buyer_shop)
        backBtn = view.findViewById(R.id.back_btn_buyer_shop)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        requireActivity().window.statusBarColor = Color.parseColor("#F7F9FD")

        key.fill(1)
        conversationAdapter =
            ConversationAdapter(requireContext(), conversationItemsArray, key)
        conversationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        conversationRecyclerView.setHasFixedSize(true)
        conversationRecyclerView.setItemViewCacheSize(20)
        conversationRecyclerView.adapter = conversationAdapter

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

//        addProduct.setOnClickListener {
//            Navigation.findNavController(view)
//                .navigate(R.id.nav_add_product, null, navBuilder.build())
//        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        orders.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_orders, null, navBuilder.build())
        }

        cart.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_cart, null, navBuilder.build())
        }

        wishlist.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_wishlist, null, navBuilder.build())
        }

        groceries.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Groceries")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        fashion.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Fashion")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        electronics.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Electronics")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        appliances.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Appliances")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        sports.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Sports")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        furniture.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Furniture")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        books.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Books")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        personalcare.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Personal Care")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        medicines.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Category", "Medicines")
            Navigation.findNavController(view).navigate(R.id.nav_products, bundle)
        }

        loadData()

        return view
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>) {
        if (list.isNotEmpty()) {
            conversationBox.visibility = View.VISIBLE
            conversationItemsArray = arrayListOf()

            for(i in 0..2)  {
                val conversationData = ConversationItems(
                    list[i].getString("name"),
                    list[i].getString("image_url"),
                    list[i].getString("last_message"),
                    uid,
                    list[i].getString("uid")
                )
                conversationItemsArray.add(conversationData)
            }
            conversationAdapter.updateConversations(conversationItemsArray)
        }
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!

        dbViewModel.getConversations(uid)
        dbViewModel.conversations.observe(viewLifecycleOwner) { list ->
            fetchData(list)
        }
    }

}