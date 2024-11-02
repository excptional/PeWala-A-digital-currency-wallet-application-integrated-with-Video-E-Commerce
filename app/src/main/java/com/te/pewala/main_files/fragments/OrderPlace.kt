package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.LocalStorage
import com.te.pewala.db.Response
import com.te.pewala.main_files.adapters.VideoTutorialsAdapter
import com.te.pewala.main_files.models.VideoTutorialsItems
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.properties.Delegates

class OrderPlace : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var addToWishlistBtn: RelativeLayout
    private lateinit var wishlistIcon: ImageView
    private lateinit var wishlistText: TextView
    private lateinit var shareBtn: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productNameTitle: TextView
    private lateinit var productPrice: TextView
    private lateinit var brandName: TextView
    private lateinit var stocks: TextView
    private lateinit var description: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingText: TextView
    private lateinit var sellerName: TextView
    private lateinit var cartText: TextView
    private lateinit var sellerImg: CircleImageView
//    private lateinit var placeOrder: CardView
    private lateinit var addToCart: CardView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private var uid: String? = null
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private var flag by Delegates.notNull<Boolean>()
    private var check by Delegates.notNull<Boolean>()
    private lateinit var productImageUrl: String
    private lateinit var productNameStr: String
    private lateinit var productPriceStr: String
    private lateinit var brandNameStr: String
    private lateinit var descriptionStr: String
    private lateinit var ratings: String
    private lateinit var sellerNameStr: String
    private lateinit var sellerImageUrl: String
    private lateinit var productId: String
    private lateinit var category: String
    private lateinit var sellerUid: String
    private lateinit var mainLayout: RelativeLayout
    private lateinit var videoAdapter: VideoTutorialsAdapter
    private var videoItemsArray = arrayListOf<VideoTutorialsItems>()
    private lateinit var recyclerview: RecyclerView
    private lateinit var messageOrder: CardView
    private lateinit var videoCall: ZegoSendCallInvitationButton
    private lateinit var voiceCall: ZegoSendCallInvitationButton
    private lateinit var videoCallBtn: CardView
    private lateinit var voiceCallBtn: CardView
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_place, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        backBtn = view.findViewById(R.id.back_btn_order)
        addToWishlistBtn = view.findViewById(R.id.add_to_wishlist_order)
        wishlistIcon = view.findViewById(R.id.wishlist_icon_order)
        wishlistText = view.findViewById(R.id.wishlist_text_order)
//        shareBtn = view.findViewById(R.id.share_order)
        productNameTitle = view.findViewById(R.id.title_productName_order)
        productName = view.findViewById(R.id.productName_order)
        brandName = view.findViewById(R.id.brandName_order)
        productImage = view.findViewById(R.id.productImage_order)
//        stocks = view.findViewById(R.id.stocks_order)
        ratingBar = view.findViewById(R.id.ratingbar_order)
        ratingText = view.findViewById(R.id.rating_text_order)
        description = view.findViewById(R.id.description_order)
        productPrice = view.findViewById(R.id.product_price_order)
        addToCart = view.findViewById(R.id.add_to_card_order)
        sellerName = view.findViewById(R.id.sellerName_order)
        sellerImg = view.findViewById(R.id.sellerImage_order)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        whiteView = view.findViewById(R.id.whiteView_order)
        loader = view.findViewById(R.id.loader_order)
        cartText = view.findViewById(R.id.cart_text_order)
        mainLayout = view.findViewById(R.id.main_layout_order)
        recyclerview = view.findViewById(R.id.recyclerView_order)
        messageOrder = view.findViewById(R.id.message_order)
        videoCall = view.findViewById(R.id.video_call_order)
        voiceCall = view.findViewById(R.id.voice_call_order)
//        videoCallBtn = view.findViewById(R.id.video_call_btn_order)
//        voiceCallBtn = view.findViewById(R.id.voice_call_btn_order)

        mainLayout.visibility = View.GONE

        productId = requireArguments().getString("productId").toString()
        sellerUid = requireArguments().getString("seller_uid").toString()

        dbViewModel.getProductDetails(
            requireArguments().getString("category").toString(),
            requireArguments().getString("productId").toString()
        )
        dbViewModel.productDetails.observe(viewLifecycleOwner) { list1 ->
            if (list1 != null) {
                productImageUrl = list1.getString("product_image_url").toString()
                productNameStr = list1.getString("product_name").toString()
                productPriceStr = list1.getString("product_price").toString()
                brandNameStr = list1.getString("brand_name").toString()
//                stocksStr = list1.getString("Stocks").toString()
                descriptionStr = list1.getString("description").toString()
                ratings = list1.getString("ratings").toString()
                sellerNameStr = list1.getString("seller_name").toString()
                sellerImageUrl = list1.getString("seller_image_url").toString()
//                productId = list1.getString("Product ID").toString()
                category = list1.getString("category").toString()
//                sellerUid = list1.getString("Seller UID").toString()

                productName.text = productNameStr
                productNameTitle.text = productNameStr
                productPrice.text = "$productPriceStr INR"
                brandName.text = brandNameStr
//                stocks.text = "Stocks : $stocksStr"
                ratingBar.rating = ratings.toFloat()
                ratingText.text = ratings
                description.text = descriptionStr
                sellerName.text = sellerNameStr
                Glide.with(view).load(sellerImageUrl).into(sellerImg)
                Glide.with(view).load(productImageUrl).into(productImage)
                mainLayout.visibility = View.VISIBLE
                loader.visibility = View.GONE

                dbViewModel.getVideoTutorials(productId)
                dbViewModel.videoTutorialsData.observe(viewLifecycleOwner) { list ->
                    if(!list.isNullOrEmpty()) {
                        fetchData(list, productId)
                    }
                }
            }
        }

        loadData()

        videoAdapter = VideoTutorialsAdapter(requireContext(), videoItemsArray)
        recyclerview.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
