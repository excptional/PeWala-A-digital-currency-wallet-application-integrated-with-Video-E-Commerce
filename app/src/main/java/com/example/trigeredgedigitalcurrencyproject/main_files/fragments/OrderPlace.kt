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
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import de.hdodenhof.circleimageview.CircleImageView

class OrderPlace : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var addWishlistBtn: ImageButton
    private lateinit var shareBtn: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var brandName: TextView
    private lateinit var quantity: TextView
    private lateinit var description: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingText: TextView
    private lateinit var sellerName: TextView
    private lateinit var sellerImg: CircleImageView
    private lateinit var placeOrder: RelativeLayout
    private lateinit var addToCart: RelativeLayout

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_place, container, false)

        backBtn = view.findViewById(R.id.back_btn_order)
        addWishlistBtn = view.findViewById(R.id.add_in_whishlist_order)
        shareBtn = view.findViewById(R.id.share_order)
        productName = view.findViewById(R.id.productName_order)
        brandName = view.findViewById(R.id.brandName_order)
        productImage = view.findViewById(R.id.productImage_order)
        quantity = view.findViewById(R.id.quantity_order)
        ratingBar = view.findViewById(R.id.ratingbar_order)
        ratingText = view.findViewById(R.id.rating_text_order)
        description = view.findViewById(R.id.description_order)
        productPrice = view.findViewById(R.id.product_price_order)
        placeOrder = view.findViewById(R.id.place_order)
        addToCart = view.findViewById(R.id.add_to_card_order)
        sellerName = view.findViewById(R.id.sellerName_order)
        sellerImg = view.findViewById(R.id.sellerImage_order)

        productName.text = requireArguments().getString("productName")
        productPrice.text = "₹" + requireArguments().getString("productPrice") + " INR"
        brandName.text = requireArguments().getString("brandName")
        quantity.text = "Quantity : " + requireArguments().getString("quantity")
        ratingBar.rating = requireArguments().getString("rating")!!.toFloat()
        ratingText.text = requireArguments().getString("rating")
        description.text = requireArguments().getString("description")
        sellerName.text = requireArguments().getString("sellerName")
        Glide.with(view).load(requireArguments().getString("sellerImageUrl")).into(sellerImg)
        Glide.with(view).load(requireArguments().getString("productImageUrl")).into(productImage)

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        addWishlistBtn.setOnClickListener {
            Toast.makeText(requireContext(), "This feature is not implemented till now", Toast.LENGTH_SHORT).show()
        }

        shareBtn.setOnClickListener {
            Toast.makeText(requireContext(), "This feature is not implemented till now", Toast.LENGTH_SHORT).show()
        }

        addToCart.setOnClickListener {
            Toast.makeText(requireContext(), "This feature is not implemented till now", Toast.LENGTH_SHORT).show()
        }

        placeOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("brandName", brandName.text.toString())
            bundle.putString("productName", productName.text.toString())
            bundle.putString("productImageUrl", requireArguments().getString("productImageUrl"))
            bundle.putString("productPrice", requireArguments().getString("productPrice"))
            bundle.putString("sellerName", sellerName.text.toString())
            bundle.putString("sellerImageUrl", requireArguments().getString("sellerImageUrl"))
//            bundle.putString("rating", currentItem.ratings)
            bundle.putString("quantity", quantity.text.toString())
            bundle.putString("description", description.text.toString())
            bundle.putString("productId", requireArguments().getString("productId"))
            bundle.putString("category", requireArguments().getString("category"))
            bundle.putString("sellerUid", requireArguments().getString("sellerUid"))
            Navigation.findNavController(it).navigate(R.id.nav_final_order_place, bundle)
        }

        return view
    }

}