package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.LocalStorage
import com.te.pewala.db.Response
import com.te.pewala.main_files.MainActivity
import com.te.pewala.main_files.adapters.OrderSummaryAdapter
import com.te.pewala.main_files.models.CartItems
import kotlin.properties.Delegates

class FinalOrderPlace : Fragment() {

    private val localStorage = LocalStorage()
    private lateinit var closeBtn: ImageButton
    private lateinit var placeOrder: CardView
    private lateinit var updatedProductPrice: TextView
    private lateinit var totalPrice: TextView
    private lateinit var finalAmount: TextView
    private lateinit var editAddress: LinearLayout
    private lateinit var addressBox: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private lateinit var senderUid: String
    private lateinit var senderName: String
    private lateinit var senderImageUrl: String
    private lateinit var senderPhone: String
//    private val receiverImageUrl = "https://firebasestorage.googleapis.com/v0/b/pewala-a26d9.appspot.com/o/profile_images%2Ffv0ooafai0UGRWIK3EOesgQcUtq2%2Fcropped4716528955880745731.jpg?alt=media&token=63990896-4f8a-471e-a485-ea1e5b4361cc"
//    private val receiverName: String = "PeWala"
//    private val receiverPhone: String = "8621028795"
//    private val receiverUid = "fv0ooafai0UGRWIK3EOesgQcUtq2"
    private var addressStr: String? = null
    private lateinit var locality: TextView
    private lateinit var cityPostal: TextView
    private lateinit var state: TextView
    private lateinit var balanceStr: String
    private var finalAmountInt = 0

    //    private lateinit var googleMap: GoogleMap
    private var lat by Delegates.notNull<Double>()
    private var long by Delegates.notNull<Double>()
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderSummaryAdapter: OrderSummaryAdapter
    private var orderSummaryItemsArray = arrayListOf<CartItems>()
    private lateinit var mainLayout: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n", "MissingInflatedId", "CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_final_order_place, container, false)

        requireActivity().window.statusBarColor = Color.parseColor("#F7F9FD")

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        closeBtn = view.findViewById(R.id.close_btn_order_summary)
        updatedProductPrice = view.findViewById(R.id.updatedProductPrice_order_summary)
        totalPrice = view.findViewById(R.id.totalPrice_order_summary)
        finalAmount = view.findViewById(R.id.final_amount_order_summary)
        placeOrder = view.findViewById(R.id.placeOrder_btn_order_summary)
        addressBox = view.findViewById(R.id.address_box_order_summary)
        editAddress = view.findViewById(R.id.edit_address_order_summary)
        whiteView = view.findViewById(R.id.whiteView_order_summary)
        loader = view.findViewById(R.id.loader_order_summary)
        locality = view.findViewById(R.id.area_order_summary)
        cityPostal = view.findViewById(R.id.city_postal_order_summary)
        state = view.findViewById(R.id.state_order_summary)
        orderRecyclerView = view.findViewById(R.id.recycler_view_order_summary)
        mainLayout = view.findViewById(R.id.main_layout_final_order_place)

        mainLayout.visibility = View.GONE
        loader.visibility = View.VISIBLE
//        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()


        orderSummaryAdapter = OrderSummaryAdapter(orderSummaryItemsArray)
        orderRecyclerView.layoutManager = LinearLayoutManager(view.context)
//        orderRecyclerView.setHasFixedSize(true)
        orderRecyclerView.setItemViewCacheSize(20)
        orderRecyclerView.adapter = orderSummaryAdapter

        addressBox.visibility = View.GONE
//        whiteView.visibility = View.VISIBLE
//        loader.visibility = View.VISIBLE

        load()
//        setPayment()

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        editAddress.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("lat", lat.toString())
            bundle.putString("long", long.toString())
            Navigation.findNavController(view).navigate(R.id.nav_address, bundle)
        }

        placeOrder.setOnClickListener {
            if (addressStr.isNullOrEmpty()) {
                showDialogAddress()
            } else {
                showPaymentOptions()
//            order()
            }
        }
        return view
    }

//    @SuppressLint("ClickableViewAccessibility")
//    fun disableMapScrolling(mapView: MapView) {
//        mapView.setOnTouchListener { _, _ -> true }
//        mapView.isScrollContainer = false
//        mapView.requestDisallowInterceptTouchEvent(true)
//    }

