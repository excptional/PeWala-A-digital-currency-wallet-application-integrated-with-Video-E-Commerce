package com.te.pewala.main_files.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.te.pewala.R
import com.te.pewala.db.AuthViewModel
import com.te.pewala.db.DBViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.Locale

class AddProduct : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var productImageUri: Uri
    private lateinit var productName: TextInputEditText
    private lateinit var price: TextInputEditText
    private lateinit var quantity: TextInputEditText
    private lateinit var brandName: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var keywords: TextInputEditText
    private lateinit var quantityTypeSpinner: Spinner
    private lateinit var productTypeSpinner: Spinner
    private lateinit var beforeLayoutProductImage: CardView
    private lateinit var afterLayoutProductImage: LinearLayout
    private lateinit var addProductImg: ImageView
    private var quantityTypeStr: String = ""
    private var productTypeStr: String = ""
    private lateinit var dbViewModel: DBViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var myUser: FirebaseUser
    private lateinit var uploadBtn: CardView
    private lateinit var addProductImage: LinearLayout

    private val productArray = arrayListOf(
        "Category",
        "Groceries",
        "Fashion",
        "Electronics",
        "Appliances",
        "Sports",
        "Furniture",
        "Books",
        "Personal Care",
        "Medicines"
    )
    private val quantityArray = arrayListOf("Quantity", "units", "kg", "gram", "l", "ml")

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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        productName = view.findViewById(R.id.productName)
        price = view.findViewById(R.id.price)
        quantity = view.findViewById(R.id.quantity)
        brandName = view.findViewById(R.id.brandName)
        description = view.findViewById(R.id.description)
        keywords = view.findViewById(R.id.addTags)
        quantityTypeSpinner = view.findViewById(R.id.quantityTypeSpinner)
        productTypeSpinner = view.findViewById(R.id.productTypeSpinner)
        addProductImg = view.findViewById(R.id.productImage)
        beforeLayoutProductImage = view.findViewById(R.id.beforeLayout_productImage)
        afterLayoutProductImage = view.findViewById(R.id.afterLayout_productImage)
        uploadBtn = view.findViewById(R.id.registerButton)
        addProductImage = view.findViewById(R.id.addProductImage)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                productImageUri = uri
                addProductImg.setImageURI(uri)
                if (beforeLayoutProductImage.isVisible) {
                    beforeLayoutProductImage.visibility = View.GONE
                    afterLayoutProductImage.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(requireContext(), "No image is selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val aa1 = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            productArray
        )
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(productTypeSpinner)
        {
            adapter = aa1
            setSelection(0, true)
            onItemSelectedListener = this@AddProduct
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.white)
        }

        val aa2 = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            quantityArray
        )
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(quantityTypeSpinner)
        {
            adapter = aa2
            setSelection(0, true)
            onItemSelectedListener = this@AddProduct
            gravity = Gravity.CENTER
            setPopupBackgroundResource(R.color.white)
        }

        addProductImage.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        uploadBtn.setOnClickListener {
            upload()
        }

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent!!.id == R.id.productTypeSpinner) {
            if (position == 0) {
                Toast.makeText(
                    requireContext(),
                    "Nothing selected, select product type",
                    Toast.LENGTH_SHORT
                ).show()
                productTypeStr = ""
            } else productTypeStr = productArray[position]
        }

        if (parent.id == R.id.quantityTypeSpinner) {
            if (position == 0) {
                Toast.makeText(
                    requireContext(),
                    "Nothing selected, select quantity type",
                    Toast.LENGTH_SHORT
                ).show()
                quantityTypeStr = ""
            } else quantityTypeStr = quantityArray[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show()
    }

    private fun upload() {
        val productNameStr = productName.text.toString()
        val quantityStr = quantity.text.toString()
        val priceStr = price.text.toString()
        val descriptionStr = description.text.toString()
        var keywordsStr = productName.text.toString() + ", " + brandName.text.toString() + ", " + keywords.text.toString()
        keywordsStr = keywordsStr.toLowerCase(Locale.ROOT)
        val brandNameStr = brandName.text.toString()

        var isAlright = true

        if (productNameStr.isEmpty()) {
            productName.error = "Enter product name"
            isAlright = false
        }
        if (brandNameStr.isEmpty()) {
            productName.error = "Enter product name"
            isAlright = false
        }
        if (priceStr.isEmpty()) {
            price.error = "Enter product price"
            isAlright = false
        }
        if (quantityStr.isEmpty()) {
            quantity.error = "Enter quantity"
            isAlright = false
        }
        if (descriptionStr.isEmpty()) {
            description.error = "Enter description here"
            isAlright = false
        }
        if (beforeLayoutProductImage.isVisible) {
            Toast.makeText(
                requireActivity().applicationContext,
                "Add your product image",
                Toast.LENGTH_SHORT
            ).show()
            isAlright = false
        }
        if (productTypeStr.isEmpty()) {
            Toast.makeText(
                requireActivity().applicationContext,
                "Choose your product type",
                Toast.LENGTH_SHORT
            ).show()
            isAlright = false
        }
        if (quantityTypeStr.isEmpty()) {
            Toast.makeText(
                requireActivity().applicationContext,
                "Choose your quantity type",
                Toast.LENGTH_SHORT
            ).show()
            isAlright = false
        }

        if (!isAlright) {
            Toast.makeText(requireContext(), "Enter valid details", Toast.LENGTH_SHORT).show()
        } else {
            authViewModel.userdata.observe(viewLifecycleOwner) {
                if (it != null) {
                    dbViewModel.fetchAccountDetails(it.uid)
                    dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                        if(list.exists()) {
                            dbViewModel.addProduct(
                                it.uid,
                                list.getString("name").toString(),
                                list.getString("image_url").toString(),
                                productNameStr,
                                brandNameStr,
                                productImageUri,
                                priceStr,
                                quantityStr,
                                quantityTypeStr,
                                descriptionStr,
                                productTypeStr,
                                keywordsStr
                            )
                            Toast.makeText(requireContext(), "Your product listed successfully, you can check it in the shop section", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                        }
                    }
                }
            }
        }
    }

}