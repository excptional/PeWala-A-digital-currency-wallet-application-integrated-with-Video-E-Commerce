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
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
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
    private lateinit var address: EditText
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private var price = 0

    @SuppressLint("SetTextI18n")
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
        finalAmount = view.findViewById(R.id.finalAmount_order_summary)
        placeOrder = view.findViewById(R.id.placeOrder_btn_order_summary)
        address = view.findViewById(R.id.address_order_summary)

        price = Integer.parseInt(requireArguments().getString("productPrice").toString())

        productName.text = requireArguments().getString("productName")
//        updatedProductPrice.text = "₹" + requireArguments().getString("productPrice") + " INR"
        brandName.text = requireArguments().getString("brandName")
        quantity.text = "Quantity : " + requireArguments().getString("quantity")
        description.text = requireArguments().getString("description")
        Glide.with(view).load(requireArguments().getString("productImageUrl")).into(productImage)

        var count = 1
        var finalAmountInt = 0
        quantity.text = "1"

        var temp1: Int = price * count
        var temp2 = temp1 + 30
        finalAmountInt = temp2

        updatedProductPrice.text = "Product Price :  ₹$temp1"
        totalPrice.text = "Total amount you have to pay : ₹$temp2 INR"

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        plusBtn.setOnClickListener {
            if (count < 12) {
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

        placeOrder.setOnClickListener {
            order()
            Toast.makeText(requireContext(), "Your order placed successfully", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
        return view
    }

    private fun order() {
        val addressStr = address.text.toString()
        if(addressStr.isEmpty()) {
            Toast.makeText(requireContext(), "Enter your proper address to place order", Toast.LENGTH_SHORT).show()
        } else {
            authViewModel.userdata.observe(viewLifecycleOwner) { it ->
                if (it != null) {
                    dbViewModel.fetchAccountDetails(it.uid)
                    dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                        dbViewModel.addOrder(list[0], list[1], addressStr, it.uid, requireArguments().getString("productName").toString(),
                            requireArguments().getString("productImageUrl").toString(), requireArguments().getString("productId").toString(),
                            requireArguments().getString("category").toString(), requireArguments().getString("productPrice").toString(),
                            requireArguments().getString("quantity").toString(), requireArguments().getString("sellerUid").toString())
                    }
                }
            }
        }
    }

}