//    override fun onMapReady(map: GoogleMap) {
//        map.let {
//            googleMap = it
//        }
//        val location = LatLng(lat, long)
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(location)
//                .title("Your location")
//        )
//        val pos = LatLng(lat, long)
////        googleMap.setMapStyle(
////            MapStyleOptions.loadRawResourceStyle(
////                requireContext(), R.raw.map_style_dark))
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f))
//    }

    private fun fetchProducts(list: MutableList<DocumentSnapshot>?) {
        if(list != null) {
            orderSummaryItemsArray = arrayListOf()
            for (i in list) {
                if (i.exists() && i.get("selected") == true) {
                    val data = CartItems(
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
                        senderUid,
                        i.getString("ratings"),
                        i.getString("product_id"),
                        i.get("selected") as Boolean
                    )
                    orderSummaryItemsArray.add(data)
                }
            }
        }

        orderSummaryAdapter.updateOrderSummary(orderSummaryItemsArray)
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val total = orderSummaryItemsArray.sumOf { item ->
            val price = item.price?.toIntOrNull() ?: 0
            val quantity = item.quantity?.toIntOrNull() ?: 0
            price * quantity
        }
        finalAmountInt = total + 30
        finalAmount.text = "$finalAmountInt INR"
        updatedProductPrice.text = "Product price: ₹$total INR"
        totalPrice.text = "Total: ₹${finalAmountInt} INR"
    }

    @SuppressLint("SetTextI18n")
    private fun load() {
        val userdata = localStorage.getData(requireContext(),"user_data")
        senderUid = userdata!!["uid"]!!
        dbViewModel.getAddress(senderUid)
        orderSummaryItemsArray =
            requireArguments().getParcelableArrayList<CartItems>("cartItems") as ArrayList<CartItems>
        if (orderSummaryItemsArray.isNotEmpty()) {
            fetchProducts(null)
        } else {
            dbViewModel.fetchCartItems(senderUid)
            dbViewModel.cartData.observe(viewLifecycleOwner) { list ->
                fetchProducts(list)
            }
        }
        dbViewModel.dbResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    dbViewModel.addressData.observe(viewLifecycleOwner) { doc ->
                        if (doc.exists()) {
                            lat = doc.getString("latitude")!!.toDouble()
                            long = doc.getString("longitude")!!.toDouble()
                            locality.text = doc.getString("locality")
                            cityPostal.text =
                                doc.getString("city") + ", " + doc.getString("postal_code")
                            state.text = doc.getString("state")
                            addressStr =
                                "${doc.getString("locality")}, ${doc.getString("street")}, " +
                                        "${doc.getString("city")}, ${doc.getString("postal_code")}, " +
                                        "${doc.getString("state")}"
                            whiteView.visibility = View.GONE
                            loader.visibility = View.GONE
                            addressBox.visibility = View.VISIBLE
                            mainLayout.visibility = View.VISIBLE
                        }
                    }
                }

                is Response.Failure -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                }
            }
        }
        dbViewModel.fetchAccountDetails(senderUid)
        dbViewModel.accDetails.observe(viewLifecycleOwner) {
            balanceStr = it.getString("balance").toString()
            senderName = it.getString("name").toString()
            senderImageUrl = it.getString("image_url").toString()
            senderPhone = it.getString("phone").toString()
        }
    }

    private fun showDialogAddress() {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_save_address)
        dialog.window?.attributes?.windowAnimations = R.anim.pop

        val goBtn: CardView = dialog.findViewById(R.id.go_btn_dialog_address)
        val cancelBtn: CardView = dialog.findViewById(R.id.cancel_btn_dialog_address)

        goBtn.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.nav_address)
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun showPaymentOptions() {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setContentView(R.layout.dialog_payment_options)
        dialog.window?.attributes?.windowAnimations = R.anim.slide_up
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val avlBalance: TextView = dialog.findViewById(R.id.avl_balance_dialog)
        val warning: TextView = dialog.findViewById(R.id.warning_dialog)
        val digitalRupeePay: CardView = dialog.findViewById(R.id.pay_digital_rupees_card_dialog)
        val codPay: CardView = dialog.findViewById(R.id.pay_cod_card_dialog)
        val codText: TextView = dialog.findViewById(R.id.cod_text_dialog)
        val proceedBtn: Button = dialog.findViewById(R.id.proceed_btn_dialog)

        avlBalance.text = "Avl. Balance : $balanceStr"

        if (Integer.parseInt(balanceStr) < finalAmountInt) warning.visibility = View.VISIBLE

        var str = ""

        digitalRupeePay.setOnClickListener {
            digitalRupeePay.setCardBackgroundColor(Color.parseColor("#2196F3"))
            codPay.setCardBackgroundColor(Color.parseColor("#dddddd"))
            codText.setTextColor(Color.BLACK)
            str = "Digital Pay"
        }

        codPay.setOnClickListener {
            digitalRupeePay.setCardBackgroundColor(Color.BLACK)
            codPay.setCardBackgroundColor(Color.parseColor("#2196F3"))
            codText.setTextColor(Color.WHITE)
            str = "COD"
        }

        proceedBtn.setOnClickListener {
            when (str) {
                "Digital Pay" -> {
                    val bundle = Bundle()

//                    bundle.putSerializable("orderItems", orderSummaryItemsArray)
//                    bundle.putLong("finalAmount", finalAmountInt.toLong())
//
//                    Navigation.findNavController(requireView()).navigate(R.id.nav_razorpay_payment, bundle)

                    Toast.makeText(requireContext(), "Payment feature will be available soon", Toast.LENGTH_SHORT).show()

                    dialog.dismiss()
                }

                "COD" -> {
                    Toast.makeText(requireContext(), "COD is not available now", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Choose an option to proceed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }

//    private fun sha256(input: String): String {
//        val bytes = input.toByteArray(Charsets.UTF_8)
//        val md = MessageDigest.getInstance("SHA-256")
//        val digest = md.digest(bytes)
//        return digest.fold("") { str, it -> str + "%20x".format(it) }
//    }

//    private fun startPayment() {
//        val checkout = Checkout()
////        checkout.setKeyID("rzp_live_KpR99KMEuOY7EN")
//        checkout.setKeyID(getString(R.string.RAZORPAY_KEY_ID))
//
//        try {
//            val options = JSONObject()
//            options.put("name", "PeWala")
//            options.put("description", "Test Payment")
//            options.put("image", "https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/pewala_app_icon.png?alt=media&token=817f21f9-e36c-4e27-b119-aa1cb0217140")
//            options.put("order_id", "order_OppqhyU2Lro3Qw")
//            options.put("amount", 10000)
//            options.put("currency", "INR")
////            options.put("amount", "10000")
//            options.put("theme.color", "#6B3E2E")
//
//            val prefill = JSONObject()
//            prefill.put("email", "bikash27dutta@gmail.com")
//            prefill.put("contact", "8420248002")
//
//            options.put("prefill", prefill)
//
//            checkout.open(requireActivity(), options)
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun order() {
//        dbViewModel.fetchAccountDetails(uid)
//        dbViewModel.accDetails.observe(viewLifecycleOwner) { doc ->
//            dbViewModel.addOrder(
//                doc.getString("name").toString(),
//                doc.getString("phone").toString(),
//                addressStr!!,
//                "COD",
//                uid,
//                requireArguments().getString("brandName").toString(),
//                requireArguments().getString("productName").toString(),
//                requireArguments().getString("productImageUrl").toString(),
//                productId,
//                requireArguments().getString("category").toString(),
//                requireArguments().getString("productPrice").toString(),
//                count.toString(),
//                requireArguments().getString("sellerUid").toString()
//            )
//
//            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
//                when (it) {
//                    is Response.Success -> {
//                        whiteView.visibility = View.GONE
//                        loader.visibility = View.GONE
//                        Toast.makeText(
//                            requireContext(),
//                            "Your order placed successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        sendNotification(
//                            requireContext(), "Your order successfully placed", "For ${
//                                requireArguments().getString("brandName").toString()
//                            } ${requireArguments().getString("productName").toString()}"
//                        )
//                        requireActivity().onBackPressed()
//                    }
//
//                    is Response.Failure -> {
//                        Toast.makeText(
//                            requireContext(),
//                            it.errorMassage,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        whiteView.visibility = View.GONE
//                        loader.visibility = View.GONE
//                    }
//                }
//            }
//        }
//    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("orderPlaced", "order")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channelName = "Default Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, "default_channel_id")
            .setSmallIcon(R.drawable.pewala_icon1)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }

}