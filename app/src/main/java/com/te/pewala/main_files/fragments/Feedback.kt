package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.google.android.material.textfield.TextInputEditText


class Feedback : Fragment() {

    private lateinit var ratingBar: RatingBar
    private lateinit var productName: TextView
    private lateinit var brandName: TextView
    private lateinit var productImage: ImageView
    private lateinit var feedbackBox: TextInputEditText
    private lateinit var submitBtn: CardView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var ratingText: TextView
    private var ratingF = 0.0f
    private lateinit var productId: String
    private lateinit var buyerUid: String
    private lateinit var sellerUid: String
    private lateinit var orderId: String
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private lateinit var backBtn: ImageView

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]
        
        ratingBar = view.findViewById(R.id.ratingbar_feedback)
        productName = view.findViewById(R.id.productName_feedback)
        productImage = view.findViewById(R.id.productImage_feedback)
        brandName = view.findViewById(R.id.brandName_feedback)
        feedbackBox = view.findViewById(R.id.description_feedback)
        submitBtn = view.findViewById(R.id.submitBtn_feedback)
        ratingText = view.findViewById(R.id.rating_text_feedback)
        whiteView = view.findViewById(R.id.whiteView_feedback)
        loader = view.findViewById(R.id.loader_feedback)
        backBtn = view.findViewById(R.id.back_btn_feedback)

        buyerUid = requireArguments().getString("buyerUid").toString()
        sellerUid = requireArguments().getString("sellerUid").toString()
        productId = requireArguments().getString("productId").toString()
        orderId = requireArguments().getString("orderId").toString()
        productName.text = requireArguments().getString("productName")
        brandName.text = requireArguments().getString("brandName")
        Glide.with(requireContext()).load(requireArguments().getString("productImg")).into(productImage)

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            val color = getColorForRating(rating)
            ratingF = rating
            ratingBar.rating = rating
            ratingBar.progressTintList = ColorStateList.valueOf(color)

            when(rating.toInt()) {
                1 -> ratingText.text = "Bad"
                2 -> ratingText.text = "Not good"
                3 -> ratingText.text = "Average"
                4 -> ratingText.text = "Good"
                5 -> ratingText.text = "Perfect"
            }
        }

        submitBtn.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            submit()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun getColorForRating(rating: Float): Int {
        // Customize the color based on the rating
        // Implement your logic to determine color based on the rating
        return when {
            rating <= 1.0 -> requireActivity().getColor(R.color.rating_color_1)
            rating <= 2.0 -> requireActivity().getColor(R.color.rating_color_2)
            rating <= 3.0 -> requireActivity().getColor(R.color.rating_color_3)
            rating <= 4.0 -> requireActivity().getColor(R.color.rating_color_5)
            else -> requireActivity().getColor(R.color.rating_color_5)
        }
    }

    private fun submit() {
        if(ratingF == 0.0f) {
            Toast.makeText(requireContext(), "Provide rating before submit", Toast.LENGTH_SHORT).show()
            whiteView.visibility = View.GONE
            loader.visibility = View.GONE

        } else {
            ratingBar.setIsIndicator(true)
            submitBtn.isClickable = false
            dbViewModel.addReview(buyerUid, sellerUid, productId, orderId, ratingF, feedbackBox.text.toString())
            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Thanks for your valuable feedback",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }

                    is Response.Failure -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                        ratingBar.setIsIndicator(false)
                        submitBtn.isClickable = true
                    }
                }
            }
        }
    }

}