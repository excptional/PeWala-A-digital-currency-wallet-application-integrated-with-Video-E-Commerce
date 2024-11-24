package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.te.celer.db.AESCrypt
import com.te.celer.db.LocalStorage
import com.te.celer.main_files.adapters.ConversationAdapter
import com.te.celer.main_files.adapters.SellerProductsAdapter
import com.te.celer.main_files.adapters.SellerReceivedOrdersAdapter
import com.te.celer.main_files.models.ConversationItems
import com.te.celer.main_files.models.SellerProductsItems
import com.te.celer.main_files.models.SellerReceivedOrdersItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Home : Fragment() {

    private val localStorage = LocalStorage()
//    private lateinit var imageUrl: ArrayList<String>
//    private lateinit var sliderView: SliderView
//    private lateinit var sliderAdapter: SliderAdapter
//    private lateinit var send: LinearLayout
//    private lateinit var add: LinearLayout
//    private lateinit var receive: LinearLayout
//    private lateinit var redeem: LinearLayout
//    private lateinit var viewWallet: ImageButton
//    private lateinit var peopleAdapter: PeopleAdapter
//    private var peopleItemsArray = arrayListOf<PeopleItems>()
//    private lateinit var recyclerview: RecyclerView
//    private lateinit var peopleText: TextView
//    private lateinit var peopleLayout: CardView
//    private lateinit var shop: CardView
    private lateinit var userType: String
//    private lateinit var userStatus: String
    private lateinit var mainLayout: ScrollView
    private lateinit var loader: LottieAnimationView

    private lateinit var addProduct: LinearLayout
    private lateinit var ordersAdapter: SellerReceivedOrdersAdapter
    private var ordersItemsArray = arrayListOf<SellerReceivedOrdersItems>()
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var productsAdapter: SellerProductsAdapter
    private var productsItemsArray = arrayListOf<SellerProductsItems>()
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productsLayout: LinearLayout
    private lateinit var ordersBox: LinearLayout
    private lateinit var shopSeller: LinearLayout

    private lateinit var orders: RelativeLayout
    private lateinit var wishlist: RelativeLayout
    private lateinit var cart: RelativeLayout
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
    private lateinit var conversationBox: LinearLayout
    private lateinit var conversationAdapter: ConversationAdapter
    private var conversationItemsArray = arrayListOf<ConversationItems>()
    private lateinit var shopBuyer: LinearLayout
    private lateinit var viewMoreChats: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var uid: String
    private lateinit var viewAllReceivedOrders: LinearLayout
    private lateinit var viewAllProducts: RelativeLayout
    val aesCrypt = AESCrypt()
    val key = ByteArray(32)
    private var userdata: Map<String, String>? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        requireActivity().window.statusBarColor = Color.parseColor("#F7F9FD")

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

//        viewWallet = view.findViewById(R.id.view_wallet)
//        send = view.findViewById(R.id.send_money)
//        add = view.findViewById(R.id.add_money)
//        receive = view.findViewById(R.id.receive_money)
//        redeem = view.findViewById(R.id.redeem_money)
//        recyclerview = view.findViewById(R.id.people_recyclerview_home)
//        peopleLayout = view.findViewById(R.id.peopleLayout_home)
//        peopleText = view.findViewById(R.id.people_text_home)
        mainLayout = view.findViewById(R.id.mainLayout_home)
        loader = view.findViewById(R.id.progressbar_home)

        productsRecyclerView = view.findViewById(R.id.seller_products_recyclerView_seller_home)
        ordersRecyclerView = view.findViewById(R.id.pending_orders_recyclerView_seller_home)
        productsLayout = view.findViewById(R.id.products_layout_seller_home)
        ordersBox = view.findViewById(R.id.orders_box_home)
        addProduct = view.findViewById(R.id.addProduct_seller_home)
        shopSeller = view.findViewById(R.id.shop_layout_seller_home)
        viewAllReceivedOrders = view.findViewById(R.id.view_all_received_orders_home)
        viewAllProducts = view.findViewById(R.id.view_all_products_seller_products)

        orders = view.findViewById(R.id.orders_buyer_home)
        cart = view.findViewById(R.id.cart_buyer_home)
        wishlist = view.findViewById(R.id.wishlist_buyer_home)
        groceries = view.findViewById(R.id.groceries)
        fashion = view.findViewById(R.id.fashion)
        electronics = view.findViewById(R.id.electronics)
        appliances = view.findViewById(R.id.appliances)
        sports = view.findViewById(R.id.sports)
        furniture = view.findViewById(R.id.furniture)
        books = view.findViewById(R.id.books)
        personalcare = view.findViewById(R.id.personal_care)
        medicines = view.findViewById(R.id.medicines)
        conversationBox = view.findViewById(R.id.chat_box_home)
        conversationRecyclerView = view.findViewById(R.id.chat_recyclerview_home)
        shopBuyer = view.findViewById(R.id.shop_layout_buyer_home)
        viewMoreChats = view.findViewById(R.id.view_more_chats_home)

        loadData()

//        checkLocalStorageForData()

//        peopleAdapter = PeopleAdapter(requireContext(), peopleItemsArray)
//        recyclerview.layoutManager = GridLayoutManager(view.context, 3)
//        recyclerview.setItemViewCacheSize(20)
//        recyclerview.adapter = peopleAdapter

        productsAdapter = SellerProductsAdapter(productsItemsArray)
        productsRecyclerView.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        productsRecyclerView.setItemViewCacheSize(20)
        productsRecyclerView.adapter = productsAdapter

        ordersAdapter = SellerReceivedOrdersAdapter(ordersItemsArray)
        ordersRecyclerView.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        ordersRecyclerView.setItemViewCacheSize(20)
        ordersRecyclerView.adapter = ordersAdapter

        key.fill(1)
        conversationAdapter =
            ConversationAdapter(requireContext(), conversationItemsArray, key)
        conversationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        conversationRecyclerView.setHasFixedSize(true)
        conversationRecyclerView.setItemViewCacheSize(20)
        conversationRecyclerView.adapter = conversationAdapter

//        sliderView = view.findViewById(R.id.slider)

//        imageUrl = ArrayList()
//        imageUrl =
//            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/13416072_5243336.png?alt=media&token=5190cbdc-9b47-4b21-b443-c6905da38d96") as ArrayList<String>
//        imageUrl =
//            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/20824349_6342757.png?alt=media&token=94e820f8-2cfa-4848-b543-8b1c512e1738") as ArrayList<String>
//        imageUrl =
//            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/9457133_4137379.png?alt=media&token=e14c5339-d2a9-47c8-a04b-4c7222c01ffa") as ArrayList<String>
//        imageUrl =
//            (imageUrl + "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/20827766_Hand%20holding%20phone%20with%20digital%20wallet%20service%20and%20sending%20money.png?alt=media&token=bb05263a-4cf6-4848-b946-e59db5983ea8") as ArrayList<String>
//
//        sliderAdapter = SliderAdapter(imageUrl)
//        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
//        sliderView.setSliderAdapter(sliderAdapter)
//        sliderView.scrollTimeInSec = 3
//        sliderView.isAutoCycle = true
//        sliderView.startAutoCycle()

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

//        shop.setOnClickListener {
////            Toast.makeText(requireContext(), "This feature is not implemented yet", Toast.LENGTH_SHORT).show()
//            val bundle = Bundle()
//            bundle.putString("userType", userType)
//            requireFragmentManager().popBackStack()
//            if (userType == "Seller") {
//                when (userStatus) {
//                    "Not Verified" -> Navigation.findNavController(view)
//                        .navigate(R.id.nav_seller_doc, null, navBuilder.build())
//
//                    "Checking" ->
//                        Navigation.findNavController(view)
//                            .navigate(R.id.nav_waiting, null, navBuilder.build())
//
//                    else -> Navigation.findNavController(view)
//                        .navigate(R.id.nav_shop_seller, bundle, navBuilder.build())
//                }
//            } else Navigation.findNavController(view)
//                .navigate(R.id.nav_shop_buyer, bundle, navBuilder.build())
////            Navigation.findNavController(view)
////                .navigate(R.id.nav_address)
//        }

//        add.setOnClickListener {
//            requireFragmentManager().popBackStack()
//            Navigation.findNavController(view).navigate(R.id.nav_add, null, navBuilder.build())
//        }
//
//        send.setOnClickListener {
//            requireFragmentManager().popBackStack()
//            Navigation.findNavController(view).navigate(R.id.nav_send, null, navBuilder.build())
//        }
//
//        receive.setOnClickListener {
//            requireFragmentManager().popBackStack()
//            Navigation.findNavController(view).navigate(R.id.nav_receive, null, navBuilder.build())
//        }
//
//        redeem.setOnClickListener {
//            requireFragmentManager().popBackStack()
//            Navigation.findNavController(view).navigate(R.id.nav_redeem, null, navBuilder.build())
//        }
//
//        viewWallet.setOnClickListener {
//            requireFragmentManager().popBackStack()
//            Navigation.findNavController(view).navigate(R.id.nav_wallet, null, navBuilder.build())
//        }

        orders.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_orders, null, navBuilder.build())
        }

        cart.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_cart, null, navBuilder.build())
        }

        wishlist.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_wishlist, null, navBuilder.build())
        }

        val bundle = Bundle()

        groceries.setOnClickListener {
            bundle.putString("Category", "Groceries")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        fashion.setOnClickListener {
            bundle.putString("Category", "Fashion")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        electronics.setOnClickListener {
            bundle.putString("Category", "Electronics")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        appliances.setOnClickListener {
            bundle.putString("Category", "Appliances")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        sports.setOnClickListener {
            bundle.putString("Category", "Sports")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        furniture.setOnClickListener {
            bundle.putString("Category", "Furniture")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        books.setOnClickListener {
            bundle.putString("Category", "Books")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        personalcare.setOnClickListener {
            bundle.putString("Category", "Personal Care")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        medicines.setOnClickListener {
            bundle.putString("Category", "Medicines")
            Navigation.findNavController(view)
                .navigate(R.id.nav_products, bundle, navBuilder.build())
        }

        viewMoreChats.setOnClickListener {
            bundle.putString("uid", uid)
            Navigation.findNavController(view)
                .navigate(R.id.nav_chat_list, bundle, navBuilder.build())
        }

        viewAllReceivedOrders.setOnClickListener {
            bundle.putString("uid", uid)
            Navigation.findNavController(view)
                .navigate(R.id.nav_received_orders, bundle, navBuilder.build())
        }

        viewAllProducts.setOnClickListener {
            bundle.putString("uid", uid)
            Navigation.findNavController(view)
                .navigate(R.id.nav_seller_products, bundle, navBuilder.build())
        }

        addProduct.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.nav_add_product, null, navBuilder.build())
        }

        return view
    }

//    private fun fetchPeopleData(list: ArrayList<DocumentSnapshot>) {
//        peopleText.visibility = View.GONE
//        peopleLayout.visibility = View.GONE
//        peopleItemsArray = arrayListOf()
//        for (i in list) {
//            if (i.exists()) {
//                val peopleData = PeopleItems(
//                    i.getString("name"),
//                    i.getString("phone"),
//                    i.getString("image_url"),
//                    i.getString("uid")
//                )
//                peopleItemsArray.add(peopleData)
//                peopleText.visibility = View.VISIBLE
//                peopleLayout.visibility = View.VISIBLE
//            }
//        }
//        peopleAdapter.updatePeople(peopleItemsArray)
//    }

    private fun fetchChatData(list: MutableList<DocumentSnapshot>) {
        if (list.isNotEmpty()) {
            conversationBox.visibility = View.VISIBLE
            conversationItemsArray = arrayListOf()

            for (i in 0..2) {
                if (i < list.size) {
                    val conversationData = ConversationItems(
                        list[i].getString("name"),
                        list[i].getString("image_url"),
                        list[i].getString("last_message"),
                        uid,
                        list[i].getString("uid")
                    )
                    conversationItemsArray.add(conversationData)
                }
            }
            conversationAdapter.updateConversations(conversationItemsArray)
        }
    }

    private fun fetchProductsList(list: MutableList<DocumentSnapshot>?) {
        productsItemsArray = arrayListOf()
        if (list!!.size == 0) {
            mainLayout.visibility = View.VISIBLE
            productsLayout.visibility = View.GONE
            loader.visibility = View.GONE
        } else {
            for (i in 0..1) {
                if (i < list.size) {
                    val product = SellerProductsItems(
                        list[i].getString("product_name"),
                        list[i].getString("brand_name"),
                        list[i].getString("product_image_url"),
                        list[i].getString("category"),
                        list[i].getString("product_price"),
                        list[i].getString("stocks"),
                        list[i].getString("description"),
                        list[i].getString("tags"),
                        list[i].getString("ratings"),
                        list[i].getString("raters"),
                        list[i].getString("seller_name"),
                        list[i].getString("seller_image"),
                        list[i].getString("seller_uid"),
                        list[i].getString("product_id")
                    )
                    productsItemsArray.add(product)
                }
            }
            productsAdapter.updateSellerProducts(productsItemsArray)
            productsRecyclerView.visibility = View.VISIBLE
            productsLayout.visibility = View.VISIBLE
            loader.visibility = View.GONE
        }
    }

    private fun fetchOrdersList(list: MutableList<DocumentSnapshot>?) {
        ordersItemsArray = arrayListOf()
        if (list.isNullOrEmpty()) {
            ordersBox.visibility = View.GONE
        } else {
            val order = SellerReceivedOrdersItems(
                list[0].getString("buyer_name"),
                list[0].getString("buyer_uid"),
                list[0].getString("seller_uid"),
                list[0].getString("buyer_address"),
                list[0].getString("order_time"),
                list[0].getString("delivery_date"),
                list[0].getString("status"),
                list[0].getString("product_name"),
                list[0].getString("brand_name"),
                list[0].getString("product_image_url"),
                list[0].getString("category"),
                list[0].getString("payable_amount"),
                list[0].getString("quantity"),
                list[0].getString("order_id"),
                list[0].getString("confirmation_code")
            )
            ordersItemsArray.add(order)
            ordersAdapter.updateSellerReceivedOrders(ordersItemsArray)
            ordersBox.visibility = View.VISIBLE
        }
    }

    private fun loadData() {
        userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!
        dbViewModel.updateTransactorDetails(uid)
        dbViewModel.fetchContacts(uid)
//        dbViewModel.contactDetails.observe(viewLifecycleOwner) {
//            if (it.isNotEmpty()) {
//                fetchPeopleData(it)
//            } else {
//                peopleText.visibility = View.GONE
//                peopleLayout.visibility = View.GONE
//            }
//        }
        userType = userdata!!["user_type"]!!
//        userStatus = userdata["status"]!!
        mainLayout.visibility = View.VISIBLE
        loader.visibility = View.GONE
        if (userType == "Buyer") {
            shopBuyer.visibility = View.VISIBLE
        } else {
            shopSeller.visibility = View.VISIBLE
            dbViewModel.fetchSellerProducts(uid)
            dbViewModel.sellerProductsData.observe(viewLifecycleOwner) { list1 ->
                fetchProductsList(list1)
            }
            dbViewModel.fetchReceivedOrders(uid)
            dbViewModel.sellerReceivedOrdersData.observe(viewLifecycleOwner) { list2 ->
                fetchOrdersList(list2)
            }
        }
        dbViewModel.getConversations(uid)
        dbViewModel.conversations.observe(viewLifecycleOwner) { list ->
            fetchChatData(list)
        }
    }

//    private fun checkLocalStorageForData() {
//        lifecycleScope.launch {
//            while (true) {
//                if(requireContext() != null)
//                    userdata = localStorage.getData(requireContext(), "user_data")
//
//                if (userdata != null) {
//                    loadData()
//                    break
//                } else {
//                    delay(1000)
//                }
//            }
//        }
//    }

}