//        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = videoAdapter

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

//        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        messageOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("receiverUid", sellerUid)
            Navigation.findNavController(view).navigate(R.id.nav_chat, bundle, navBuilder.build())
        }

        addToWishlistBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            if (!flag) addToWishlist(requireArguments().getString("productId").toString())
            else removeFromWishlist(requireArguments().getString("productId").toString())
        }

        addToCart.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            if (!check) addToCart(requireArguments().getString("productId").toString())
            else Navigation.findNavController(view).navigate(R.id.nav_cart, null, navBuilder.build())
        }

//        placeOrder.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("brandName", brandNameStr)
//            bundle.putString("productName", productNameStr)
//            bundle.putString("productImageUrl", productImageUrl)
//            bundle.putString("productPrice", productPriceStr)
//            bundle.putString("sellerName", sellerNameStr)
//            bundle.putString("sellerImageUrl", sellerImageUrl)
//            bundle.putString("rating", ratings)
////            bundle.putString("quantity", stocksStr)
//            bundle.putString("description", descriptionStr)
//            bundle.putString("productId", productId)
//            bundle.putString("category", category)
//            bundle.putString("sellerUid", sellerUid)
//            Navigation.findNavController(view).navigate(R.id.nav_final_order_place, bundle, navBuilder.build())
//        }

        startVideoCall()
        startVoiceCall()

        return view
    }

    private fun addToWishlist(productId: String) {
        Toast.makeText(
            requireContext(),
            "This product is added to your wishlist",
            Toast.LENGTH_SHORT
        ).show()
        dbViewModel.addToWishlist(
            requireArguments().getString("category").toString(),
            productId,
            uid!!
        )
        dbViewModel.dbResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    wishlistIcon.setImageResource(R.drawable.love_icon)
                    wishlistText.text = "Wishlisted"
                    flag = true
//                    Toast.makeText(
//                        requireContext(),
//                        "This product is added to your wishlist",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

                is Response.Failure -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    flag = false
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeFromWishlist(productId: String) {
        Toast.makeText(
            requireContext(),
            "This product is removed from your wishlist",
            Toast.LENGTH_SHORT
        ).show()
        dbViewModel.removeFromWishlist(productId, uid!!)
        dbViewModel.dbResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    wishlistIcon.setImageResource(R.drawable.wishlist_icon)
                    wishlistText.text = "Wishlist"
//                    Toast.makeText(
//                        requireContext(),
//                        "This product is removed from your wishlist",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

                is Response.Failure -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addToCart(productId: String) {
        Toast.makeText(
            requireContext(),
            "This product is added to your cart",
            Toast.LENGTH_SHORT
        ).show()
        dbViewModel.addToCart(requireArguments().getString("category").toString(), productId, uid!!)
        dbViewModel.dbResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    cartText.text = "Go to cart"
                    check = true
//                    Toast.makeText(
//                        requireContext(),
//                        "This product is added to your cart",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

                is Response.Failure -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    check = false
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startVideoCall() {
        videoCall.setIsVideoCall(true)
        videoCall.resourceID = "zego_uikit_call"
        videoCall.setInvitees(listOf(ZegoUIKitUser(sellerUid)))
//        ZegoUIKitPrebuiltCallService.sendCallInvitation(inviteeUserID, isVideoCall, object : ZegoUIKitPrebuiltCallService.Callback {
//            override fun onResult(errorCode: Int, errorMessage: String) {
//                if (errorCode == 0) {
//                    // Call invitation sent successfully
//                    Toast.makeText(this@MainActivity, "Call invitation sent successfully!", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Handle error
//                    Toast.makeText(this@MainActivity, "Failed to send call invitation: $errorMessage", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })

    }

    private fun startVoiceCall() {
        voiceCall.setIsVideoCall(false)
        voiceCall.resourceID = "zego_uikit_call"
        voiceCall.setInvitees(listOf(ZegoUIKitUser(sellerUid)))
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val userdata = localStorage.getData(requireContext(),"user_data")
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                uid = it.uid
                dbViewModel.isInWishList(productId, it.uid)
                dbViewModel.isInWishlistData.observe(viewLifecycleOwner) { bool1 ->
                    if (bool1) {
                        wishlistIcon.setImageResource(R.drawable.love_icon)
                        wishlistText.text = "Wishlisted"
                        flag = true
                    } else {
                        wishlistIcon.setImageResource(R.drawable.wishlist_icon)
                        wishlistText.text = "Wishlist"
                        flag = false
                    }
                }
                dbViewModel.isInCart(requireArguments().getString("productId").toString(), it.uid)
                dbViewModel.isInCartData.observe(viewLifecycleOwner) { bool2 ->
                    if (bool2) {
                        cartText.text = "Go to cart"
                        check = true
                    } else {
                        check = false
                    }
                }
            }
        }
    }

    private fun fetchData(list: MutableList<DocumentSnapshot>, productId: String) {
        videoItemsArray = arrayListOf()
        for (doc in list) {
            if (doc.exists()) {
                val videoData = VideoTutorialsItems(
                    doc.getString("video_url"),
                    productNameStr,
                    brandNameStr,
                    productId,
                    sellerUid
                )
                videoItemsArray.add(videoData)
            }
        }
        videoAdapter.updateVideoTutorials(videoItemsArray)
        recyclerview.visibility = View.VISIBLE
    }

}