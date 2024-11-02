package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.pewala.R
import com.te.pewala.db.DBViewModel
import com.te.pewala.db.Response
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class UpdateProductDetails : Fragment() {

    private lateinit var productId: String
    private var productImageUri: Uri? = null
    private lateinit var productImage: ImageView
    private lateinit var changeProductImage: RelativeLayout
    private lateinit var backBtn: ImageView
    private lateinit var updateBtn: CardView
    private lateinit var productNameET: AppCompatEditText
    private lateinit var brandNameET: AppCompatEditText
    private lateinit var priceET: AppCompatEditText
    private lateinit var stocksET: AppCompatEditText
    private lateinit var descriptionET: AppCompatEditText
    private lateinit var productNameStr: String
    private lateinit var brandNameStr: String
    private lateinit var priceStr: String
    private lateinit var stocksStr: String
    private lateinit var descriptionStr: String
    private lateinit var categoryStr: String
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private lateinit var sellerUid: String
    private val dbViewModel: DBViewModel by viewModels()

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(6, 7)
                .setOutputCompressQuality(50)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    @SuppressLint("MissingInflatedId", "CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_product_details, container, false)

        productImage = view.findViewById(R.id.product_image_update_product_details)
        productNameET = view.findViewById(R.id.product_name_update_product_details)
        brandNameET = view.findViewById(R.id.brand_name_update_product_details)
        priceET = view.findViewById(R.id.price_update_product_details)
        stocksET = view.findViewById(R.id.stocks_update_product_details)
        descriptionET = view.findViewById(R.id.description_update_product_details)
        backBtn = view.findViewById(R.id.back_btn_update_product_details)
        updateBtn = view.findViewById(R.id.btn_update_product_details)
        whiteView = view.findViewById(R.id.whiteView_update_product_details)
        loader = view.findViewById(R.id.loader_update_product_details)
        changeProductImage = view.findViewById(R.id.change_image_update_product_details)

        sellerUid = requireArguments().getString("sellerUid")!!
        productId = requireArguments().getString("productId")!!
        categoryStr = requireArguments().getString("category")!!
        productNameStr = requireArguments().getString("productName")!!
        brandNameStr = requireArguments().getString("brandName")!!
        priceStr = requireArguments().getString("productPrice")!!
        stocksStr = requireArguments().getString("quantity")!!
        descriptionStr = requireArguments().getString("description")!!
        Glide.with(view).load(requireArguments().getString("productImageUrl")).into(productImage)

        productNameET.setText(productNameStr)
        brandNameET.setText(brandNameStr)
        priceET.setText(priceStr)
        stocksET.setText(stocksStr)
        descriptionET.setText(descriptionStr)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                productImageUri = uri
                productImage.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "No image is selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        changeProductImage.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }


        productNameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                   productNameET.setText(productNameStr)
                }
            }
        })

        brandNameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    brandNameET.setText(brandNameStr)
                }
            }
        })

        stocksET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    stocksET.setText(stocksStr)
                }
            }
        })

        priceET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    priceET.setText(priceStr)
                }
            }
        })

        descriptionET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    descriptionET.setText(descriptionStr)
                }
            }
        })

        updateBtn.setOnClickListener {
            updateBtn.isClickable = false
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            updateProductDetails()
        }

        return view
    }


    private fun updateProductDetails() {
        dbViewModel.updateProductDetails(sellerUid, productId, categoryStr, productNameET.text.toString(), brandNameET.text.toString(),
            productImageUri, priceET.text.toString(), stocksET.text.toString(), descriptionET.text.toString())

        dbViewModel.dbResponse.observe(viewLifecycleOwner) {
            when(it) {
                is Response.Failure -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                    updateBtn.isClickable = true

                }
                is Response.Success -> {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    Toast.makeText(requireContext(), "Product details are updated successfully", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(requireView()).popBackStack()
                    requireActivity().onBackPressed()
                }

            }
        }
    }

}