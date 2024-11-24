package com.te.celer.main_files.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.te.celer.R
import com.te.celer.auth.AuthenticationActivity
import com.te.celer.db.AuthViewModel
import com.te.celer.db.DBViewModel
import com.te.celer.db.Response
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.te.celer.db.LocalStorage
import com.te.celer.main_files.MainActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class Account : Fragment() {

    private lateinit var nameAccount: TextView
    private lateinit var phoneAccount: TextView
    private lateinit var cardIdAccount: TextView
//    private lateinit var viewWallet: LinearLayout
//    private lateinit var viewQR: LinearLayout
    private lateinit var viewHistory: LinearLayout
    private lateinit var viewPaymentRecords: LinearLayout
    private lateinit var viewAbout: LinearLayout
    private lateinit var signOut: LinearLayout
    private lateinit var profileImg: CircleImageView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var errorText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var pinText: TextView
//    private lateinit var managePIN: LinearLayout
    private lateinit var verifyIcon: ImageView
    private lateinit var loaderAccount: LottieAnimationView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var uploadImage: FloatingActionButton
//    private lateinit var myUser: FirebaseUser
    private lateinit var uid: String
    private lateinit var imgUrl: String
    private val localStorage = LocalStorage()

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(4, 4)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setOutputCompressQuality(50)
                .getIntent(requireActivity())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dbViewModel = ViewModelProvider(this)[DBViewModel::class.java]

        nameAccount = view.findViewById(R.id.name_account)
        phoneAccount = view.findViewById(R.id.phone_account)
        cardIdAccount = view.findViewById(R.id.cardId_account)
//        viewWallet = view.findViewById(R.id.wallet_account)
//        viewQR = view.findViewById(R.id.qr_account)
        viewHistory = view.findViewById(R.id.history_account)
        viewPaymentRecords = view.findViewById(R.id.payments_account)
        viewAbout = view.findViewById(R.id.about_account)
        signOut = view.findViewById(R.id.sign_out_account)
        profileImg = view.findViewById(R.id.profileImage_account)
        whiteView = view.findViewById(R.id.whiteView_account)
        loaderAccount = view.findViewById(R.id.loader_account)
        mainLayout = view.findViewById(R.id.main_layout_account)
        errorText = view.findViewById(R.id.error_text_account)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_account)
        uploadImage = view.findViewById(R.id.upload_image)
        pinText = view.findViewById(R.id.pin_account)
//        managePIN = view.findViewById(R.id.managePIN_account)
        verifyIcon = view.findViewById(R.id.verify_icon)

        loadData(view)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                loaderAccount.visibility = View.VISIBLE
                whiteView.visibility = View.VISIBLE
                uploadImage.isClickable = false
                imageUploadToStorage(uri, view)
//                replaceImage(uri, view)
            } else {
                Toast.makeText(requireContext(), "No image is uploaded", Toast.LENGTH_SHORT).show()
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            whiteView.visibility = View.VISIBLE
            loaderAccount.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            loadData(view)

        }

        uploadImage.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

//        viewWallet.setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.nav_wallet, null, navBuilder.build())
//        }

        viewHistory.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_history_, null, navBuilder.build())
        }

//        viewQR.setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.nav_receive, null, navBuilder.build())
//        }

        viewAbout.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_about)
        }

        viewPaymentRecords.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_pending_payments)
        }

        signOut.setOnClickListener {
            localStorage.removeAllData(requireContext())
            authViewModel.logout()
            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            requireActivity().finish()
        }

