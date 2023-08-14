package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import kotlin.properties.Delegates

class FinalOrderPlace : Fragment() {

    private lateinit var closeBtn: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var brandName: TextView
    private lateinit var quantity: TextView
    private lateinit var description: TextView
    private lateinit var placeOrder: Button
    private lateinit var plusBtn: ImageButton
    private lateinit var minusBtn: ImageButton
    private lateinit var updatedProductPrice: TextView
    private lateinit var totalPrice: TextView
    private lateinit var finalAmount: TextView
    private lateinit var address: LinearLayout
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private var price = 0
    private var availableStocks by Delegates.notNull<Int>()
    private var count = 1
    private lateinit var uid: String
    private lateinit var addressStr: String
    private lateinit var locality: TextView
    private lateinit var city_postal: TextView
    private lateinit var state: TextView

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_final_order_place, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        closeBtn = view.findViewById(R.id.close_btn_order_summary)
        productImage = view.findViewById(R.id.productImg_order_summary)
        productName = view.findViewById(R.id.productName_order_summary)
        brandName = view.findViewById(R.id.brandName_order_summary)
        quantity = view.findViewById(R.id.quantity_order_summary)
        description = view.findViewById(R.id.description_order_summary)
        updatedProductPrice = view.findViewById(R.id.updatedProductPrice_order_summary)
        totalPrice = view.findViewById(R.id.totalPrice_order_summary)
        plusBtn = view.findViewById(R.id.plus_order_summary)
        minusBtn = view.findViewById(R.id.minus_order_summary)
        finalAmount = view.findViewById(R.id.final_amount_order_summary)
        placeOrder = view.findViewById(R.id.placeOrder_btn_order_summary)
        address = view.findViewById(R.id.address_order_summary)
        whiteView = view.findViewById(R.id.whiteView_order_summary)
        loader = view.findViewById(R.id.loader_order_summary)
        locality = view.findViewById(R.id.area_order_summary)
        city_postal = view.findViewById(R.id.city_postal_order_summary)
        state = view.findViewById(R.id.state_order_summary)

        whiteView.visibility = View.VISIBLE
        loader.visibility = View.VISIBLE

        price = Integer.parseInt(requireArguments().getString("productPrice").toString())
        productName.text = requireArguments().getString("productName")
        brandName.text = requireArguments().getString("brandName")
        availableStocks = requireArguments().getString("quantity")!!.toInt()
        description.text = requireArguments().getString("description")
        Glide.with(view).load(requireArguments().getString("productImageUrl")).into(productImage)

        load(view)

        var finalAmountInt = 0
        quantity.text = "1"

        var temp1: Int = price * count
        var temp2 = temp1 + 30
        finalAmountInt = temp2

        finalAmount.text = "₹$finalAmountInt"

        updatedProductPrice.text = "Product Price :  ₹$temp1"
        totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        plusBtn.setOnClickListener {
            if (count <= availableStocks) {
                count++
                quantity.text = "$count"
                temp1 = price * count
                temp2 = temp1 + 30
                finalAmountInt = temp2
                updatedProductPrice.text = "Product Price :  ₹$temp1"
                totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"
                finalAmount.text = "₹$temp2"
            } else {
                Toast.makeText(
                    requireContext(),
                    "You can't order more than 12 items",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        minusBtn.setOnClickListener {
            if (count > 1) {
                count--
                quantity.text = "$count"
                temp1 = price * count
                temp2 = temp1 + 30
                finalAmountInt = temp2
                updatedProductPrice.text = "Product Price :  ₹$temp1"
                totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"
                finalAmount.text = "₹$temp2"
            } else {
                Toast.makeText(
                    requireContext(),
                    "Already minimum no. of item selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        address.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_address)
        }

        placeOrder.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            order()
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun load(view: View) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if (it != null) {
                uid = it.uid
                dbViewModel.getAddress(uid)
                dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                    when (it) {
                        is Response.Success -> {
                            dbViewModel.addressData.observe(viewLifecycleOwner) { doc ->
                                if (doc.exists()) {
                                    locality.text = doc.getString("Locality")
                                    city_postal.text =
                                        doc.getString("City") + ", " + doc.getString("Postal Code")
                                    state.text = doc.getString("State")
                                    addressStr =
                                        "${doc.getString("Locality")}, ${doc.getString("Landmark")}, " +
                                                "${doc.getString("City")}, ${doc.getString("Postal Code")}, " +
                                                "${doc.getString("State")}"
                                    whiteView.visibility = View.GONE
                                    loader.visibility = View.GONE
                                    address.visibility = View.VISIBLE
                                }
                            }
                        }

                        is Response.Failure -> {
                            whiteView.visibility = View.GONE
                            loader.visibility = View.GONE
//                            Navigation.findNavController(view).popBackStack()
                            Navigation.findNavController(view).navigate(R.id.nav_address)
                        }
                    }
                }
            }
        }
    }

    private fun order() {
        dbViewModel.fetchAccountDetails(uid)
        dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
            dbViewModel.addOrder(
                list[0],
                list[1],
                addressStr,
                uid,
                requireArguments().getString("brandName").toString(),
                requireArguments().getString("productName").toString(),
                requireArguments().getString("productImageUrl").toString(),
                requireArguments().getString("productId").toString(),
                requireArguments().getString("category").toString(),
                requireArguments().getString("productPrice").toString(),
                count.toString(),
                requireArguments().getString("sellerUid").toString()
            )

            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Your order placed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }

                    is Response.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            it.errorMassage,
                            Toast.LENGTH_SHORT
                        ).show()
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                    }
                }
            }
        }
    }
}