package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.db.LocalStorage
import com.te.celer.db.Response

class RemoveCartItems : Fragment() {

    private lateinit var brandName: TextView
    private lateinit var productName: TextView
    private lateinit var productImg: ImageView
    private lateinit var quantity: TextView
    private lateinit var description: TextView
    private lateinit var price: TextView
    private lateinit var yesBtn: CardView
    private lateinit var noBtn: CardView
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var userUid: String
    private lateinit var mainLayout: RelativeLayout
    private val localStorage = LocalStorage()

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_remove_cart_items, container, false)

        brandName = view.findViewById(R.id.brandName_remove_cart)
        productName = view.findViewById(R.id.productName_remove_cart)
        productImg = view.findViewById(R.id.productImg_remove_cart)
        quantity = view.findViewById(R.id.quantity_remove_cart)
        description = view.findViewById(R.id.description_remove_cart)
        whiteView = view.findViewById(R.id.whiteView_remove_cart)
        loader = view.findViewById(R.id.loader_remove_cart)
        yesBtn = view.findViewById(R.id.yes_btn_remove_cart)
        noBtn = view.findViewById(R.id.no_btn_remove_cart)
        price = view.findViewById(R.id.price_remove_cart)
        mainLayout = view.findViewById(R.id.main_layout_remove_cart)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        load()

        brandName.text = requireArguments().getString("brandName").toString()
        productName.text = requireArguments().getString("productName").toString()
        quantity.text = "Quantity : " + requireArguments().getString("quantity").toString()
        description.text = requireArguments().getString("description").toString()
        price.text = "₹" + requireArguments().getString("productPrice").toString()
        Glide.with(view).load(requireArguments().getString("productImg").toString())
            .into(productImg)

        yesBtn.setOnClickListener {
            showRemoveDialog(requireArguments().getString("productId").toString(),  userUid)
        }

        noBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun showRemoveDialog(productId: String, uid: String) {
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setContentView(R.layout.dialog_remove_cart)
        dialog.window?.attributes?.windowAnimations = R.anim.pop

        val yesBtnDialog: CardView = dialog.findViewById(R.id.yes_btn_dialog_cart)
        val noBtnDialog: CardView = dialog.findViewById(R.id.no_btn_dialog_cart)

        yesBtnDialog.setOnClickListener {
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            dbViewModel.removeFromCart(productId, uid)
            dbViewModel.fetchCartItems(uid)
            dialog.hide()
            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Following product is removed from your cart",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }

                    is Response.Failure -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        noBtnDialog.setOnClickListener {
            dialog.dismiss()
            requireActivity().onBackPressed()
        }
        dialog.show()
    }

    private fun load() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        mainLayout.visibility = View.VISIBLE
        loader.visibility = View.GONE
        userUid = userdata!!["uid"]!!
    }
}