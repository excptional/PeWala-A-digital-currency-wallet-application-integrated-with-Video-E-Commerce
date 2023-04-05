package com.example.trigeredgedigitalcurrencyproject.main_files.fragments

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
import com.example.trigeredgedigitalcurrencyproject.R
import com.example.trigeredgedigitalcurrencyproject.auth.AuthenticationActivity
import com.example.trigeredgedigitalcurrencyproject.db.AuthViewModel
import com.example.trigeredgedigitalcurrencyproject.db.DBViewModel
import com.example.trigeredgedigitalcurrencyproject.db.Response
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

class Account : Fragment() {

    private lateinit var nameAccount: TextView
    private lateinit var phoneAccount: TextView
    private lateinit var cardIdAccount: TextView
    private lateinit var viewWallet: LinearLayout
    private lateinit var viewQR: LinearLayout
    private lateinit var viewHistory: LinearLayout
    private lateinit var viewAbout: LinearLayout
    private lateinit var signOut: LinearLayout
    private lateinit var profileImg: CircleImageView
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbViewModel: DBViewModel
    private lateinit var whiteView: View
    private lateinit var errorText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var pinText: TextView
    private lateinit var managePIN: LinearLayout
    private lateinit var verifyIcon: ImageView
    private lateinit var loaderAccount: LottieAnimationView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var uploadImage: FloatingActionButton
    private lateinit var myUser: FirebaseUser

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
        viewWallet = view.findViewById(R.id.wallet_account)
        viewQR = view.findViewById(R.id.qr_account)
        viewHistory = view.findViewById(R.id.history_account)
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
        managePIN = view.findViewById(R.id.managePIN_account)
        verifyIcon = view.findViewById(R.id.verify_icon)

        loadData(view)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                loaderAccount.visibility = View.VISIBLE
                whiteView.visibility = View.VISIBLE
                uploadImage.isClickable = false
                imageUploadToStorage(uri, view)
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

        viewWallet.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_wallet, null, navBuilder.build())
        }

        viewHistory.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_history, null, navBuilder.build())
        }

        viewQR.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.nav_receive, null, navBuilder.build())
        }

        viewAbout.setOnClickListener {

        }

        signOut.setOnClickListener {
            authViewModel.logout()
            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            requireActivity().finish()
        }

        managePIN.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("phone", phoneAccount.text.toString())
            Navigation.findNavController(view).navigate(R.id.nav_manage_pin, bundle)
        }

        return view
    }

    private fun imageUploadToStorage(uri: Uri, view: View) {
        dbViewModel.uploadImageToStorage(uri, myUser)
        dbViewModel.dbResponse.observe(this.viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    loadData(view)
                    Toast.makeText(requireContext(), "Image changed successfully", Toast.LENGTH_SHORT).show()
                    loaderAccount.visibility = View.GONE
                    whiteView.visibility = View.GONE
                }
                is Response.Failure -> {
                    loaderAccount.visibility = View.GONE
                    whiteView.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMassage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(view: View) {
        authViewModel.userdata.observe(viewLifecycleOwner) {
            if(it != null) {
                myUser = it
                dbViewModel.fetchAccountDetails(it.uid)
                dbViewModel.accDetails.observe(viewLifecycleOwner) { list ->
                    if(list.isNotEmpty()) {
                        if(list[6] == ""){
                            verifyIcon.visibility = View.GONE
                            pinText.text = "PIN : _ _ _ _ (Not set)"
                        } else {
                            pinText.text = "PIN : * * * *"
                            verifyIcon.visibility = View.VISIBLE
                        }
                        nameAccount.text = list[0]
                        phoneAccount.text = "Phone : +91 ${list[1]}"
                        cardIdAccount.text = "Wallet id : ${list[2]}"
                        Glide.with(view).load(list[3]).into(profileImg)
                        mainLayout.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderAccount.visibility = View.GONE
                        uploadImage.isClickable = true
                        errorText.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    } else {
                        errorText.visibility = View.VISIBLE
                        whiteView.visibility = View.GONE
                        loaderAccount.visibility = View.GONE
                        mainLayout.visibility = View.GONE
                        uploadImage.isClickable = true
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

}
