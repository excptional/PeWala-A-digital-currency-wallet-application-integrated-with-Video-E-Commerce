package com.te.celer.main_files.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.te.celer.R
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.db.LocalStorage
import com.te.celer.db.Response

class SellerDocumentation : Fragment() {

    private val validPANPattern by lazy { "[A-Z]{5}[0-9]{4}[A-Z]{1}" }
    private val validGSTINPattern by lazy { "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9A-Za-z]{1}$" }
    private val REQUEST_PICK_DOCUMENT = 1
    private val REQUEST_PERMISSION = 2
    private lateinit var panET: AppCompatEditText
    private lateinit var gstET: AppCompatEditText
    private lateinit var backBtn: ImageView
    private lateinit var beforeUploadDoc: CardView
    private lateinit var afterUploadDoc: CardView
    private lateinit var submitBtn: CardView
    private var doc_uri: Uri? = null
    private lateinit var whiteView: View
    private lateinit var loader: LottieAnimationView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var uid: String
    private val localStorage = LocalStorage()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_seller_documentation, container, false)

        requireActivity().window.statusBarColor = Color.WHITE

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        panET = view.findViewById(R.id.panNo_seller_doc)
        gstET = view.findViewById(R.id.gst_seller_doc)
        beforeUploadDoc = view.findViewById(R.id.before_upload_seller_doc)
        afterUploadDoc = view.findViewById(R.id.after_upload_seller_doc)
        backBtn = view.findViewById(R.id.back_btn_seller_doc)
        submitBtn = view.findViewById(R.id.submit_btn_seller_doc)
        whiteView = view.findViewById(R.id.whiteView_seller_doc)
        loader = view.findViewById(R.id.loader_seller_doc)

        loadData()

        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        beforeUploadDoc.setOnClickListener {
//            requestStoragePermission()
            openDocumentPicker()
        }

        afterUploadDoc.setOnClickListener {
//            requestStoragePermission()
            openDocumentPicker()
        }

        submitBtn.setOnClickListener {
            submitBtn.isClickable = false
            whiteView.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
            submit(view)
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun submit(view: View) {
        val pan = panET.text.toString()
        val gstin = gstET.text.toString()
        var isAlright = true
        var msg = ""
        if(!pan.matches(validPANPattern.toRegex())) {
            msg = "Enter valid PAN No"
            isAlright = false
            panET.error = msg
        }
        if(!gstin.matches(validGSTINPattern.toRegex())) {
            msg = "Enter valid GST No"
            isAlright = false
            gstET.error = msg
        }
        if(gstin.substring(2, 12) != pan) {
            msg = "Your PAN No and GSTIN is not "
            isAlright = false
            gstET.error = msg
        }
        if(doc_uri == null) {
            msg = "Enter a copy of Trade license"
            isAlright = false
        }

        if (!isAlright) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            whiteView.visibility = View.GONE
            loader.visibility = View.GONE
            submitBtn.isClickable = true
        } else {
            dbViewModel.uploadSellerDoc(pan, gstin, doc_uri!!, uid)
            dbViewModel.dbResponse.observe(viewLifecycleOwner) {
                when(it) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(requireContext(), "Verification on processing", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }
                    is Response.Failure -> {
                        Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        submitBtn.isClickable = true
                    }
                }
            }
        }
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        startActivityForResult(intent, REQUEST_PICK_DOCUMENT)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == REQUEST_PICK_DOCUMENT && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                doc_uri = resultData.data
                beforeUploadDoc.visibility = View.GONE
                afterUploadDoc.visibility = View.VISIBLE
            }
        }
    }

    private fun requestStoragePermission() {
        val permissions = arrayOf(READ_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_PERMISSION)
        } else {
            openDocumentPicker()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDocumentPicker()
            } else {
                requestStoragePermission()
            }
        }
    }

    private fun loadData() {
        val userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!
    }

}