//        managePIN.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("phone", phoneAccount.text.toString())
//            Navigation.findNavController(view).navigate(R.id.nav_manage_pin, bundle)
//        }

        return view
    }

    private fun imageUploadToStorage(uri: Uri, view: View) {
        loaderAccount.visibility = View.VISIBLE
        whiteView.visibility = View.VISIBLE
        dbViewModel.uploadImageToStorage(uri, uid)
        dbViewModel.dbResponse.observe(this.viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    updateLocalStorage(uid)
                }
                is Response.Failure -> {
                    loaderAccount.visibility = View.GONE
                    whiteView.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun replaceImage(uri: Uri, view: View) {
//        loaderAccount.visibility = View.VISIBLE
//        whiteView.visibility = View.VISIBLE
//        val newImageByteArray: ByteArray = uriToByteArray(requireContext(), uri)
//        val newImageStream = ByteArrayInputStream(newImageByteArray)
//        dbViewModel.replaceImage(imgUrl, newImageStream)
//        dbViewModel.dbResponse.observe(this.viewLifecycleOwner) {
//            when (it) {
//                is Response.Success -> {
//                    updateLocalStorage(uid)
//                }
//
//                is Response.Failure -> {
//                    loaderAccount.visibility = View.GONE
//                    whiteView.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    @SuppressLint("SetTextI18n")
    private fun loadData(view: View) {

        val userdata = localStorage.getData(requireContext(), "user_data")
        uid = userdata!!["uid"]!!

        if(userdata["user_type"] == "Buyer")
            viewPaymentRecords.visibility = View.GONE

        if (userdata["pin"].isNullOrEmpty()) {
            verifyIcon.visibility = View.GONE
            pinText.text = "PIN : _ _ _ _ (Not set)"
        } else {
            pinText.text = "PIN : * * * *"
            verifyIcon.visibility = View.VISIBLE
        }
        nameAccount.text = userdata["name"]
        phoneAccount.text = "Phone : +91 ${userdata["phone"]}"
        cardIdAccount.text = "Wallet id : ${userdata["card_id"]}"
        Glide.with(view).load(userdata["image_url"]).into(profileImg)
        imgUrl = userdata["image_url"]!!
        mainLayout.visibility = View.VISIBLE
        whiteView.visibility = View.GONE
        loaderAccount.visibility = View.GONE
        uploadImage.isClickable = true
        errorText.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false

//        authViewModel.userdata.observe(viewLifecycleOwner) {
//            if (it != null) {
//                myUser = it
//                dbViewModel.fetchAccountDetails(it.uid)
//                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
//                    if (list.exists()) {
//                        if (list.getString("pin").isNullOrEmpty()) {
//                            verifyIcon.visibility = View.GONE
//                            pinText.text = "PIN : _ _ _ _ (Not set)"
//                        } else {
//                            pinText.text = "PIN : * * * *"
//                            verifyIcon.visibility = View.VISIBLE
//                        }
//                        nameAccount.text = list.getString("name")
//                        phoneAccount.text = "Phone : +91 ${list.getString("phone")}"
//                        cardIdAccount.text = "Wallet id : ${list.getString("card_id")}"
//                        Glide.with(view).load(list.getString("image_url")).into(profileImg)
//                        imgUrl = list.getString("image_url")!!
//                        mainLayout.visibility = View.VISIBLE
//                        whiteView.visibility = View.GONE
//                        loaderAccount.visibility = View.GONE
//                        uploadImage.isClickable = true
//                        errorText.visibility = View.GONE
//                        swipeRefreshLayout.isRefreshing = false
//                    } else {
//                        errorText.visibility = View.VISIBLE
//                        whiteView.visibility = View.GONE
//                        loaderAccount.visibility = View.GONE
//                        mainLayout.visibility = View.GONE
//                        uploadImage.isClickable = true
//                        swipeRefreshLayout.isRefreshing = false
//                    }
//                }
//            }
//        }
    }

    private fun updateLocalStorage(uid: String) {
        dbViewModel.fetchAccountDetails(uid)
        dbViewModel.accDetails.observe(viewLifecycleOwner) { details ->
            val retrieveData = localStorage.getData(requireContext(),"user_data")
            if (retrieveData == null || retrieveData["image_url"] != details.getString("image_url")
                || retrieveData["balance"] != details.getString("balance")
            ) {
                val userdata = mapOf(
                    "uid" to uid,
                    "name" to details.getString("name")!!,
                    "phone" to details.getString("phone")!!,
                    "card_id" to details.getString("card_id")!!,
                    "user_type" to details.getString("user_type")!!,
                    "image_url" to details.getString("image_url")!!,
                    "pin" to details.getString("pin")!!,
                    "qr_code" to details.getString("qr_code")!!,
                    "balance" to details.getString("balance")!!
                )
                localStorage.removeData(requireContext(), "user_data")
                localStorage.saveData(requireContext(), "user_data", userdata)
            }
        }
        loadData(requireView())
    }

    @SuppressLint("Recycle")
    private fun uriToByteArray(context: Context, uri: Uri): ByteArray {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream?.read(buffer).also { bytesRead = it ?: 0 } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        return outputStream.toByteArray()
    }

}
