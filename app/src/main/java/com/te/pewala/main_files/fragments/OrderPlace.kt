package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.te.pewala.main_files.adapters.VideoTutorialsAdapter
import com.te.pewala.main_files.items.VideoTutorialsItems
import de.hdodenhof.circleimageview.CircleImageView
import org.bouncycastle.util.Integers
import kotlin.properties.Delegates

class OrderPlace : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var addToWishlistBtn: ImageButton
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
    private lateinit var placeOrder: CardView
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

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_place, container, false)

        backBtn = view.findViewById(R.id.back_btn_order)
        addToWishlistBtn = view.findViewById(R.id.add_to_whishlist_order)
        shareBtn = view.findViewById(R.id.share_order)
        productNameTitle = view.findViewById(R.id.title_productName_order)
        productName = view.findViewById(R.id.productName_order)
        brandName = view.findViewById(R.id.brandName_order)
        productImage = view.findViewById(R.id.productImage_order)
//        stocks = view.findViewById(R.id.stocks_order)
        ratingBar = view.findViewById(R.id.ratingbar_order)
        ratingText = view.findViewById(R.id.rating_text_order)
        description = view.findViewById(R.id.description_order)
        productPrice = view.findViewById(R.id.product_price_order)
        placeOrder = view.findViewById(R.id.place_order)
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

        mainLayout.visibility = View.GONE

        productId = requireArguments().getString("productId").toString()
        sellerUid = requireArguments().getString("seller_uid").toString()

//        productVideo.setVideoURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-chat-app-98801.appspot.com/o/videos%2FRetro%20Kahuna%20Icon%20Senior%20Cricket%20Bat%20_%20Kookaburra%20Cricket.mp4?alt=media&token=87bc641b-ae8c-4b05-8b38-a49197a83ea9"))
//        productVideo.setOnPreparedListener {
//            mp -> mp.isLooping = true
//            mp.setVolume(0f, 0f)
//            loaderVideo.visibility = View.GONE
//        }
//        productVideo.start()
//
//        productVideo.setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.nav_product_feed)
//        }

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
                ratingBar.rating = Integer.parseInt(ratings).toFloat()
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

//        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        messageOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("receiverUid", sellerUid)
            Navigation.findNavController(it).navigate(R.id.nav_chat, bundle)
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
            else Navigation.findNavController(view).navigate(R.id.nav_cart)
        }

        placeOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("brandName", brandNameStr)
            bundle.putString("productName", productNameStr)
            bundle.putString("productImageUrl", productImageUrl)
            bundle.putString("productPrice", productPriceStr)
            bundle.putString("sellerName", sellerNameStr)
            bundle.putString("sellerImageUrl", sellerImageUrl)
            bundle.putString("rating", ratings)
//            bundle.putString("quantity", stocksStr)
            bundle.putString("description", descriptionStr)
            bundle.putString("productId", productId)
            bundle.putString("category", category)
            bundle.putString("sellerUid", sellerUid)
            Navigation.findNavController(it).navigate(R.id.nav_final_order_place, bundle)
        }

        val data = arguments?.getString("data")
        data?.let {
            // Do something with the data
        }

        return view
    }

    private fun addToWishlist(productId: String) {
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
                    addToWishlistBtn.setImageResource(R.drawable.love_icon)
                    flag = true
                    Toast.makeText(
                        requireContext(),
                        "This product is added to your wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
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
        dbViewModel.removeFromWishlist(productId, uid!!)
        dbViewModel.dbResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    addToWishlistBtn.setImageResource(R.drawable.whishlist_icon)
                    Toast.makeText(
                        requireContext(),
                        "This product is removed from your wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
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
        dbViewModel.addToCart(requireArguments().getString("category").toString(), productId, uid!!)
        dbViewModel.dbResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    cartText.text = "Go to cart"
                    check = true
                    Toast.makeText(
                        requireContext(),
                        "This product is added to your cart",
                        Toast.LENGTH_SHORT
                    ).show()
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

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                uid = it.uid
                dbViewModel.isInWishList(productId, it.uid)
                dbViewModel.isInWishlistData.observe(viewLifecycleOwner) { bool1 ->
                    if (bool1) {
                        addToWishlistBtn.setImageResource(R.drawable.love_icon)
                        flag = true
                    } else {
                        addToWishlistBtn.setImageResource(R.drawable.whishlist_icon)
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