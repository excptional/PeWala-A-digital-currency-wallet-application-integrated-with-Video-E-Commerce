package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.db.Response
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.properties.Delegates

class OrderPlace : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var addToWishlistBtn: ImageButton
    private lateinit var shareBtn: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var brandName: TextView
    private lateinit var stocks: TextView
    private lateinit var description: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingText: TextView
    private lateinit var sellerName: TextView
    private lateinit var cartText: TextView
    private lateinit var sellerImg: CircleImageView
    private lateinit var placeOrder: RelativeLayout
    private lateinit var addToCart: RelativeLayout
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
    private lateinit var stocksStr: String
    private lateinit var descriptionStr: String
    private lateinit var ratings: String
    private lateinit var sellerNameStr: String
    private lateinit var sellerImageUrl: String
    private lateinit var productId: String
    private lateinit var category: String
    private lateinit var sellerUid: String
    private lateinit var mainLayout: RelativeLayout

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_place, container, false)

        backBtn = view.findViewById(R.id.back_btn_order)
        addToWishlistBtn = view.findViewById(R.id.add_to_whishlist_order)
        shareBtn = view.findViewById(R.id.share_order)
        productName = view.findViewById(R.id.productName_order)
        brandName = view.findViewById(R.id.brandName_order)
        productImage = view.findViewById(R.id.productImage_order)
        stocks = view.findViewById(R.id.stocks_order)
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

        dbViewModel.getProductDetails(
            requireArguments().getString("category").toString(),
            requireArguments().getString("productId").toString()
        )
        dbViewModel.productDetails.observe(viewLifecycleOwner) { list1 ->
            if (list1 != null) {
                productImageUrl = list1.getString("Product Image").toString()
                productNameStr = list1.getString("Product Name").toString()
                productPriceStr = list1.getString("Product Price").toString()
                brandNameStr = list1.getString("Brand Name").toString()
                stocksStr = list1.getString("Stocks").toString()
                descriptionStr = list1.getString("Description").toString()
                ratings = list1.getString("Ratings").toString()
                sellerNameStr = list1.getString("Seller Name").toString()
                sellerImageUrl = list1.getString("Seller Image").toString()
                productId = list1.getString("Product ID").toString()
                category = list1.getString("Category").toString()
                sellerUid = list1.getString("Seller UID").toString()

                productName.text = productNameStr
                productPrice.text = "₹$productPriceStr"
                brandName.text = brandNameStr
                stocks.text = "Stocks : $stocksStr"
                ratingBar.rating = ratings.toFloat()
                ratingText.text = ratings
                description.text = descriptionStr
                sellerName.text = sellerNameStr
                Glide.with(view).load(sellerImageUrl).into(sellerImg)
                Glide.with(view).load(productImageUrl).into(productImage)
                mainLayout.visibility = View.VISIBLE
                loader.visibility = View.GONE
            }
        }

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        addToWishlistBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            if (!flag) addToWishlist(requireArguments().getString("productId").toString())
            else removeFromWishlist(requireArguments().getString("productId").toString())
        }

        shareBtn.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "This feature is not implemented till now",
                Toast.LENGTH_SHORT
            ).show()
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
            bundle.putString("quantity", stocksStr)
            bundle.putString("description", descriptionStr)
            bundle.putString("productId", productId)
            bundle.putString("category", category)
            bundle.putString("sellerUid", sellerUid)
            Navigation.findNavController(it).navigate(R.id.nav_final_order_place, bundle)
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
                dbViewModel.isInWishList(requireArguments().getString("productId").toString(), it.uid)
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

}